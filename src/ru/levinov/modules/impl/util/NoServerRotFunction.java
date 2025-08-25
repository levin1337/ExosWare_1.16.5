package ru.levinov.modules.impl.util;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventTeleport;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.util.ClientUtil;

/**
 * @author levin1337
 * @since 07.06.2023
 */
@FunctionAnnotation(name = "NoServerRots", type = Type.Util,desc = "Отключение сдвига ач")
public class NoServerRotFunction extends Function {
    private ModeSetting serverRotMode = new ModeSetting("Тип", "Обычный", "Обычный", "RW");

    public NoServerRotFunction() {
        addSettings(serverRotMode);
    }

    @Override
    public void onEvent(final Event event) {
        if (!serverRotMode.is("RW")) {
            if (event instanceof EventPacket packet) {
                if (packet.isReceivePacket()) {
                    if (packet.getPacket() instanceof SPlayerPositionLookPacket packet1) {
                        packet1.yaw = mc.player.rotationYaw;
                        packet1.pitch = mc.player.rotationPitch;
                    }
                }
            }
        }
    }
}
