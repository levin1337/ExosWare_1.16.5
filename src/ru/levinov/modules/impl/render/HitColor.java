package ru.levinov.modules.impl.render;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;

@FunctionAnnotation(name = "HitColor", type = Type.Render)
public class HitColor extends Function {

    public SliderSetting intensivity = new SliderSetting("Интенсивность", 0.3f, 0.1f, 1, 0.1f);

    public HitColor() {
        super();
        addSettings(intensivity);
    }

    @Override
    public void onEvent(Event event) {

    }
}
