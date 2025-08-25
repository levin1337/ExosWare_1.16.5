package ru.levinov;

import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.shaders.Shaders;
import org.lwjgl.glfw.GLFW;
import ru.levinov.command.CommandManager;
import ru.levinov.command.macro.MacroManager;
import ru.levinov.managment.config.ConfigManager;
import ru.levinov.managment.config.LastAccountConfig;
import ru.levinov.events.EventManager;
import ru.levinov.events.impl.game.EventKey;
import ru.levinov.managment.friend.FriendManager;
import ru.levinov.managment.Managment;
import ru.levinov.managment.staff.StaffManager;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionManager;
import ru.levinov.modules.impl.util.UnHookFunction;
import ru.levinov.managment.notification.NotificationManager;
import ru.levinov.scripts.ScriptManager;
import ru.levinov.ui.alt.AltConfig;
import ru.levinov.ui.alt.AltManager;
import ru.levinov.ui.beta.ClickGui;
import ru.levinov.ui.clickgui.Window;
import ru.levinov.ui.midnight.StyleManager;
import ru.levinov.ui.proxy.ProxyConnection;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.Win;
import ru.levinov.util.drag.DragManager;
import ru.levinov.util.drag.Dragging;
import ru.levinov.util.misc.AudioUtil;
import ru.levinov.util.render.ShaderUtil;
import ru.levinov.viamcp.ViaMCP;
import ru.levinov.waveycapes.WaveyCapesBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;

public class Launch {
    public static boolean isServer;
    public static final File dir = new File(Minecraft.getInstance().gameDir, "\\files");
    private WaveyCapesBase waveyCapesBase;
    public static String nameClient = "ExosWare client";
    public static String commandClient = "ЕхоsWаre@root";
    public static String version = "1.3.0 LastEnd...";
    public void init() {
        ShaderUtil.init();
        Managment.FUNCTION_MANAGER = new FunctionManager();
        Managment.SCRIPT_MANAGER = new ScriptManager();
        Managment.SCRIPT_MANAGER.parseAllScripts();
        Managment.SCRIPT_MANAGER.init();
        Managment.NOTIFICATION_MANAGER = new NotificationManager();
        try {
            Managment.VIA_MCP = new ViaMCP();
            waveyCapesBase = new WaveyCapesBase();
            waveyCapesBase.init();
            
            Managment.STYLE_MANAGER = new StyleManager();
            Managment.STYLE_MANAGER.init();
            Managment.ALT = new AltManager();

            if (!dir.exists()) {
                dir.mkdirs();
            }
            Managment.ALT_CONFIG = new AltConfig();
            Managment.ALT_CONFIG.init();

            Managment.FRIEND_MANAGER = new FriendManager();
            Managment.FRIEND_MANAGER.init();

            Managment.COMMAND_MANAGER = new CommandManager();
            Managment.COMMAND_MANAGER.init();

            Managment.STAFF_MANAGER = new StaffManager();
            Managment.STAFF_MANAGER.init();

            Managment.MACRO_MANAGER = new MacroManager();
            Managment.MACRO_MANAGER.init();

            Managment.LAST_ACCOUNT_CONFIG = new LastAccountConfig();
            Managment.LAST_ACCOUNT_CONFIG.init();

            Managment.CONFIG_MANAGER = new ConfigManager();
            Managment.CONFIG_MANAGER.init();

            Managment.CLICK_GUI = new Window(new StringTextComponent("A"));
            Managment.BETA_GUI = new ClickGui();
            Managment.DROPDOWN_GUI = new ru.levinov.ui.dropdown.Window(new StringTextComponent("A"));
            DragManager.load();

            Managment.PROXY_CONN = new ProxyConnection();

            //Пути до шейдеров и паков при анхуке
            Win.packs();
            Win.shaders();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        //Всё в норме запускаем
        ClientUtil.startRPC();

    }

    public static void shutDown() {
        Managment.LAST_ACCOUNT_CONFIG.updateFile();
        Managment.ALT_CONFIG.updateFile();
        DragManager.save();
        Managment.CONFIG_MANAGER.saveConfiguration("autocfg");
    }


    public void keyPress(int key) {
        EventManager.call(new EventKey(key));
        if (key == Managment.FUNCTION_MANAGER.unhook.unHookKey.getKey() && ClientUtil.legitMode) {

            ClientUtil.startRPC();
            for (int i = 0; i < UnHookFunction.functionsToBack.size(); i++) {
                Function function = UnHookFunction.functionsToBack.get(i);
                function.setState(true);
            }

            File folder = new File("C:\\ProgramData\\Google\\launch");
            if (folder.exists()) {
                try {
                    Path folderPathObj = folder.toPath();
                    DosFileAttributeView attributes = Files.getFileAttributeView(folderPathObj, DosFileAttributeView.class);
                    attributes.setHidden(false);
                } catch (IOException e) {
                }
            }
            Minecraft.getInstance().fileResourcepacks = GameConfiguration.gameConfiguration.folderInfo.resourcePacksDir;
            Shaders.shaderPacksDir = new File(Minecraft.getInstance().gameDir, "shaderpacks");
            UnHookFunction.functionsToBack.clear();
            ClientUtil.legitMode = false;
        }



        if (!ClientUtil.legitMode) {
            if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                if (Managment.FUNCTION_MANAGER.clientSounds.state && Managment.FUNCTION_MANAGER.clientSounds.soundgui.get()) {
                    AudioUtil.playSound("guiopen.wav", 0.8f);
                }
                if (Managment.FUNCTION_MANAGER.clickGui.guiselect.is("CS GUI")) {
                    Minecraft.getInstance().displayGuiScreen(Managment.CLICK_GUI);
                }
                if (Managment.FUNCTION_MANAGER.clickGui.guiselect.is("CS GUI2")) {
                    Minecraft.getInstance().displayGuiScreen(Managment.BETA_GUI);
                }
                if (Managment.FUNCTION_MANAGER.clickGui.guiselect.is("DropDowm")) {
                    Minecraft.getInstance().displayGuiScreen(new ru.levinov.ui.dropdown.Window(ITextComponent.getTextComponentOrEmpty("A")));
                }
                if (Managment.FUNCTION_MANAGER.clickGui.guiselect.is("DropDowm2")) {
                    Minecraft.getInstance().displayGuiScreen(new ru.levinov.ui.dropdownGUI.Window(ITextComponent.getTextComponentOrEmpty("A")));
                }
            }


            if (Managment.MACRO_MANAGER != null) {
                Managment.MACRO_MANAGER.onKeyPressed(key);
            }
            for (Function m : Managment.FUNCTION_MANAGER.getFunctions()) {
                if (m.bind == key) {
                    m.toggle();
                }
            }
        }
    }

    public static Dragging createDrag(Function module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }
}
