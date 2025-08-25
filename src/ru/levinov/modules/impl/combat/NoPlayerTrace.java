package ru.levinov.modules.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(name = "NoPlayerTrace", type = Type.Combat,desc = "Убирает хит-бокс игрока")
public class NoPlayerTrace extends Function {

    @Override
    public void onEvent(final Event event) {
        handleEvent(event);
    }

    private void handleEvent(Event event) {
        if (!(event instanceof EventRender && ((EventRender) event).isRender3D()))
            return;
        adjustBoundingBoxesForPlayers();
    }

    private void adjustBoundingBoxesForPlayers() {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (shouldSkipPlayer(player))
                continue;

            float sizeMultiplier = 0 * 0.0F;
            setBoundingBox(player, sizeMultiplier);
        }
    }

    private boolean shouldSkipPlayer(PlayerEntity player) {
        return player == mc.player || !player.isAlive();
    }

    private void setBoundingBox(Entity entity, float size) {
        AxisAlignedBB newBoundingBox = calculateBoundingBox(entity, size);
        entity.setBoundingBox(newBoundingBox);
    }

    private AxisAlignedBB calculateBoundingBox(Entity entity, float size) {
        double minX = entity.getPosX() - size;
        double minY = entity.getBoundingBox().minY;
        double minZ = entity.getPosZ() - size;
        double maxX = entity.getPosX() + size;
        double maxY = entity.getBoundingBox().maxY;
        double maxZ = entity.getPosZ() + size;
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}