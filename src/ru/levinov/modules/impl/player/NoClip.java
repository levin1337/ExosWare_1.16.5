package ru.levinov.modules.impl.player;

import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMove;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;

/**
 * @author levin1337
 * @since 25.06.2023
 */
@FunctionAnnotation(name = "NoClip", type = Type.Player,desc = "Перемещение в блоках")
public class NoClip extends Function {
    private final BooleanOption destroyBlocks = new BooleanOption("Ломать блоки", true);
    private final BooleanOption speed = new BooleanOption("Ускорение", false);
    public final SliderSetting speed_value = new SliderSetting("Скорость", 0.15f, 0.10f, 0.50F, 0.01F).setVisible(() -> speed.get());
    private final TimerUtil timerHelper = new TimerUtil();

    public NoClip() {
        this.addSettings(new Setting[]{this.destroyBlocks,speed,speed_value});
    }

    public void onEvent(Event event) {
        if (event instanceof EventMove move) {
            if (this.destroyBlocks.get()) {
                float f = mc.player.rotationYaw * 0.017453292F;
                double speed = 0.5;
                double x = -((double) MathHelper.sin(f) * speed);
                double z = (double)MathHelper.cos(f) * speed;
                if (this.timerHelper.hasTimeElapsed((long)mc.player.getDigSpeed(mc.world.getBlockState(new BlockPos((double)((int)mc.player.getPosX()) + x, (double)((int)mc.player.getPosY()) + 0.4, (double)((int)mc.player.getPosZ()) + z)).getBlock().getDefaultState()))) {
                    mc.player.swingArm(Hand.MAIN_HAND);
                    mc.playerController.onPlayerDamageBlock(new BlockPos(mc.player.getPosX() + x, mc.player.getPosY() + 0.4, mc.player.getPosZ() + z), mc.player.getHorizontalFacing());
                    this.timerHelper.reset();
                }
            }
            if (this.speed.get()) {
                if (!mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isSpectator() && !mc.player.isCreative()) {
                    if (mc.player.getBlockState() != null && MoveUtil.isMoving() && !mc.world.isAirBlock(mc.player.getPosition())) {
                        MoveUtil.setSpeed(speed_value.getValue().floatValue());
                    }
                }
            }

            if (!this.collisionPredict(move.to())) {
                if (move.isCollidedHorizontal()) {
                    move.setIgnoreHorizontalCollision();
                }

                if (move.motion().y > 0.0 || mc.player.isSneaking()) {
                    move.setIgnoreVerticalCollision();
                }

                move.motion().y = Math.min(move.motion().y, 99999.0);
            }
        }

    }

    public boolean collisionPredict(Vector3d to) {
        boolean prevCollision = mc.world.getCollisionShapes(mc.player, mc.player.getBoundingBox().shrink(0.0625)).toList().isEmpty();
        Vector3d backUp = new Vector3d(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
        mc.player.setPosition(to.x, to.y, to.z);
        boolean collision = mc.world.getCollisionShapes(mc.player, mc.player.getBoundingBox().shrink(0.0625)).toList().isEmpty() && prevCollision;
        mc.player.setPosition(backUp.x, backUp.y, backUp.z);
        return collision;
    }
}
