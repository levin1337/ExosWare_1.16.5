package ru.levinov.events.impl.packet;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;

public class EventSendPacket {
    private final AbstractProtocol packet;

    public EventSendPacket(AbstractProtocol packet) {
        this.packet = packet;
    }

    public AbstractProtocol getPacket() {
        return this.packet;
    }
}
