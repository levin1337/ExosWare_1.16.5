package ru.levinov.modules.impl.movement;

import net.minecraft.network.play.client.CPlayerPacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

import java.util.Random;

@FunctionAnnotation(name = "KTLeave", type = Type.Movement,desc = "Телепорт в рандомное место")
public class KTLeave extends Function {
    Random random = new Random();
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            leaveHoly();
        }
    }
    double randomZ = random.nextDouble() * 2000 - 1000;
    double randomX = random.nextDouble() * 2000 - 1000;
    private void leaveHoly() {
        int i;
        for(i = 0; i < 11; ++i) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
        }

        for(i = 0; i < 11; ++i) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() - 10, mc.player.getPosZ(), false));
        }

        mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() - 10, mc.player.getPosZ());


        toggle();
    }
}
