package ru.levinov.command.impl;

import ru.levinov.command.Cmd;
import ru.levinov.command.Command;
import ru.levinov.managment.Managment;
import ru.levinov.util.ClientUtil;

@Cmd(name = "help", description = "Информация")
public class HelpCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        for (Command cmd : Managment.COMMAND_MANAGER.getCommands()) {
            if (cmd instanceof HelpCommand) continue;
            ClientUtil.sendMesage(cmd.command + " | " + cmd.description);
        }
    }

    @Override
    public void error() {

    }
}
