package ru.levinov.modules.impl.movement;

import lombok.val;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Direction;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;

import static net.minecraft.util.math.MathHelper.cos;
import static net.minecraft.util.math.MathHelper.sin;

@FunctionAnnotation(
        name = "Step",
        type = Type.Combat,
        desc = "Быстрое взбирание на блок"
)
public class Step extends Function {
    public final ModeSetting mode = new ModeSetting("Мод", "NCP/AAC", "NCP/AAC", "Custom");


    private TimerUtil timer = new TimerUtil();
    public Step() {
        addSettings(mode);
    }
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion) {
            if (mode.is("NCP/AAC")) {
                if (mc.player.collidedHorizontally) {
                    if (mc.player.isOnGround() && timer.hasTimeElapsed(30)) {

                        fakeJump();
                        mc.player.motion.y += 0.620000001490116;

                        val yaw = MoveUtil.direction();
                        mc.player.motion.x -= sin(yaw) * 0.2;
                        mc.player.motion.z += cos(yaw) * 0.2;
                        timer.reset();
                    }

                    mc.player.setOnGround(true);
                }
            }
        }
    }


    private void fakeJump() {
        if (mc.player != null) {
            return;
        }

        mc.player.isAirBorne = true;
        mc.player.doesEntityNotTriggerPressurePlate();
    }
}