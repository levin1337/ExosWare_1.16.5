package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;
import ru.levinov.managment.Managment;
import ru.levinov.managment.staff.StaffManager;

/**
 * @author levin1337
 * @since 06.07.2023
 */
@Cmd(name = "staff", description = "Управление персоналом")
public class StaffCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "add" -> addStaffName(args[2]);
                case "remove" -> removeStaffName(args[2]);
                case "clear" -> clearList();
                case "list" -> outputList();
                case "reload" -> reloaded();
            }
        } else {
            error();
        }
    }
    private void reloaded() {
        StaffManager manager = Managment.STAFF_MANAGER;
        manager.reload();
        sendMessage(TextFormatting.GREEN + "Список был перезагружен");
    }
    private void addStaffName(String name) {
        StaffManager manager = Managment.STAFF_MANAGER;

        if (manager.getStaffNames().contains(name)) {
            sendMessage(TextFormatting.RED + "Этот игрок уже в Staff List!");
        } else {
            manager.addStaff(name);
            sendMessage(TextFormatting.GREEN + "Ник " + TextFormatting.WHITE + name + TextFormatting.GREEN + " добавлен в Staff List");
        }
    }

    private void removeStaffName(String name) {
        StaffManager manager = Managment.STAFF_MANAGER;

        if (manager.getStaffNames().contains(name)) {
            manager.removeStaff(name);
            sendMessage(TextFormatting.GREEN + "Ник " + TextFormatting.WHITE + name + TextFormatting.GREEN + " удален из Staff List");
        } else {
            sendMessage(TextFormatting.RED + "Этого игрока нет в Staff List!");
        }
    }

    private void clearList() {
        StaffManager manager = Managment.STAFF_MANAGER;

        if (manager.getStaffNames().isEmpty()) {
            sendMessage(TextFormatting.RED + "Staff List пуст!");
        } else {
            manager.clearStaffs();
            sendMessage(TextFormatting.GREEN + "Staff List очищен");
        }
    }

    private void outputList() {
        StaffManager manager = Managment.STAFF_MANAGER;

        sendMessage(TextFormatting.GRAY + "Список Staff:");

        for (String name : manager.getStaffNames()) {
            sendMessage(TextFormatting.WHITE + name);
        }
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "Ошибка в использовании" + TextFormatting.WHITE + ":");
        sendMessage(TextFormatting.WHITE + ".staff " + TextFormatting.GRAY + "<" + TextFormatting.RED + "add; remove; clear; list." + TextFormatting.GRAY + ">");
        sendMessage(TextFormatting.WHITE + "." + "staff reload" + TextFormatting.GRAY + " - перезагрузить список");
    }
}
