package ru.levinov.events.impl.render;

import ru.levinov.events.Event;

public class RotationEvent extends Event {
    public float yaw;
    public float pitch;
    public float partialTicks;

    public RotationEvent(float yaw, float pitch, float partialTicks) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.partialTicks = partialTicks;
    }
}
