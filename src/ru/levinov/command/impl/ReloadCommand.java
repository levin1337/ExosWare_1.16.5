package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.util.math.KeyMappings;

/**
 * @author levin1337
 * @since 25.06.2023
 */
@Cmd(name = "reload", description = "Перезагрузка всех скриптов")
public class ReloadCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        Managment.SCRIPT_MANAGER.reload();
        sendMessage("Все скрипты перезагружены.");
    }

    @Override
    public void error() {

    }
}