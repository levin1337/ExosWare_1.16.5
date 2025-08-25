package ru.levinov.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Cmd;
import ru.levinov.command.Command;

@Cmd(
        name = "connect",
        description = "Подключение к серверу."
)
public class connectCommand extends Command {

    public connectCommand() {
    }

    public void run(String[] args) throws Exception {
        if (mc.isSingleplayer()) {
            sendMessage(TextFormatting.RED + "Ошибка: Вы не можете подключиться к серверу в одиночном мире.");
            return;
        }
        if (args.length >= 1) {
            Minecraft mc = Minecraft.getInstance();
            //Айпи
            String ip = args[1];
            //Дата
            ServerData serverData = new ServerData("ServerConnect", ip, false);
            //скрин
            mc.displayGuiScreen(new ConnectingScreen(null, mc, serverData));
            //ратка
            sendMessage(TextFormatting.GREEN + "Подключение к серверу " + ip + "...");
        } else {
            error();
        }
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "Ошибка в использовании" + TextFormatting.WHITE + ":");
        sendMessage(".connect <ip>" + TextFormatting.GRAY);
    }
}
