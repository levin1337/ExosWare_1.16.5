package ru.levinov.modules.impl.render;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;

@FunctionAnnotation(name = "ExtendedTab", type = Type.Render)
public class ExtendedTab extends Function {

    public static  SliderSetting quantity = new SliderSetting("Игроков в колонке", 20, 15, 50, 10f);
    public static  SliderSetting column = new SliderSetting("Макс. кол-во колонок", 3, 3, 5, 1f);

    public ExtendedTab() {
        addSettings(column,quantity);
    }
    @Override
    public void onEvent(Event event) {

    }
}