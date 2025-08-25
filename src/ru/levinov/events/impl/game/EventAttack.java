package ru.levinov.events.impl.game;

import net.minecraft.entity.Entity;
import ru.levinov.events.Event;

public class EventAttack extends Event {
    public Entity entity;

    public Entity getEntity() {
        return this.entity;
    }

    public EventAttack(Entity entity) {
        this.entity = entity;
    }
}
