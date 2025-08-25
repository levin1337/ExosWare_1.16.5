package ru.levinov.events.impl.render;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Matrix4f;
import ru.levinov.events.Event;

public class EventRender2 extends Event {
    public float partialTicks;
    public ActiveRenderInfo activeRenderInfo;
    public MainWindow scaledResolution;
    public Type type;
    public MatrixStack matrixStack;
    public Matrix4f matrix;


    public EventRender2(float partialTicks, MatrixStack matrixStack, MainWindow mainWindow, Type type, Matrix4f matrix, ActiveRenderInfo activeRenderInfo) {
        this.partialTicks = partialTicks;
        this.scaledResolution = mainWindow;
        this.matrixStack = matrixStack;
        this.type = type;
        this.matrix = matrix;
        this.activeRenderInfo = activeRenderInfo;
    }

    public boolean isRender3D() {
        return this.type == Type.RENDER3D;
    }
    public boolean isWorld3D() {
        return type == Type.WORLD3D;
    }


    public enum Type {
        RENDER3D,WORLD3D
    }
}