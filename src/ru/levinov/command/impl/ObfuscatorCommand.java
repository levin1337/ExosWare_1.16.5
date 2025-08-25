package ru.levinov.command.impl;

import org.luaj.vm2.globals.Standarts;
import ru.levinov.command.Cmd;
import ru.levinov.command.Command;
import ru.levinov.managment.Managment;
import ru.levinov.scripts.DefaultScript;

import java.io.File;
import java.nio.file.Files;

@Cmd(name = "obf", description = "Обфускация скрипта.")
public class ObfuscatorCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            String scriptName = args[1];
            if (Files.exists(new File(mc.gameDir, "scripts/" + scriptName + ".lua").toPath())) {
                DefaultScript script = null;
                for (DefaultScript script1 : Managment.SCRIPT_MANAGER.scripts) {
                    if (script1.scriptName.equalsIgnoreCase(scriptName + ".lua")) {
                        script = script1;
                    }
                }
                if (script != null) {
                    script.processScript(Standarts.standardGlobals(), Files.newInputStream(new File(mc.gameDir, "scripts/" + scriptName + ".lua").toPath()),  "=stdin", Files.newOutputStream(new File(mc.gameDir, "scripts/" + scriptName + "-obf.lua").toPath()));
                    sendMessage("Скрипт сохранен с названием: " + scriptName + "-obf.lua");
                }
            } else {
                sendMessage("Скрипт не найден!");
            }
        }
    }

    @Override
    public void error() {

    }
}
