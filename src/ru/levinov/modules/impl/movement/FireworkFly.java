package ru.levinov.modules.impl.movement;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.world.InventoryUtil;

@FunctionAnnotation(
        name = "FireworkFly",
        type = Type.Movement,
        desc = "??????? ??? ????????? ??????????",
        keywords = {"catfly","???????"}
)
public class FireworkFly extends Function {

    public final ModeSetting mode = new ModeSetting("???", "?????", "?????", "??????");
    private ItemStack oldStack = null;
    private final TimerUtil timerUtil = new TimerUtil();
    private final TimerUtil timerUtil1 = new TimerUtil();
    private final SliderSetting timerStartFireWork = new SliderSetting("?????? ??????????", 400.0F, 50.0F, 1500.0F, 1.0F);
    private final BooleanOption onlyGrimBypass = new BooleanOption("???????? ?????? ????", false);
    private final BooleanOption noFireWorkifEat = new BooleanOption("?????????? ??? ? ?????? ????", false);

    public FireworkFly() {
        this.addSettings(new Setting[]{mode,this.timerStartFireWork, this.onlyGrimBypass, this.noFireWorkifEat});
    }

    public void onEvent(Event event) {
        if (mode.is("??????")) {
            if (InventoryUtil.getItemSlot(Items.FIREWORK_ROCKET) != -1 && event instanceof EventUpdate) {
                int timeSwap = 200;
                if (this.onlyGrimBypass.get()) {
                    timeSwap = 0;
                }

                boolean startFireWork = true;
                if (this.noFireWorkifEat.get() && mc.player.getActiveHand() == Hand.MAIN_HAND && mc.player.getHeldItemMainhand().getUseAction() == UseAction.EAT) {
                    startFireWork = false;
                }

                for (int i = 0; i < 9; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA && mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 0.01, mc.player.getPosZ())).getBlock() == Blocks.AIR && !mc.player.isOnGround() && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isElytraFlying()) {
                        if (this.timerUtil1.hasTimeElapsed((long) timeSwap)) {
                            mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                            mc.player.startFallFlying();
                            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                            mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                            this.timerUtil1.reset();
                        }

                        if (this.timerUtil.hasTimeElapsed((long) this.timerStartFireWork.getValue().intValue()) && mc.player.isElytraFlying()) {
                            if (startFireWork) {
                                InventoryUtil.inventorySwapClick(Items.FIREWORK_ROCKET);
                            }

                            this.timerUtil.reset();
                        }
                    }
                }
            }
        }
        if (mode.is("?????")) {
            if (InventoryUtil.getItemSlot(Items.FIREWORK_ROCKET) != -1 && event instanceof EventUpdate) {
                int timeSwap = 100;
                if (onlyGrimBypass.get()) {
                    timeSwap = 560;
                }

                boolean startFireWork = true;
                if (this.noFireWorkifEat.get() && mc.player.getActiveHand() == Hand.MAIN_HAND && mc.player.getHeldItemMainhand().getUseAction() == UseAction.EAT) {
                    startFireWork = false;
                }

                for (int i = 0; i < 9; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA && mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 0.01, mc.player.getPosZ())).getBlock() == Blocks.AIR && !mc.player.isOnGround() && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isElytraFlying()) {
                        if (this.timerUtil1.hasTimeElapsed((long) timeSwap)) {
                            mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                            mc.player.startFallFlying();
                            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                            mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                            this.timerUtil1.reset();
                        }

                        if (this.timerUtil.hasTimeElapsed((long) this.timerStartFireWork.getValue().intValue()) && mc.player.isElytraFlying()) {
                            if (startFireWork) {
                                InventoryUtil.inventorySwapClick(Items.FIREWORK_ROCKET);
                            }

                            this.timerUtil.reset();
                        }
                    }
                }
            }
        }
    }

}