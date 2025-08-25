package ru.levinov.events.impl.player;

import ru.levinov.events.Event;

/**
 * @author levin1337
 * @since 22.07.2023
 */
public class EventDamage extends Event {
    private final DamageType damageType;

    public EventDamage(DamageType damageType) {
        this.damageType = damageType;
    }

    public DamageType getDamageType() {
        return this.damageType;
    }

    public enum DamageType {
        FALL,
        ARROW,
        ENDER_PEARL;
    }
}
