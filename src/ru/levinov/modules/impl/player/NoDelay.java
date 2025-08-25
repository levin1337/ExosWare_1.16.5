package ru.levinov.modules.impl.player;

import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;

@FunctionAnnotation(name = "NoDelay", type = Type.Player, desc = "Отключение задержки")
public class NoDelay extends Function {

    private final MultiBoxSetting actions = new MultiBoxSetting("Действия",
            new BooleanOption("Прыжок", true),
            new BooleanOption("Ставить", false)
    );

    public NoDelay() {
        addSettings(actions);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (actions.get(0)) mc.player.jumpTicks = 0;
            if (actions.get(1)) mc.rightClickDelayTimer = 0;
        }
    }
}
