package ru.levinov.modules.impl.movement;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MoverType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMove;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.world.InventoryUtil;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author levin1337
 * @since 07.06.2023
 */
@FunctionAnnotation(name = "Flight", type = Type.Movement,desc = "Флай",
        keywords = {"Fly", "Флай"})
public class FlightFunction extends Function {
    private final ModeSetting flMode = new ModeSetting("Flight Mode",
            "Motion",
            "Motion", "Glide", "Трезубец","Трезубец2","Collision","HoweLand","Damage","Elytra","Test");

    private final SliderSetting motion
            = new SliderSetting("Speed XZ",
            1F,
            0F,
            8F,
            0.1F).setVisible(() -> !flMode.is("Трезубец"));

    private final SliderSetting motionY
            = new SliderSetting("Speed Y",
            1F,
            0F,
            8F,
            0.1F).setVisible(() -> !flMode.is("Трезубец"));

    private final BooleanOption setPitch = new BooleanOption("Поворачивать голову", false).setVisible(() -> flMode.is("Трезубец"));
    private int originalSlot = -1;
    public long lastUseTridantTime = 0;

    public CopyOnWriteArrayList<IPacket<?>> packets = new CopyOnWriteArrayList<>();
    private final TimerUtil timerHelper = new TimerUtil();

    public FlightFunction() {
        addSettings(flMode, motion, motionY, setPitch);
    }

    @Override
    public void onEvent(final Event event) {

        if (event instanceof EventUpdate) {

            handleFlyMode();
        }
    }

    /**
     * Обрабатывает выбранный режим полета.
     */
    private void handleFlyMode() {
        switch (flMode.get()) {
            case "Motion" -> handleMotionFly();
            case "Glide" -> handleGlideFly();
            case "Трезубец" -> handleTridentFly();
            case "Collision" -> Collision();
            case "HoweLand" -> howe();
            case "Damage" -> Damage();
            case "Elytra" -> Elytra();
            case "Test" -> govno1337();
            case "Трезубец2" -> tunderhack1337();
        }
    }
    private static int currentTick;
    private void tunderhack1337() {
        if (currentTick >= 20) {
            currentTick = 0;
            assert mc.player != null;
            float f = mc.player.getYaw(90);
            float g = mc.player.getPitch(90);
            float h = -MathHelper.sin(f * (float) (Math.PI / 180.0)) * MathHelper.cos(g * (float) (Math.PI / 180.0));
            float k = -MathHelper.sin(g * (float) (Math.PI / 180.0));
            float l = MathHelper.cos(f * (float) (Math.PI / 180.0)) * MathHelper.cos(g * (float) (Math.PI / 180.0));
            float m = MathHelper.sqrt(h * h + k * k + l * l);
            float n = 3.0F;
            h *= n / m;
            k *= n / m;
            l *= n / m;
            mc.player.addVelocity(h, k, l);
            if (mc.player.isOnGround()) {
                mc.player.move(MoverType.SELF, new Vector3d(0.0, 1.1999999F, 0.0));
            }
        } else {
            currentTick++;
        }
    }



    private final TimerUtil timerUtil1 = new TimerUtil();
    private final TimerUtil timerUtil = new TimerUtil();
    private void govno1337() {
            int timeSwap = 250;

            for (int i = 0; i < 9; ++i) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA && mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 0.01, mc.player.getPosZ())).getBlock() == Blocks.AIR && !mc.player.isOnGround() && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isElytraFlying()) {
                    if (this.timerUtil1.hasTimeElapsed((long) timeSwap)) {
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                        mc.player.startFallFlying();

                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        final float motionY = this.motionY.getValue().floatValue();
                        final float speed = this.motion.getValue().floatValue();

                        mc.player.motion.y = 0;

                        if (mc.gameSettings.keyBindJump.pressed) {
                            mc.player.motion.y = motionY;
                        } else if (mc.player.isSneaking()) {
                            mc.player.motion.y = -motionY;
                        }

                        MoveUtil.setMotion(speed);
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                        this.timerUtil1.reset();
                    }
                    boolean startFireWork = true;

                }
            }
    }



    private void Elytra() {
        if (mc.player.isElytraFlying()) {
            mc.player.setOnGround(false);
            mc.player.setVelocity(0.0, 0.0, 0.0);
            if (mc.gameSettings.keyBindSneak.isKeyDown())
                mc.player.motion.y = -motion.getValue().floatValue();
            if (mc.gameSettings.keyBindJump.isKeyDown())
                mc.player.motion.y = motion.getValue().floatValue();
            if (MoveUtil.isMoving()) {
                MoveUtil.setSpeed(motion.getValue().floatValue());
            }
        }
    }


    public static int getElytra() {
        Iterator var0 = mc.player.getArmorInventoryList().iterator();

        while(var0.hasNext()) {
            ItemStack stack = (ItemStack)var0.next();
            if (stack.getItem() == Items.ELYTRA) {
                return -2;
            }
        }

        int slot = -1;

        for(int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == Items.ELYTRA) {
                slot = i;
                break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }

        return slot;
    }

    private void Damage() {
        if (mc.player.hurtTime > 0.1f) {
            MoveUtil.setMotion(motion.getValue().floatValue());
        }
    }
    private void howe() {
        final float motionY = this.motionY.getValue().floatValue();
        final float speed = this.motion.getValue().floatValue();
        if (mc.player.isElytraFlying()) {
            mc.player.motion.y = 0;
            if (mc.gameSettings.keyBindJump.pressed) {
                mc.player.motion.y = motionY;
            } else if (mc.player.isSneaking()) {
                mc.player.motion.y = -motionY;
            }
            MoveUtil.setMotion(speed);
        }
        for(int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA && mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 0.01, mc.player.getPosZ())).getBlock() == Blocks.AIR && !mc.player.isOnGround() && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isElytraFlying()) {
                mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                mc.player.startFallFlying();
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                this.timerHelper.reset();
            }
        }
    }
    private void Collision() {
        if (mc.player.fallDistance > 3.0F) {
            mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));

            for(int i = 0; i < 19; ++i) {
                mc.getConnection().sendPacket(new CPlayerPacket.PositionRotationPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), mc.player.rotationYaw, mc.player.rotationPitch, true));

                for(int j = 0; j < 19; ++j) {
                    mc.getConnection().sendPacket(new CPlayerPacket.PositionRotationPacket(mc.player.getPosX(), mc.player.getPosY() + 10, mc.player.getPosZ(), mc.player.rotationYaw, mc.player.rotationPitch, false));
                }

                mc.getConnection().sendPacket(new CPlayerPacket.PositionRotationPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), mc.player.rotationYaw, mc.player.rotationPitch, true));
            }

            mc.getConnection().sendPacket(new CAnimateHandPacket(Hand.OFF_HAND));
            if (!mc.player.isSprinting()) {
                mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            }

            mc.player.fallDistance = 0.0F;
        }
    }
    private void handleTridentFly() {
        final int slot = InventoryUtil.getTrident();
        if ((mc.player.isInWater() || mc.world.getRainStrength(1) == 1)
                && EnchantmentHelper.getRiptideModifier(mc.player.getHeldItemMainhand()) > 0) {
            if (slot != -1) {
                originalSlot = mc.player.inventory.currentItem;
                if (mc.gameSettings.keyBindUseItem.pressed && setPitch.get()) {
                    mc.player.rotationPitch = -90;
                }
                mc.gameSettings.keyBindUseItem.setPressed(mc.player.ticksExisted % 20 < 15);
            }
        }
    }

    /**
     * Обрабатывает режим полета "Motion".
     */
    private void handleMotionFly() {
        final float motionY = this.motionY.getValue().floatValue();
        final float speed = this.motion.getValue().floatValue();

        if (mc.gameSettings.keyBindSprint.isKeyDown()) {
            MoveUtil.setMotion(speed);
        } else {
            MoveUtil.setMotion(speed);
        }
        mc.player.motion.y = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motion.y = motionY;
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motion.y = -motionY;
        }
    }

    /**
     * Обрабатывает режим полета "Glide".
     */
    private void handleGlideFly() {
        if (mc.player.isOnGround()) {
            mc.player.motion.y = 0.42;  // Устанавливаем вертикальную скорость при нахождении на земле
        } else {
            mc.player.setVelocity(0, -0.003, 0);  // Устанавливаем вертикальную скорость при полете
            MoveUtil.setMotion(motion.getValue().floatValue());  // Устанавливаем горизонтальную скорость движения
        }
    }

    @Override
    protected void onDisable() {
        mc.timer.timerSpeed = 1;
        if (flMode.is("Трезубец")) {
            if (originalSlot != -1) {
                mc.player.inventory.currentItem = originalSlot;
                originalSlot = -1;
            }
            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                mc.gameSettings.keyBindUseItem.setPressed(false);
            }
        }
        super.onDisable();
    }
}
