package ru.levinov.events.impl.render;

import net.optifine.util.RenderChunkUtils;
import ru.levinov.events.Event;

public class EventRenderChunkContainer extends Event {
    private RenderChunkUtils renderChunk;

    public EventRenderChunkContainer(RenderChunkUtils renderChunk) {
        this.renderChunk = renderChunk;
    }

    public RenderChunkUtils getRenderChunk() {
        return this.renderChunk;
    }
}
