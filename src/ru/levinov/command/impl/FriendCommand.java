package ru.levinov.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Cmd;
import ru.levinov.command.Command;
import ru.levinov.managment.friend.Friend;
import ru.levinov.managment.friend.FriendManager;
import ru.levinov.managment.Managment;

/**
 * @author levin1337
 * @since 27.06.2023
 */
@Cmd(name = "friend", description = "���������� ��������")
public class FriendCommand extends Command {

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            switch (args[1]) {
                case "add" -> {
                    final String friendName = args[2];
                    addFriend(friendName);
                }
                case "remove" -> {
                    final String friendName = args[2];
                    removeFriend(friendName);
                }
                case "list" -> friendList();
                case "clear" -> clearFriendList();
            }
        } else {
            error();
        }
    }

    /**
     * ������� ���������� ������ � ������
     *
     * @param friendName ��� ������
     */
    private void addFriend(final String friendName) {
        final FriendManager friendManager = Managment.FRIEND_MANAGER;

        if (friendName.contains(Minecraft.getInstance().player.getName().getString())) {
            sendMessage("� ��������� �� �� ������ �������� ������ ���� � ������ :(");
            return;
        }
        if (friendManager.getFriends().stream().map(Friend::getName).toList().contains(friendName)) {
            sendMessage(friendName + " ��� ���� � ������ ������");
            return;
        }

        sendMessage(friendName + " ������� �������� � ������ ������!");
        friendManager.addFriend(friendName);
    }

    /**
     * ������� �������� ������ �� ������ ������
     *
     * @param friendName ��� ������
     */
    private void removeFriend(final String friendName) {
        final FriendManager friendManager = Managment.FRIEND_MANAGER;

        if (friendManager.isFriend(friendName)) {
            friendManager.removeFriend(friendName);
            sendMessage(friendName + " ��� ������ �� ������ ������");
            return;
        }
        sendMessage(friendName + " ���� � ������ ������");
    }

    /**
     * ������� ������ ����� ���� ������
     */
    private void friendList() {
        final FriendManager friendManager = Managment.FRIEND_MANAGER;

        if (friendManager.getFriends().isEmpty()) {
            sendMessage("������ ������ ����");
            return;
        }

        sendMessage("������ ������:");
        for (Friend friend : friendManager.getFriends()) {
            sendMessage(TextFormatting.GRAY + friend.getName());
        }
    }

    /**
     * ������� ������ ������
     */
    private void clearFriendList() {
        final FriendManager friendManager = Managment.FRIEND_MANAGER;

        if (friendManager.getFriends().isEmpty()) {
            sendMessage("������ ������ ����");
            return;
        }

        friendManager.clearFriend();
        sendMessage("������ ������ ������� ������");
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "������ � �������������" + TextFormatting.WHITE + ":");
        sendMessage(TextFormatting.WHITE + "." + "friend add " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "name" + TextFormatting.GRAY + ">");
        sendMessage(TextFormatting.WHITE + "." + "friend remove " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "name" + TextFormatting.GRAY + ">");
        sendMessage(TextFormatting.WHITE + "." + "friend list" + TextFormatting.GRAY + " - ���������� ������ ���� ��������");
        sendMessage(TextFormatting.WHITE + "." + "friend clear" + TextFormatting.GRAY + " - ������� ���� ��������");
    }
}
