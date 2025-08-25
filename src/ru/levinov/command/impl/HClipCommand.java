package ru.levinov.command.impl;

import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.glfw.GLFW;
import ru.levinov.command.Cmd;
import ru.levinov.command.Command;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;

import static ru.levinov.util.IMinecraft.mc;

@Cmd(
        name = "hclip",
        description = "Телепортирует вас вперед."
)
public class HClipCommand extends Command {
    public HClipCommand() {
    }

    public void run(String[] args) throws Exception {
        Vector3d tp = Minecraft.getInstance().player.getLook(1.0F).mul(Double.parseDouble(args[1]), 0.0, Double.parseDouble(args[1]));

        int i;
        for(i = 0; i < 10; ++i) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + tp.getX(), mc.player.getPosY(), mc.player.getPosZ() + tp.getZ(), false));
        }

        for(i = 0; i < 10; ++i) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + tp.getX(), mc.player.getPosY(), mc.player.getPosZ() + tp.getZ(), false));
        }

        mc.player.setPosition(mc.player.getPosX() + tp.getX(), mc.player.getPosY(), mc.player.getPosZ() + tp.getZ());
    }

    public void error() {
    }
}
