package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;
import ru.levinov.util.drag.DragManager;

@Cmd(
        name = "drag",
        description = "—брос всех элементов на экране."
)
public class dragCommand extends Command {

    public void run(String[] args) throws Exception {
        DragManager.reset();
    }

    public void error() {
        sendMessage(TextFormatting.GRAY + "ќшибка в использовании" + TextFormatting.WHITE + ":");
        sendMessage(".drag reset" + TextFormatting.GRAY);
    }
}
