package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import ru.levinov.command.Cmd;
import ru.levinov.command.Command;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.util.math.KeyMappings;

/**
 * @author levin1337
 * @since 25.06.2023
 */
@Cmd(name = "bind", description = "���� ������ �� �������")
public class BindCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        try {
            if (args.length >= 2) {
                switch (args[1].toLowerCase()) {
                    case "list" -> listBoundKeys();
                    case "clear" -> clearAllBindings();
                    case "add" -> {
                        if (args.length >= 4) {
                            addKeyBinding(args[2], args[3]);
                        } else {
                            error();
                        }
                    }
                    case "remove" -> {
                        if (args.length >= 4) {
                            removeKeyBinding(args[2], args[3]);
                        } else {
                            error();
                        }
                    }
                    default -> error();
                }
            } else {
                error();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ����� ��� ������ ������ ������� � ������������ ���������
     */
    private void listBoundKeys() {
        sendMessage(TextFormatting.GRAY + "������ ���� ������� � ������������ ���������:");
        for (Function f : Managment.FUNCTION_MANAGER.getFunctions()) {
            if (f.bind == 0) continue;
            sendMessage(f.name + " [" + TextFormatting.GRAY + (GLFW.glfwGetKeyName(f.bind, -1) == null ? "" : GLFW.glfwGetKeyName(f.bind, -1)) + TextFormatting.RESET + "]");
        }
    }

    /**
     * ����� ��� ������� ���� �������� ������
     */
    private void clearAllBindings() {
        for (Function f : Managment.FUNCTION_MANAGER.getFunctions()) {
            f.bind = 0;
        }
        sendMessage(TextFormatting.GREEN + "��� ������� ���� �������� �� �������");
    }

    /**
     * ����� ��� ���������� �������� ������� � ������
     *
     * @param moduleName ��� ������
     * @param keyName    �������� �������
     */
    private void addKeyBinding(String moduleName, String keyName) {
        Integer key = null;

        try {
            key = KeyMappings.keyMap.get(keyName.toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Function module = Managment.FUNCTION_MANAGER.get(moduleName);
        if (key != null) {
            if (module != null) {
                module.bind = key;
                sendMessage("������� " + TextFormatting.GRAY + keyName + TextFormatting.WHITE + " ���� ��������� � ������ " + TextFormatting.GRAY + module.name);
            } else {
                sendMessage("������ " + moduleName + " �� ��� ������");
            }
        } else {
            sendMessage("������� " + keyName + " �� ���� �������!");
        }
    }

    /**
     * ����� ��� �������� �������� �������
     *
     * @param moduleName ��� ������
     * @param keyName    �������� �������
     */
    private void removeKeyBinding(String moduleName, String keyName) {
        for (Function f : Managment.FUNCTION_MANAGER.getFunctions()) {
            if (f.name.equalsIgnoreCase(moduleName)) {
                f.bind = 0;
                sendMessage("������� " + TextFormatting.GRAY + keyName + TextFormatting.RESET + " ���� �������� �� ������ " + TextFormatting.GRAY + f.name);
            }
        }
    }

    /**
     * ����� ��� ��������� ������ ��������� ���������� �������
     */
    @Override
    public void error() {
        sendMessage(TextFormatting.WHITE + "�������� ��������� �������. " + TextFormatting.GRAY + "�����������:");
        sendMessage(TextFormatting.WHITE + ".bind add " + TextFormatting.DARK_GRAY + "<name> <key>");
        sendMessage(TextFormatting.WHITE + ".bind remove " + TextFormatting.DARK_GRAY + "<name> <key>");
        sendMessage(TextFormatting.WHITE + ".bind list");
        sendMessage(TextFormatting.WHITE + ".bind clear");
    }
}