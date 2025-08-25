package ru.levinov.modules.impl.player;

import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.movement.MoveUtil;


@FunctionAnnotation(name = "AutoParkour", type = Type.Player,desc = "Авто прыжок на краю блока")
public class AutoParkour extends Function {

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (MoveUtil.isBlockUnder(0.001f) && mc.player.isOnGround()) {
                mc.player.jump();
            }
        }
    }
}