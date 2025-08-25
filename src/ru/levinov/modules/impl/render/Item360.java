package ru.levinov.modules.impl.render;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;

/**
 * @author levin1337
 * @since 07.06.2024
 */

@FunctionAnnotation(name = "Item360", type = Type.Render)
public class Item360 extends Function {
    public final SliderSetting speed = new SliderSetting("Замедление", 1L, 1L, 5L, 1L);
    public static  BooleanOption nulltarget = new BooleanOption("Если нету таргета", true);

    public static  BooleanOption left = new BooleanOption("Левая рука", true);
    public static  BooleanOption right = new BooleanOption("Правая рука", false);


    public Item360() {
        addSettings(speed,nulltarget,right,left);
    }
    @Override
    public void onEvent(Event event) {

    }
}
