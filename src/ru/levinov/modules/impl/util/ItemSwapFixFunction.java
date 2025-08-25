package ru.levinov.modules.impl.util;

import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

/**
 * @author levin1337
 * @since 12.06.2023
 */
@FunctionAnnotation(name = "ItemSwapFix", type = Type.Util,desc = "Не даст переключить слоты ач",
        keywords = {"NoSlotChange","NoServerDesync","СлотФиксер"})
public class ItemSwapFixFunction extends Function {
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPacket packetEvent) {
            if (packetEvent.isReceivePacket()) {
                if (packetEvent.getPacket() instanceof SHeldItemChangePacket packetHeldItemChange) {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    event.setCancel(true);
                }
            }
        }
    }
}
