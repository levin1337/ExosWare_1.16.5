package ru.levinov.events.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import ru.levinov.events.Event;

public class ContainerRenderEvent extends Event {
    public final MatrixStack matrix;
    public final ITextComponent title;
    public final Container container;

    public ContainerRenderEvent(MatrixStack matrix, ITextComponent title, Container container) {
        this.matrix = matrix;
        this.title = title;
        this.container = container;
    }
}
