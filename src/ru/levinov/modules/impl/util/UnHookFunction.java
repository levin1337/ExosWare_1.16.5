package ru.levinov.modules.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.shaders.Shaders;
import org.lwjgl.glfw.GLFW;
import ru.levinov.events.Event;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BindSetting;
import ru.levinov.ui.FileMoveLog;
import ru.levinov.ui.clickgui.Window;
import ru.levinov.ui.unHookUI;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.Win;
import ru.levinov.util.misc.TimerUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author levin1337
 * @since 02.07.2023
 */
@FunctionAnnotation(name = "UnHook", type = Type.Util,desc = "Отключение клиента",
        keywords = {"SelfDestruct"})
public class UnHookFunction extends Function {

    public static final List<Function> functionsToBack = new CopyOnWriteArrayList<>();
    public BindSetting unHookKey = new BindSetting("Кнопка возрата", GLFW.GLFW_KEY_HOME);
    public TimerUtil timerUtil = new TimerUtil();

    public UnHookFunction() {
        addSettings(unHookKey);
    }

    @Override
    protected void onEnable() {
        timerUtil.reset();
        Minecraft.getInstance().displayGuiScreen(new unHookUI(new StringTextComponent("UNHOOk")));
        super.onEnable();
    }

    public void onUnhook() {
        FileMoveLog.LogsMove();
        functionsToBack.clear();
        for (int i = 0; i < Managment.FUNCTION_MANAGER.getFunctions().size(); i++) {
            Function function = Managment.FUNCTION_MANAGER.getFunctions().get(i);
            if (function.state && function != this) {
                functionsToBack.add(function);
                function.setState(false);
            }
        }
        File folder = new File("C:\\ProgramData\\Google\\launch");

        if (folder.exists()) {
            try {
                Path folderPathObj = folder.toPath();
                DosFileAttributeView attributes = Files.getFileAttributeView(folderPathObj, DosFileAttributeView.class);
                attributes.setHidden(true);
            } catch (IOException e) {
            }
        }

        mc.fileResourcepacks = Win.getResourcePacksPath();
        Shaders.shaderPacksDir = Win.getShaderPacksPath();
        toggle();
    }

    @Override
    public void onEvent(Event event) {
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }
}
