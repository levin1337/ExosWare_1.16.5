package ru.levinov.modules.impl.util;

import net.minecraft.client.gui.LoadingGui;
import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.ClientUtil;

@FunctionAnnotation(name = "DiscordRPC", type = Type.Util,desc = "Вкл/Выкл активности")
public class DiscordRPC extends Function {

    @Override
    protected void onDisable() {
        super.onDisable();

    }


    @Override
    public void onEvent(Event event) {

    }
}
