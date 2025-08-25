package ru.levinov.command.impl;

import com.mojang.datafixers.types.Func;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.util.ClientUtil;

@Cmd(name = "panic", description = "��������� ��� ������� ����")

public class PanicCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        if (args.length == 1) {
            Managment.FUNCTION_MANAGER.getFunctions().stream().filter(function -> function.state).forEach(function -> function.setState(false));
            ClientUtil.sendMesage("�������� ��� ������!");
        } else error();
    }

    @Override
    public void error() {

    }
}
