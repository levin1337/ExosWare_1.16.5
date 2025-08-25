package ru.levinov.events;

import net.minecraft.client.Minecraft;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.util.ClientUtil;

public class EventManager {

    /**
     * Вызывает событие и передает его всем активным модулям для обработки.
     *
     * @param event событие для вызова.
     */
    public static void call(final Event event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) {
            return;
        }

        if (event.isCancel()) {
            return;
        }

        if (!ClientUtil.legitMode) {
            callEvent(event);
        }
    }

    private static void callEvent(Event event) {
        for (final Function module : Managment.FUNCTION_MANAGER.getFunctions()) {
            if (!module.isState())
                continue;

            module.onEvent(event);
        }
    }
}