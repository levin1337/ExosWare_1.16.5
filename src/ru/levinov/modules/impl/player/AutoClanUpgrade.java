package ru.levinov.modules.impl.player;

import net.minecraft.block.*;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import net.minecraft.util.math.BlockPos;
import ru.levinov.util.misc.TimerUtil;

@FunctionAnnotation(name = "AutoClanUpgrade", type = Type.Player, desc = "Авто прокачка клана FunTime")
public class AutoClanUpgrade extends Function {
    private final TimerUtil timerHelper = new TimerUtil();
    private final TimerUtil timerHelper2 = new TimerUtil();


    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            for (int x = (int) (mc.player.getPosX() - 2); x <= mc.player.getPosX() + 2; ++x) {
                for (int y = (int) (mc.player.getPosY() - 2); y <= mc.player.getPosY() + 2; ++y) {
                    for (int z = (int) (mc.player.getPosZ() - 2); z <= mc.player.getPosZ() + 2; ++z) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = mc.world.getBlockState(pos);
                        if (state.getBlock() instanceof RedstoneWireBlock) {
                            BlockPos redstonePos = pos;
                            float[] rotation = rots(new Vector3d(redstonePos.getX() + 0.5, redstonePos.getY() + 0.5, redstonePos.getZ() + 0.5));

                            BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(
                                    new Vector3d(redstonePos.getX() + 0.5, redstonePos.getY() + 0.5f, redstonePos.getZ() + 0.5),
                                    Direction.UP,
                                    redstonePos,
                                    false
                            );

                            if (timerHelper.hasTimeElapsed((1))) {
                                mc.player.swingArm(Hand.MAIN_HAND);
                                mc.playerController.onPlayerDamageBlock(redstonePos, mc.player.getHorizontalFacing());
                                timerHelper.reset();
                            }
                        }
                    }
                }
            }
        }
    }

    public static float[] rots(Vector3d vec) {
        double x = vec.x - mc.player.getPosX();
        double y = vec.y - (mc.player.getPosY() + 2);
        double z = vec.z - mc.player.getPosZ();
        double u = MathHelper.sqrt(x * x + z * z);
        float yaw = (float) (MathHelper.atan2(z, x) * 57.29577951308232 - 90.0);
        float pitch = (float) (-MathHelper.atan2(y, u) * 57.29577951308232);
        return new float[]{yaw, pitch};
    }
}
