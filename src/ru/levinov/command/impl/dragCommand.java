package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;
import ru.levinov.util.drag.DragManager;

@Cmd(
        name = "drag",
        description = "����� ���� ��������� �� ������."
)
public class dragCommand extends Command {

    public void run(String[] args) throws Exception {
        DragManager.reset();
    }

    public void error() {
        sendMessage(TextFormatting.GRAY + "������ � �������������" + TextFormatting.WHITE + ":");
        sendMessage(".drag reset" + TextFormatting.GRAY);
    }
}
