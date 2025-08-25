package ru.levinov.modules.impl.Misc;

import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.MarkerUtils.Mathf;
import ru.levinov.util.math.MathUtil;
import ru.levinov.util.movement.GCDfix;

@FunctionAnnotation(
        name = "AntiAim",
        type = Type.Misc,
        desc = "Крутилочка"
)
public class AntiAim extends Function {
    private final SliderSetting pitch = new SliderSetting("pitch", 2.95F, 1.0F, 10.0F, 0.05F);
    private final SliderSetting spinSpeed = new SliderSetting("Скорость", 2.95F, 1.0F, 10.0F, 0.05F);
    private final BooleanOption hideplayer = new BooleanOption("Чтобы видели игроки", false);
    private final BooleanOption PitchHead = new BooleanOption("PitchHead | голову вниз", true);
    private float previousPitch;
    public float rot = 0.0F;

    public AntiAim() {
        this.addSettings(new Setting[]{this.spinSpeed, this.hideplayer, this.PitchHead});
    }

    public void onEvent(Event event) {
        if (event instanceof EventMotion eventMotion) {
            float speed = spinSpeed.getValue().floatValue() * 10.0F;
            if (hideplayer.get()) {
                eventMotion.setPitch(pitch.getValue().floatValue());
            }

            float yaw = GCDfix.getFixedRotation((float)(Math.floor((double)aim(speed)) + (double) Mathf.randomizeFloat(-4.0F, 1.0F)));
            if (hideplayer.get()) {
                eventMotion.setYaw(GCDfix.getFixedRotation(yaw));
            }

            mc.player.renderYawOffset = yaw;
            mc.player.rotationYawHead = yaw;

            if (PitchHead.get()) {
                previousPitch = 180.0F;
                mc.player.rotationPitchHead = previousPitch;
            }
        }

    }

    public float aim(float rotation) {
        rot += rotation;
        return rot;
    }
}
