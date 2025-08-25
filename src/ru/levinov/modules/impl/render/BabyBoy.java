package ru.levinov.modules.impl.render;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;

import static org.luaj.vm2.Print.print;


@FunctionAnnotation(name = "BabyBoy", type = Type.Render)
public class BabyBoy extends Function {
    public BabyBoy() {
        addSettings();
    }

    @Override
    public void onEvent(Event event) {
    }
}
