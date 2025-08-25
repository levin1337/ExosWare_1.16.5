package ru.levinov.modules.impl.render;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(name = "ButtonBloom", type = Type.Render)
public class ButtonBloom extends Function {
    public ButtonBloom() {
        addSettings();
    }

    @Override
    public void onEvent(Event event) {
    }
}
