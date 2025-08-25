package ru.levinov.events.impl.world;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.optifine.util.RenderChunkUtils;
import ru.levinov.events.Event;

public class EventRenderWorld extends Event {
    private float partialTicks;
    private MatrixStack matrixStack;
    private ActiveRenderInfo activeRenderInfo;

    public EventRenderWorld(MatrixStack matrixStack, ActiveRenderInfo activeRenderInfo, float partialTicks) {
        this.matrixStack = matrixStack;
        this.activeRenderInfo = activeRenderInfo;
        this.partialTicks = partialTicks;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public ActiveRenderInfo getActiveRenderInfo() {
        return this.activeRenderInfo;
    }

    public void setPartialTicks(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}