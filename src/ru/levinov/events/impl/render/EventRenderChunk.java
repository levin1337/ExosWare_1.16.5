package ru.levinov.events.impl.render;

import net.optifine.util.RenderChunkUtils;
import ru.levinov.events.Event;

public class EventRenderChunk extends Event {
    private RenderChunkUtils renderChunk;

    public RenderChunkUtils getRenderChunk() {
        return this.renderChunk;
    }

    public EventRenderChunk(RenderChunkUtils renderChunk) {
        this.renderChunk = renderChunk;
    }
}

