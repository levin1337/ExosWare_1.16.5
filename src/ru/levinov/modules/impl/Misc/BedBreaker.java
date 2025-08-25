package ru.levinov.modules.impl.Misc;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.misc.TimerUtil;

@FunctionAnnotation(name = "BedBreaker", type = Type.Misc, desc = "Ломание кровати через блоки")
public class BedBreaker extends Function {

    private final TimerUtil timerHelper = new TimerUtil();

    public BedBreaker() {
        addSettings();
    }

    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            BlockPos nearestBed = findNearestBed(mc.player.getPosition(), 4);
            if (nearestBed != null) {
                // Установка ротации игрока
                float[] rotations = rots(nearestBed);


                // Ломаем кровать по таймеру
                if (timerHelper.hasTimeElapsed((long) mc.player.getDigSpeed(mc.world.getBlockState(nearestBed)))) {
                    mc.player.swingArm(Hand.MAIN_HAND);
                    mc.playerController.onPlayerDamageBlock(nearestBed, mc.player.getHorizontalFacing());
                    timerHelper.reset();
                }
            }
        }
    }

    private BlockPos findNearestBed(BlockPos position, int radius) {
        for (int x = position.getX() - radius; x <= position.getX() + radius; x++) {
            for (int y = position.getY() - radius; y <= position.getY() + radius; y++) {
                for (int z = position.getZ() - radius; z <= position.getZ() + radius; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock() instanceof BedBlock) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    public static float[] rots(BlockPos pos) {
        double x = pos.getX() + 0.5 - mc.player.getPosX();
        double z = pos.getZ() + 0.5 - mc.player.getPosZ();
        double y = pos.getY() - (mc.player.getPosY() + mc.player.getEyeHeight());
        double u = MathHelper.sqrt(x * x + z * z);
        float yaw = (float) (MathHelper.atan2(z, x) * (180 / Math.PI)) - 90.0f;
        float pitch = (float) (MathHelper.atan2(y, u) * (180 / Math.PI));
        return new float[]{yaw, pitch};
    }
}
