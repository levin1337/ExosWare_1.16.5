package ru.levinov.events.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.ActiveRenderInfo;
import ru.levinov.events.Event;

public final class Render2DNur extends Event {
    private final MatrixStack matrix;
    private final ActiveRenderInfo activeRenderInfo;
    private final MainWindow mainWindow;
    private final float partialTicks;

    public Render2DNur(float partialTicks) {
        this.partialTicks = partialTicks;
        this.matrix = null;
        this.activeRenderInfo = null;
        this.mainWindow = null;
    }

    public MatrixStack getMatrix() {
        return this.matrix;
    }

    public ActiveRenderInfo getActiveRenderInfo() {
        return this.activeRenderInfo;
    }

    public MainWindow getMainWindow() {
        return this.mainWindow;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public Render2DNur(MatrixStack matrix, ActiveRenderInfo activeRenderInfo, MainWindow mainWindow, float partialTicks) {
        this.matrix = matrix;
        this.activeRenderInfo = activeRenderInfo;
        this.mainWindow = mainWindow;
        this.partialTicks = partialTicks;
    }
}
