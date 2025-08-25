package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.util.ClientUtil;

@Cmd(name = "t", description = "��������/��������� ������.")
public class ToggleCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        if (args.length == 2) {
            Function func = Managment.FUNCTION_MANAGER.get(args[1]);
            func.setState(!func.isState());

            if (func.isState()) ClientUtil.sendMesage(TextFormatting.GREEN + "������ " + func.name + " �������.");
            else ClientUtil.sendMesage(TextFormatting.RED + "������ " + func.name + " ��������.");
        } else {
            ClientUtil.sendMesage(TextFormatting.RED + "�� ������� �������� �������� ������!");
        }
    }

    @Override
    public void error() {

    }
}
