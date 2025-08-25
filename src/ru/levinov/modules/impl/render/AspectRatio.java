package ru.levinov.modules.impl.render;

import net.minecraft.client.renderer.GameRenderer;
import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;

@FunctionAnnotation(name = "AspectRatio", type = Type.Render)
public class AspectRatio extends Function {

    public SliderSetting scroll = new SliderSetting("–€ст€г", 1, 0.7f, 1.5f, 0.1f);



    public AspectRatio() {
        addSettings(scroll);
    }
    @Override
    public void onEvent(Event event) {
    }
}
