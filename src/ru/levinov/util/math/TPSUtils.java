package ru.levinov.util.math;

import com.google.common.eventbus.Subscribe;
import net.minecraft.util.math.MathHelper;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventUpdate;

import java.util.Arrays;

public class TPSUtils {
    protected static final float[] ticks = new float[20];
    protected int index;
    protected long lastPacketTime;

    public TPSUtils() {
        this.index = 0;
        this.lastPacketTime = -1L;
        Arrays.fill(ticks, 0.0F);
    }

    public static float getTPS() {
        float numTicks = 0.0F;
        float sumTickRates = 0.0F;
        for (float tickRate : ticks) {
            if (tickRate > 0.0F) {
                sumTickRates += tickRate;
                numTicks++;
            }
        }
        return MathHelper.clamp(0.0F, 20.0F, sumTickRates / numTicks);
    }

    private void update() {
        if (this.lastPacketTime != -1L) {
            float timeElapsed = (float) (System.currentTimeMillis() - this.lastPacketTime) / 1000.0F;
            ticks[this.index % ticks.length] = MathHelper.clamp(0.0F, 20.0F, 20.0F / timeElapsed);
            this.index++;
        }
        this.lastPacketTime = System.currentTimeMillis();
    }

    @Subscribe
    public void onEvent(Event event) {
        if (event instanceof EventPacket e) {
            update();
        }
    }
}
