package ru.levinov.modules.impl.Misc;

import net.minecraft.network.play.client.CPlayerPacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(
        name = "NoPitchLimit",
        type = Type.Util,
        desc = "Убирает лимит на поворот"
)
public class NoPitchLimit extends Function {
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPacket e) {
            if (e.getPacket() instanceof CPlayerPacket p) {
                if (p.rotating) {
                    p.pitch = mc.player.rotationPitch + 50;
                }
            }
        }
    }
}