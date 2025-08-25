package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;

@Cmd(
        name = "option",
        description = "����� ��������"
)
public class OptionCommand extends Command {

    public OptionCommand() {
    }

    public void run(String[] args) throws Exception {
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "reset" -> reset();
                case "save" -> save();
            }
        } else {
            error();
        }

    }

    @Override
    public void error() {
        this.sendMessage(TextFormatting.GRAY + "������ � �������������" + TextFormatting.WHITE + ":");
        this.sendMessage(".option reset - �������� ��������� � ��������� / .option save - ���������� ��������" + TextFormatting.GRAY);
    }

    public void save() {
        mc.gameSettings.saveOptions();
        mc.gameSettings.saveOfOptions();
    }

    private void reset() {
        mc.gameSettings.resetSettings();
    }

}