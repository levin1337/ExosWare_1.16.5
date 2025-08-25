package ru.levinov.modules.impl.combat;

import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CUseEntityPacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

/**
 * @author levin1337
 * @since 27.06.2023
 */
@FunctionAnnotation(name = "NoFriendDamage", type = Type.Combat,desc = "Отключает урон по друзьям",
        keywords = {"NoDamageFriend"})
public class NoFriendDamage extends Function {


    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPacket packet) {
            if (packet.getPacket() instanceof CUseEntityPacket useEntityPacket) {
                Entity entity = useEntityPacket.getEntityFromWorld(mc.world);
                if (entity instanceof RemoteClientPlayerEntity
                        && Managment.FRIEND_MANAGER.isFriend(entity.getName().getString())
                        && useEntityPacket.getAction() == CUseEntityPacket.Action.ATTACK) {
                    event.setCancel(true);
                }
            }
        }
    }
}
