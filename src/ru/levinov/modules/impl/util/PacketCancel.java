package ru.levinov.modules.impl.util;

import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventAction;
import ru.levinov.events.impl.player.EventInput;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventMove;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;

@FunctionAnnotation(name = "PacketCancel", type = Type.Util,desc = "Отменение пакетов")
public class PacketCancel extends Function {

    public final BooleanOption move = new BooleanOption("EventMove", true);
    public final BooleanOption packet = new BooleanOption("EventPacket", true);
    public final BooleanOption eventaction = new BooleanOption("EventAction", true);
    public final BooleanOption eventmotion = new BooleanOption("eventmotion", false);

    public PacketCancel() {
        addSettings(move, packet, eventaction, eventmotion);
    }

    @Override
    public void onEvent(Event event) {
        if (move.get()) {
            if (event instanceof EventMove e) {
                e.setCancel(true);
            }
        }
        if (packet.get()) {
            if (event instanceof EventPacket e2) {
                e2.setCancel(true);
            }
        }
        if (eventaction.get()) {
            if (event instanceof EventAction e3) {
                e3.setCancel(true);
            }
        }
        if (eventmotion.get()) {
            if (event instanceof EventMotion e4) {
                e4.setCancel(true);
            }
        }
    }
}