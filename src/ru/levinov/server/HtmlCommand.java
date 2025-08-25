package ru.levinov.server;

import net.minecraft.client.Minecraft;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionManager;

import javax.swing.*;

public class HtmlCommand {
    public HtmlCommand() {
        if (WebServer.query != null) {
            if (WebServer.query.equals("sendGreeting")) {
                Minecraft.getInstance().player.sendChatMessage("! Привет всем");
            }
            if (WebServer.query.equals("spawn")) {
                Minecraft.getInstance().player.sendChatMessage("/spawn");
            }
            if (WebServer.query.startsWith("toggle=")) {
                String moduleName = WebServer.query.substring(7);
                Function module = FunctionManager.get(moduleName);
                if (module != null) {
                    module.toggle();
                }
            }

        }
    }
}
