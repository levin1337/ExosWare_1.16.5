package ru.levinov.command.impl;

import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;

@Cmd(
        name = "vclip",
        description = "Телепортирует вас вверх"
)
public class VClipCommand extends Command {
    public VClipCommand() {
    }

    public void run(String[] args) throws Exception {
        int i;

        for(i = 0; i < 19; ++i) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
        }

        for(i = 0; i < 19; ++i) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + Double.parseDouble(args[1]), mc.player.getPosZ(), false));
        }

        mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() + Double.parseDouble(args[1]), mc.player.getPosZ());
    }

    public void error() {
        this.sendMessage(TextFormatting.GRAY + "Ошибка в использовании" + TextFormatting.WHITE + ":");
        this.sendMessage(".vclip y - <100>" + TextFormatting.GRAY);
    }
}
