package ru.levinov.command;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.impl.*;
import ru.levinov.managment.Managment;
import ru.levinov.util.ClientUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    public List<Command> commandList = new ArrayList<>();

    public boolean isMessage;


    public void init() {
        commandList.addAll(Arrays.asList(
                new HClipCommand(),
                new VClipCommand(),
                new clipCommand(),
                new RegionCreateCommand(),
                new TPCommand(),
                new dragCommand(),
                new BotCommand(),
                new eclipCommand(),
                new OptionCommand(),
                new HelpCommand(),
                new MacroCommand(),
                new BindCommand(),
                new ConfigCommand(),
                new ThemeCommand(),
                new FriendCommand(),
                new PanicCommand(),
                new LoginCommand(),
                new StaffCommand(),
                new ideaCommand(),
                new reportCommand(),
                new GPSCommand(),
                new ParseCommand(),
                new ReloadCommand(),
                new ObfuscatorCommand(),
                new ToggleCommand(),
                new connectCommand()
        ));
    }
    // NOCOMMANDS
    public void runCommands(String message) {
        if (ClientUtil.legitMode || Managment.FUNCTION_MANAGER.noCommands.state) {
            isMessage = false;
            return;
        }

        if (message.startsWith(".")) {
            for (Command command : Managment.COMMAND_MANAGER.getCommands()) {
                if (message.startsWith("." + command.command)) {
                    try {
                        command.run(message.split(" "));
                    } catch (Exception ex) {
                        command.error();
                        ex.printStackTrace();
                    }
                    isMessage = true;
                    return;
                }
            }
            ClientUtil.sendMesage(TextFormatting.RED + "Команды не существует");
            ClientUtil.sendMesage(TextFormatting.GRAY + "Используйте: .help");
            isMessage = true;

        } else {
            isMessage = false;
        }
    }

    public List<Command> getCommands() {
        return commandList;
    }
}
