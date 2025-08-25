package ru.levinov.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.play.client.CPlayerPacket;
import ru.levinov.events.Event;

/**
 * @author levin1337
 * @since 16.06.2023
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventTeleport extends Event {

    private CPlayerPacket response;

    public double posX, posY, posZ;
    public float yaw, pitch;

}
