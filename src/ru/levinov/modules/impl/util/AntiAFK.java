package ru.levinov.modules.impl.util;

import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;

/**
 * @author levin1337
 * @since 12.06.2023
 */
@FunctionAnnotation(name = "AntiAFK", type = Type.Util,desc = "Убирает кик за афк")
public class AntiAFK extends Function {

    private final TimerUtil timerUtil = new TimerUtil();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {

            if (!MoveUtil.isMoving()) {
                if (timerUtil.hasTimeElapsed(15000)) {
                    mc.player.sendChatMessage("/kirkapremiumkaktok");
                    timerUtil.reset();
                }
            } else {
                timerUtil.reset();
            }
        }
    }
}
