package ru.levinov.modules.impl.player;

import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(
        name = "BedrockLeave",
        type = Type.Misc,
        desc = "Ливает под бедрок от дамага"
)
public class BedrockLeave extends Function {
    private ItemStack oldStack = null;

    public BedrockLeave() {
    }

    public void onEvent(Event event) {
        if (event instanceof EventMotion && (mc.player.isInWater() || mc.player.hurtTime > 0)) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));

            int i;
            for(i = 0; i < 12; ++i) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
            }

            for(i = 0; i < 12; ++i) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() - 100, mc.player.getPosZ(), false));
            }

            mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() - 100, mc.player.getPosZ());
            this.toggle();
        }

    }
}