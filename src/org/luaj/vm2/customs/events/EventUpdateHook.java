package org.luaj.vm2.customs.events;

import org.luaj.vm2.customs.EventHook;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventUpdate;

public class EventUpdateHook extends EventHook {

    private EventUpdate update;

    public EventUpdateHook(Event event) {
        super(event);
    }

    @Override
    public String getName() {
        return "update_event";
    }
}
