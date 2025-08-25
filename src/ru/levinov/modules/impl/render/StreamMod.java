package ru.levinov.modules.impl.render;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;

@FunctionAnnotation(
        name = "StreamMod",
        type = Type.Render
)
public class StreamMod extends Function {
    public final BooleanOption warp = new BooleanOption("Âàðïû", true);
    public final BooleanOption tpa = new BooleanOption("Òïà", false);

    public StreamMod() {
        this.addSettings(warp, this.tpa);
    }

    public void onEvent(Event event) {
    }
}
