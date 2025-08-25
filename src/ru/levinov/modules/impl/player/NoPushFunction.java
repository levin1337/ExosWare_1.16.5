package ru.levinov.modules.impl.player;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;

/**
 * @author levin1337
 * @since 04.06.2023
 */
@FunctionAnnotation(name = "NoPush", type = Type.Player,desc = "Отключение смещения")
public class NoPushFunction extends Function {

    public final MultiBoxSetting modes = new MultiBoxSetting("Тип",
            new BooleanOption("Игроки", true),
            new BooleanOption("Блоки", true),
            new BooleanOption("Вода", true));

    public NoPushFunction() {
        addSettings(modes);
    }

    @Override
    public void onEvent(final Event event) {
    }
}
