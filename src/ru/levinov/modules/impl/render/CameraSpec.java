package ru.levinov.modules.impl.render;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;


@FunctionAnnotation(name = "CameraSpec", type = Type.Render)
public class CameraSpec extends Function {

    public SliderSetting scroll = new SliderSetting("Сдвиг", 0.5f, -2f, 2f, 0.1f);

    public SliderSetting zoom = new SliderSetting("Zoom F5", 1f, 1f, 10f, 1f);


    public CameraSpec() {
        addSettings(scroll,zoom);
    }

    @Override
    public void onEvent(Event event) {
    }
}
