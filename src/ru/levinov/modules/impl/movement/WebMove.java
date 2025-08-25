package ru.levinov.modules.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.movement.MoveUtil;

@FunctionAnnotation(
        name = "NoWeb",
        type = Type.Movement,
        desc = "Ходьба в паутине",
        keywords = {"WebMove"}
)
public class WebMove extends Function {

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            BlockPos playerPos = mc.player.getPosition();
            Block block = mc.world.getBlockState(playerPos).getBlock();

            if (block == Blocks.COBWEB) {
                // Установите скорость движения
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.motion.y = 1.0f;
                } else {
                    MoveUtil.setMotion(0.30f);
                }

                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motion.y = -0.9f;
                } else {
                    MoveUtil.setMotion(0.30f);
                }
            }
        }
    }
}
