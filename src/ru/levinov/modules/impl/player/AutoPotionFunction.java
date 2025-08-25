package ru.levinov.modules.impl.player;

import java.util.Iterator;
import java.util.function.Supplier;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Hand;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.world.PotionUtil;

@FunctionAnnotation(
        name = "AutoPotion",
        type = Type.Player
)
public class AutoPotionFunction extends Function {
    private static MultiBoxSetting potions = new MultiBoxSetting("Бафать", new BooleanOption[]{new BooleanOption("Силу", true), new BooleanOption("Скорость", true), new BooleanOption("Огнестойкость", true)});
    private BooleanOption autoDisable = new BooleanOption("Авто выключение", false);
    private BooleanOption onlyGround = new BooleanOption("Только на земле", false);
    private BooleanOption onlyPvP = new BooleanOption("Только в PVP", false);
    public boolean isActive;
    private int selectedSlot;
    private float previousPitch;
    private TimerUtil time = new TimerUtil();
    private PotionUtil potionUtil = new PotionUtil();
    public boolean isActivePotion;
    private final TimerUtil timerUtil = new TimerUtil();

    public AutoPotionFunction() {
        this.addSettings(new Setting[]{potions, this.onlyPvP, this.autoDisable, this.onlyGround});
    }

    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (!this.isActivePotion && !mc.player.isOnGround() && this.onlyGround.get()) {
                return;
            }

            int var4;
            if (this.isActive() && this.shouldUsePotion()) {
                PotionType[] var2 = AutoPotionFunction.PotionType.values();
                int var3 = var2.length;

                for(var4 = 0; var4 < var3; ++var4) {
                    PotionType potionType = var2[var4];
                    this.isActivePotion = potionType.isEnabled();
                }
            } else {
                this.isActivePotion = false;
            }

            if (this.isActive() && this.shouldUsePotion() && this.previousPitch == mc.player.getLastReportedPitch()) {
                int oldItem = mc.player.inventory.currentItem;
                this.selectedSlot = -1;
                PotionType[] var11 = AutoPotionFunction.PotionType.values();
                var4 = var11.length;

                for(int var13 = 0; var13 < var4; ++var13) {
                    PotionType potionType = var11[var13];
                    if (potionType.isEnabled()) {
                        int slot = this.findPotionSlot(potionType);
                        if (this.selectedSlot == -1) {
                            this.selectedSlot = slot;
                        }

                        this.isActive = true;
                    }
                }

                if (this.selectedSlot > 8) {
                    mc.playerController.pickItem(this.selectedSlot);
                }

                mc.player.connection.sendPacket(new CHeldItemChangePacket(oldItem));
            }

            if (this.time.hasTimeElapsed(500L)) {
                try {
                    this.reset();
                    this.selectedSlot = -2;
                } catch (Exception var8) {
                }
            }

            this.potionUtil.changeItemSlot(this.selectedSlot == -2);
            if (this.autoDisable.get() && this.isActive && this.selectedSlot == -2) {
                this.setState(false);
                this.isActive = false;
            }
        }

        if (event instanceof EventMotion e) {
            if (!this.isActive() || !this.shouldUsePotion()) {
                this.isActive = false;
                if (this.onlyGround.get() && !mc.player.isOnGround()) {
                    return;
                } else {
                    if (this.autoDisable.get()) {
                        this.toggle();
                    }

                    return;
                }
            }

            float[] angles = new float[]{mc.player.rotationYaw, 90.0F};
            this.previousPitch = 90.0F;
            e.setYaw(angles[0]);
            e.setPitch(this.previousPitch);
            mc.player.rotationPitchHead = this.previousPitch;
            mc.player.rotationYawHead = angles[0];
            mc.player.renderYawOffset = angles[0];
        }

    }

    private boolean shouldUsePotion() {
        return !this.onlyPvP.get() || ClientUtil.isPvP();
    }

    private void reset() {
        PotionType[] var1 = AutoPotionFunction.PotionType.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            PotionType potionType = var1[var3];
            if ((Boolean)potionType.isPotionSettingEnabled().get()) {
                potionType.setEnabled(this.isPotionActive(potionType));
            }
        }

    }

    private int findPotionSlot(PotionType type) {
        int hbSlot = this.getPotionIndexHb(type.getPotionId());
        if (hbSlot != -1) {
            this.potionUtil.setPreviousSlot(mc.player.inventory.currentItem);
            mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            PotionUtil.useItem(Hand.MAIN_HAND);
            type.setEnabled(false);
            this.time.reset();
            return hbSlot;
        } else {
            int invSlot = this.getPotionIndexInv(type.getPotionId());
            if (invSlot != -1) {
                this.potionUtil.setPreviousSlot(mc.player.inventory.currentItem);
                mc.playerController.pickItem(invSlot);
                PotionUtil.useItem(Hand.MAIN_HAND);
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                type.setEnabled(false);
                this.time.reset();
                return invSlot;
            } else {
                return -1;
            }
        }
    }

    public boolean isActive() {
        PotionType[] var1 = AutoPotionFunction.PotionType.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            PotionType potionType = var1[var3];
            if ((Boolean)potionType.isPotionSettingEnabled().get() && potionType.isEnabled()) {
                return true;
            }
        }

        return false;
    }

    private boolean isPotionActive(PotionType type) {
        if (mc.player.isPotionActive(type.getPotion())) {
            this.isActive = false;
            return false;
        } else {
            return this.getPotionIndexInv(type.getPotionId()) != -1 || this.getPotionIndexHb(type.getPotionId()) != -1;
        }
    }

    private int getPotionIndexHb(int id) {
        for(int i = 0; i < 9; ++i) {
            Iterator var3 = PotionUtils.getEffectsFromStack(mc.player.inventory.getStackInSlot(i)).iterator();

            while(var3.hasNext()) {
                EffectInstance potion = (EffectInstance)var3.next();
                if (potion.getPotion() == Effect.get(id) && mc.player.inventory.getStackInSlot(i).getItem() == Items.SPLASH_POTION) {
                    return i;
                }
            }
        }

        return -1;
    }

    private int getPotionIndexInv(int id) {
        for(int i = 9; i < 36; ++i) {
            Iterator var3 = PotionUtils.getEffectsFromStack(mc.player.inventory.getStackInSlot(i)).iterator();

            while(var3.hasNext()) {
                EffectInstance potion = (EffectInstance)var3.next();
                if (potion.getPotion() == Effect.get(id) && mc.player.inventory.getStackInSlot(i).getItem() == Items.SPLASH_POTION) {
                    return i;
                }
            }
        }

        return -1;
    }

    protected void onDisable() {
        this.isActive = false;
        super.onDisable();
    }

    static enum PotionType {
        STRENGHT(Effects.STRENGTH, 5, () -> {
            return AutoPotionFunction.potions.get(0);
        }),
        SPEED(Effects.SPEED, 1, () -> {
            return AutoPotionFunction.potions.get(1);
        }),
        FIRE_RESIST(Effects.STRENGTH, 12, () -> {
            return AutoPotionFunction.potions.get(2);
        });

        private final Effect potion;
        private final int potionId;
        private final Supplier<Boolean> potionSetting;
        private boolean enabled;

        private PotionType(Effect potion, int potionId, Supplier potionSetting) {
            this.potion = potion;
            this.potionId = potionId;
            this.potionSetting = potionSetting;
        }

        public Effect getPotion() {
            return this.potion;
        }

        public int getPotionId() {
            return this.potionId;
        }

        public Supplier<Boolean> isPotionSettingEnabled() {
            return this.potionSetting;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean var1) {
            this.enabled = var1;
        }
    }
}
