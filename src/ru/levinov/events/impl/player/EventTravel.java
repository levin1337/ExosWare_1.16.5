package ru.levinov.events.impl.player;

import ru.levinov.events.Event;

public class EventTravel extends Event {

    public float speed;

    public EventTravel(float speed) {
        this.speed = speed;
    }

}
