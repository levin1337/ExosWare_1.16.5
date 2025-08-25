package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Cmd;
import ru.levinov.command.Command;
import ru.levinov.managment.Managment;
import ru.levinov.ui.midnight.Style;
import ru.levinov.ui.midnight.StyleManager;

@Cmd(name = "theme", description = "Смена темы клиента")
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
        currentStyle = new Style("Командный", StyleManager.HexColor.toColor(color), StyleManager.HexColor.toColor(color2));
        Managment.STYLE_MANAGER.setCurrentStyle(currentStyle);
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "Ошибка в использовании" + TextFormatting.WHITE + ":");
        sendMessage(".theme clear - Установить тему клиента!" + TextFormatting.GRAY);
        sendMessage(".theme set 1 цвет, 2 цвет писать без запятых - установить свою тему!" + TextFormatting.GRAY);
        sendMessage("Цвета должны быть в HexColor сайт: https://colorscheme.ru/html-colors.html" + TextFormatting.GRAY);
    }
}
