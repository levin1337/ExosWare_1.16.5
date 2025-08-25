package org.luaj.vm2.customs;

import ru.levinov.events.Event;

public class EventHook {

    public Event event;

    public EventHook(Event event) {
        this.event = event;
    }

    public String getName() {
        return "default";
    }

}
