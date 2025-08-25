package ru.levinov.events.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.Entity;
import ru.levinov.events.Event;

public class EntityRenderMatrixEvent extends Event {
    private final MatrixStack matrix;
    private final Entity entity;

    public MatrixStack getMatrix() {
        return this.matrix;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public EntityRenderMatrixEvent(MatrixStack matrix, Entity entity) {
        this.matrix = matrix;
        this.entity = entity;
    }
}
