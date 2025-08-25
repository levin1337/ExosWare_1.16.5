package ru.levinov.modules.impl.util;

import net.minecraft.network.play.client.CCloseWindowPacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(name = "XCarry", type = Type.Util,desc = "Слоты для крафта")
public class XCarry extends Function {


    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPacket) {
            if (((EventPacket) event).getPacket() instanceof CCloseWindowPacket) {
                event.setCancel(true);
            }
        }
    }
}
