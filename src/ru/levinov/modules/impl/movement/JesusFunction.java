package ru.levinov.modules.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.movement.MoveUtil;

/**
 * @author levin1337
 * @since 09.06.2023
 */
@FunctionAnnotation(name = "Jesus", type = Type.Movement,desc = "Передвижение по воде")
public class JesusFunction extends Function {

    private ModeSetting jesusMode = new ModeSetting("Режим", "Matrix Solid", "Matrix Solid", "Matrix Zoom");

    private SliderSetting zoomSpeed = new SliderSetting("Скорость", 0.5F, 0.1F, 10.0F, 0.1F);
    private BooleanOption noJump = new BooleanOption("Не приземляться", false).setVisible(() -> jesusMode.is("Matrix Solid"));

    private int ticks;

    public JesusFunction() {
        addSettings(jesusMode, zoomSpeed, noJump);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            handleEventMove(e);
        }
    }

    /**
     * Переделать
     */
    private void handleEventMove(EventMotion e) {
        if (jesusMode.is("Matrix Solid")) {
            handleWaterAndAirMovement(e);
        }
        if (jesusMode.is("Matrix Zoom")) {
            if (mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() + 1, mc.player.getPosZ())).getBlock() == Blocks.WATER) {
                mc.player.motion.y = 0.18f;
            } else if (mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() + 0.0000001, mc.player.getPosZ())).getBlock() == Blocks.WATER) {
                mc.player.fallDistance = 0.0f;
                mc.player.motion.x = 0.0;
                mc.player.motion.y = 0.06f;
                mc.player.jumpMovementFactor = zoomSpeed.getValue().floatValue();
                mc.player.motion.z = 0.0;
            }
        }
    }

    private void handleWaterAndAirMovement(EventMotion motion) {
        handleWaterMovement();
        handleAirMovement(motion);
    }

    private void handleWaterMovement() {
        BlockPos playerPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY() + 0.008D, mc.player.getPosZ());
        Block playerBlock = mc.world.getBlockState(playerPos).getBlock();
        if (playerBlock == Blocks.WATER && !mc.player.isOnGround()) {
            boolean isUp = mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() + 0.03D, mc.player.getPosZ())).getBlock() == Blocks.WATER;
            mc.player.jumpMovementFactor = 0.0F;
            float yPort = MoveUtil.getMotion() > 0.1D ? 0.02F : 0.032F;
            mc.player.setVelocity(mc.player.motion.x, (double) mc.player.fallDistance < 3.5D ? (double) (isUp ? yPort : -yPort) : -0.1D, mc.player.motion.z);
        }
    }

    private void handleAirMovement(EventMotion motion) {
        double posY = mc.player.getPosY();
        if (posY > (double) ((int) posY) + 0.89D && posY <= (double) ((int) posY + 1) || (double) mc.player.fallDistance > 3.5D) {
            mc.player.getPositionVec().y = ((double) ((int) posY + 1) + 1.0E-45D);
            if (!mc.player.isInWater()) {
                BlockPos waterBlockPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 0.1D, mc.player.getPosZ());
                Block waterBlock = mc.world.getBlockState(waterBlockPos).getBlock();
                if (waterBlock == Blocks.WATER) {
                    movementInWater(motion);
                }
            }
        }
    }

    private void movementInWater(EventMotion motion) {
        motion.setOnGround(false);
        handleCollisionJump();
        if (ticks == 1) {
            MoveUtil.setMotion(1.1f);
            ticks = 0;
        } else {
            ticks = 1;
        }
    }

    private void handleCollisionJump() {
        if (this.mc.player.collidedHorizontally && !noJump.get()) {
            this.mc.player.motion.y = 0.2D;
            mc.player.motion.x *= 0.0D;
            mc.player.motion.z *= 0.0D;
        }
    }
}
