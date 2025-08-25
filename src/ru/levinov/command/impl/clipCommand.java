package ru.levinov.command.impl;

import net.minecraft.block.Blocks;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;

@Cmd(
        name = "clip",
        description = "Телепорт вверх/вниз"
)
public class clipCommand extends Command {
    float y = 3.0F;

    public clipCommand() {
    }

    public void run(String[] args) throws Exception {
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "up" -> this.up();
                case "down" -> this.down();
            }
        }

    }

    public void error() {
    }

    private void up() {
        int i;
        for(i = 3; i < 255; ++i) {
            if (mc.world.getBlockState((new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ())).add(0, i, 0)) == Blocks.AIR.getDefaultState()) {
                this.y = (float)(i + 1);
                break;
            }
        }

        for(i = 0; (float)i < Math.max(this.y / 1000.0F, 3.0F); ++i) {
        }

        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + (double)this.y, mc.player.getPosZ(), false));
        mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() + (double)this.y, mc.player.getPosZ());
    }

    private void down() {
    }

}
