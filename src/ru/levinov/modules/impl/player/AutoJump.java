package ru.levinov.modules.impl.player;

import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.misc.TimerUtil;

@FunctionAnnotation(name = "AutoJump", type = Type.Player,desc = "Авто прыжок")
public class AutoJump extends Function {

    private final SliderSetting time = new SliderSetting("Время", 100f, 10f, 5000f, 10F);

    private final TimerUtil timerHelper = new TimerUtil();

    public AutoJump() {
        addSettings(time);
    }
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            if (timerHelper.hasTimeElapsed((long) time.getValue().floatValue())) {
                mc.player.jump();
                timerHelper.reset();
            }
        }
    }
}
