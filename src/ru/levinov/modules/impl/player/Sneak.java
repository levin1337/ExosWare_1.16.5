package ru.levinov.modules.impl.player;

import net.minecraft.network.play.client.CEntityActionPacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.misc.TimerUtil;

@FunctionAnnotation(name = "Sneak", type = Type.Player,desc = "Бег на шифте")
public class Sneak extends Function {

    private final TimerUtil timerHelper = new TimerUtil();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            if (timerHelper.hasTimeElapsed(15)) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
            } else {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
            }
            timerHelper.reset();
        }
    }


    @Override
    public void onDisable() {
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
        timerHelper.reset();
    }
}
