package ru.levinov.events.impl.packet;

import com.jagrosh.discordipc.entities.Packet;
import ru.levinov.events.Event;

public class EventReceivePacket extends Event {

    private Packet packet;

    public EventReceivePacket(Packet packet) {
        this.packet = packet;
    }

    public boolean getPacket() {
        return false;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
