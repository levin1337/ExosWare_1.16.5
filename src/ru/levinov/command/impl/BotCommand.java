package ru.levinov.command.impl;

import ru.levinov.command.Cmd;
import ru.levinov.command.Command;

import java.awt.*;

@Cmd(
        name = "bot",
        description = "Управление ботами (test)"
)
public class BotCommand extends Command {

    public BotCommand() {
    }

    public void run(String[] args) throws Exception {
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "screen" -> this.up();
                case "message" -> message(args[2]);
            }
        }

    }

    public void error() {
    }

    private void up() {
    }

    private void message(String message) {
        // Получаем имя игрока из mc.session
        String playerName = mc.getSession().getProfile().getName();
        String formattedMessage = String.format("<%s> %s", playerName, message);
        mc.player.sendChatMessage(formattedMessage);
    }
}