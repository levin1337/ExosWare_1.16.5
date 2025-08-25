package ru.levinov.command.impl;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.math.NumberUtils;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;

@Cmd(name = "tp", description = "�������� � ����� �����")
public class TPCommand extends Command {

    @Override
    public void run(String[] args) throws Exception {
        if (!NumberUtils.isNumber(args[1])) {
            PlayerEntity entityPlayer = mc.world.getPlayers()
                    .stream()
                    .filter(player -> player.getName().getString().equalsIgnoreCase(args[1]))
                    .findFirst().orElse(null);

            if (entityPlayer == null) {
                sendMessage(TextFormatting.RED + "�� ������� ����� ������ � ����� ���������!");
                return;
            }

            if (args[1].equals(entityPlayer.getName().getString())) {
                int x = (int) entityPlayer.getPosX();
                int y = (int) entityPlayer.getPosY();
                int z = (int) entityPlayer.getPosZ();

                int i;
                for(i = 0; i < 19; ++i) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
                }

                for(i = 0; i < 19; ++i) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + 1, z, false));
                }

                mc.player.setPosition(x, y + 1, z);



                sendMessage("������������ � ������ " + TextFormatting.GRAY + entityPlayer.getName().getString());

            }
        }
        if (NumberUtils.isNumber(args[1])) {
            if (args.length >= 2) {
                double x = 0, y = 0, z = 0;
                if (args.length == 4) {
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                    z = Double.parseDouble(args[3]);
                    sendMessage("������� ����������������� �� " + TextFormatting.LIGHT_PURPLE + args[1] + " " + args[2] + " " + args[3]);
                } else if (args.length == 3) {
                    x = Double.parseDouble(args[1]);
                    y = 150;
                    z = Double.parseDouble(args[2]);
                    sendMessage("������� ����������������� �� " + TextFormatting.LIGHT_PURPLE + args[1] + " " + args[2]);
                } else if (args.length == 2) {
                    x = mc.player.getPosX();
                    y = mc.player.getPosY() + Double.parseDouble(args[1]);
                    z = mc.player.getPosZ();
                    sendMessage(TextFormatting.GREEN + "�� ������� ����������������� �� " + TextFormatting.WHITE + args[1] + TextFormatting.GREEN + " ������ �����");
                } else {
                    error();
                }


                int i;
                for(i = 0; i < 10; ++i) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
                }

                for(i = 0; i < 10; ++i) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
                }

                mc.player.setPosition(x, y, z);
                
                
            } else {
                error();
            }
        }
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "������ � �������������" + TextFormatting.WHITE + ":");

        sendMessage(".tp" + TextFormatting.GRAY + " <" + "x" + TextFormatting.GRAY + "> " + TextFormatting.GRAY + "<" + "y" + TextFormatting.GRAY + "> " + TextFormatting.GRAY + "<" + "z" + TextFormatting.GRAY + ">");

        sendMessage(".tp" + TextFormatting.GRAY + " <" + "x" + TextFormatting.GRAY + "> " + TextFormatting.GRAY + "<" + "z" + TextFormatting.GRAY + ">");

        sendMessage(".tp" + TextFormatting.GRAY + " <" + "y" + TextFormatting.GRAY + ">");
        sendMessage(".tp" + TextFormatting.GRAY + "<������� ������>");

    }
}
