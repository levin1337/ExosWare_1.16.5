package ru.levinov.managment;

import ru.levinov.command.CommandManager;
import ru.levinov.command.macro.MacroManager;
import ru.levinov.managment.config.ConfigManager;
import ru.levinov.managment.config.LastAccountConfig;
import ru.levinov.managment.friend.FriendManager;
import ru.levinov.managment.staff.StaffManager;
import ru.levinov.modules.FunctionManager;
import ru.levinov.managment.notification.NotificationManager;
import ru.levinov.scripts.ScriptManager;
import ru.levinov.ui.alt.AltConfig;
import ru.levinov.ui.alt.AltManager;
import ru.levinov.ui.beta.ClickGui;
import ru.levinov.ui.clickgui.Window;
import ru.levinov.ui.midnight.StyleManager;
import ru.levinov.ui.proxy.ProxyConnection;
import ru.levinov.util.UserProfile;
import ru.levinov.viamcp.ViaMCP;

public class Managment {
    public static FunctionManager FUNCTION_MANAGER;
    public static CommandManager COMMAND_MANAGER;
    public static FriendManager FRIEND_MANAGER;
    public static MacroManager MACRO_MANAGER;
    public static LastAccountConfig LAST_ACCOUNT_CONFIG;
    public static ScriptManager SCRIPT_MANAGER;

    public static StaffManager STAFF_MANAGER;
    public static Window CLICK_GUI;
    public static ClickGui BETA_GUI;
    public static ru.levinov.ui.dropdown.Window DROPDOWN_GUI;
    public static ConfigManager CONFIG_MANAGER;
    public static StyleManager STYLE_MANAGER;
    public static UserProfile USER_PROFILE;
    public static NotificationManager NOTIFICATION_MANAGER;
    public static AltManager ALT;
    public static AltConfig ALT_CONFIG;

    public static ProxyConnection PROXY_CONN;
    public static ViaMCP VIA_MCP;
}
