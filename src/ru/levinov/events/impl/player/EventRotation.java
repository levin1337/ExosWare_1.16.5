package ru.levinov.events.impl.player;

import ru.levinov.events.Event;

public class EventRotation extends Event {

    public float yaw,pitch;

    public EventRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

}
