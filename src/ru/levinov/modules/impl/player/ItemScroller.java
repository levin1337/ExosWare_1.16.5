package ru.levinov.modules.impl.player;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;

/**
 * @author levin1337
 * @since 25.06.2023
 */

@FunctionAnnotation(name = "ItemScroller", type = Type.Player,desc = "Помощь со слотами")
public class ItemScroller extends Function {

    public SliderSetting delay = new SliderSetting("Задержка", 80, 0, 1000, 1);


    public ItemScroller() {
        addSettings(delay);
    }

    @Override
    public void onEvent(Event event) {

    }
}
