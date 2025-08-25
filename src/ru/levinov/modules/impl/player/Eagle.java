package ru.levinov.modules.impl.player;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(name = "Eagle", type = Type.Misc,desc = "Ўифт на краю блока")
public class Eagle extends Function {

    private boolean sneaked;
    private int ticksOverEdge;
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion eventUpdate) {
            if (mc.player.isOnGround() && blockRelativeToPlayer(0.0, -1.0, 0.0) instanceof AirBlock) {
                mc.gameSettings.keyBindSneak.setPressed(true);
            } else {
                mc.gameSettings.keyBindSneak.setPressed(false);
            }
        }
    }
    public static Block blockRelativeToPlayer(double offsetX, double offsetY, double offsetZ) {
        return mc.world.getBlockState((new BlockPos(mc.player.getPositionVec())).add(offsetX, offsetY, offsetZ)).getBlock();
    }
    protected void onDisable() {
        super.onDisable();
        if (this.sneaked) {
            this.sneaked = false;
        }
    }
}