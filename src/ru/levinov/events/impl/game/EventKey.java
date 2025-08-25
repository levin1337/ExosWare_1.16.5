package ru.levinov.events.impl.game;

import ru.levinov.events.Event;

public class EventKey extends Event {

    public int key;

    public EventKey(int key) {
        this.key = key;
    }
}
