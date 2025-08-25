package ru.levinov.events.impl.player;

import ru.levinov.events.Event;

public class EventStep extends Event {

    public float stepHeight;

    public EventStep(float stepHeight) {
        this.stepHeight = stepHeight;
    }

}
