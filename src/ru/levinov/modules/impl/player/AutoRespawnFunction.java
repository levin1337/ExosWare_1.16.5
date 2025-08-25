package ru.levinov.modules.impl.player;

import net.minecraft.client.gui.screen.DeathScreen;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
/**
 * @author levin1337
 * @since 04.06.2023
 */
@FunctionAnnotation(name = "AutoRespawn", type = Type.Player, desc = "Респавнит вас при смерти")
public class AutoRespawnFunction extends Function {

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventUpdate) {
            if (mc.currentScreen instanceof DeathScreen && mc.player.deathTime > 2) {
                mc.player.respawnPlayer();
                mc.displayGuiScreen(null);
            }
        }
    }
}
