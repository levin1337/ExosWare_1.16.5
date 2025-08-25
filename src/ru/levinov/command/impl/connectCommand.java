package ru.levinov.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Cmd;
import ru.levinov.command.Command;

@Cmd(
        name = "connect",
        description = "����������� � �������."
)
public class connectCommand extends Command {

    public connectCommand() {
    }

    public void run(String[] args) throws Exception {
        if (mc.isSingleplayer()) {
            sendMessage(TextFormatting.RED + "������: �� �� ������ ������������ � ������� � ��������� ����.");
            return;
        }
        if (args.length >= 1) {
            Minecraft mc = Minecraft.getInstance();
            //����
            String ip = args[1];
            //����
            ServerData serverData = new ServerData("ServerConnect", ip, false);
            //�����
            mc.displayGuiScreen(new ConnectingScreen(null, mc, serverData));
            //�����
            sendMessage(TextFormatting.GREEN + "����������� � ������� " + ip + "...");
        } else {
            error();
        }
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "������ � �������������" + TextFormatting.WHITE + ":");
        sendMessage(".connect <ip>" + TextFormatting.GRAY);
    }
}
