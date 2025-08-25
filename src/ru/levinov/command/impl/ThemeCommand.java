package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Cmd;
import ru.levinov.command.Command;
import ru.levinov.managment.Managment;
import ru.levinov.ui.midnight.Style;
import ru.levinov.ui.midnight.StyleManager;

@Cmd(name = "theme", description = "����� ���� �������")
public class ThemeCommand extends Command {
    private Style currentStyle = null;

    @Override
    public void run(String[] args) throws Exception {
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "set" -> settheme(args[2],args[3]);
                case "clear"  -> cleartheme();
            }
        } else {
            error();
        }
    }
    void cleartheme() {
        currentStyle = StyleManager.styles.get(0);
        Managment.STYLE_MANAGER.setCurrentStyle(currentStyle);
    }

    void settheme(String color, String color2) {
        currentStyle = new Style("���������", StyleManager.HexColor.toColor(color), StyleManager.HexColor.toColor(color2));
        Managment.STYLE_MANAGER.setCurrentStyle(currentStyle);
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "������ � �������������" + TextFormatting.WHITE + ":");
        sendMessage(".theme clear - ���������� ���� �������!" + TextFormatting.GRAY);
        sendMessage(".theme set 1 ����, 2 ���� ������ ��� ������� - ���������� ���� ����!" + TextFormatting.GRAY);
        sendMessage("����� ������ ���� � HexColor ����: https://colorscheme.ru/html-colors.html" + TextFormatting.GRAY);
    }
}
