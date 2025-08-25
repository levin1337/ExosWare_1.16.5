package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Cmd(name = "rg", description = "Создание регионов по координатах")
public class RegionCreateCommand extends Command {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static String direction1 = "Ly9wb3Mx";
    static byte[] direct = Base64.getDecoder().decode(direction1);
    static String directionDOX = new String(direct);
    static String direction2 = "Ly9wb3My";
    static byte[] direct2 = Base64.getDecoder().decode(direction2);
    static String directionSWAT = new String(direct2);
    static String rglist = "L3JnIGNsYWlt";
    static byte[] ratka = Base64.getDecoder().decode(rglist);
    static String ratkaget = new String(ratka);

    //УЖЕ НЕ ВОРК
    //УЖЕ НЕ ВОРК
    //УЖЕ НЕ ВОРК
    //УЖЕ НЕ ВОРК    //УЖЕ НЕ ВОРК
    //УЖЕ НЕ ВОРК
    //УЖЕ НЕ ВОРК

    //УЖЕ НЕ ВОРК
    @Override
    public void run(String[] args) throws Exception {
        if (args.length == 6) {
            try {
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                int radius = Integer.parseInt(args[4]);
                String name = args[5];
                int x1 = x - radius;
                int z1 = z - radius;
                int x2 = x + radius;
                int z2 = z + radius;
                scheduler.schedule(() -> mc.player.sendChatMessage(directionDOX + " " + x1 + "," + y + "," + z1), 0, TimeUnit.SECONDS);
                scheduler.schedule(() -> mc.player.sendChatMessage(directionSWAT + " " + x2 + "," + y + "," + z2), 3, TimeUnit.SECONDS);
                scheduler.schedule(() -> mc.player.sendChatMessage(ratkaget + " " + name), 6, TimeUnit.SECONDS);
            } catch (NumberFormatException e) {
                sendMessage(TextFormatting.RED + "Ошибка: координаты и радиус должны быть целыми числами.");
            }
        } else {
            error();
        }
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "Ошибка в использовании" + TextFormatting.WHITE + ":");
        sendMessage(".rg <x> <y> <z> <radius> <name>" + TextFormatting.GRAY);
    }
}

