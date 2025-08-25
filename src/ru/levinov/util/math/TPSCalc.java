package ru.levinov.util.math;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.math.MathHelper;
import ru.levinov.events.impl.packet.EventPacket;

@Getter
public class TPSCalc {

    private static float TPS = 20;

    private static long timestamp;
    public static float adjustTicks = 0;

    public static float adjustTicks() {
        updateTPS();
        return 0;
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SUpdateTimePacket) {
            updateTPS();
        }
    }

    private static void updateTPS() {
        long delay = System.nanoTime() - timestamp;

        float maxTPS = 20;
        float rawTPS = maxTPS * (1e9f / delay);

        float boundedTPS = MathHelper.clamp(rawTPS, 0, maxTPS);

        TPS = (float) round(boundedTPS);

        adjustTicks = boundedTPS - maxTPS;

        timestamp = System.nanoTime();
    }

    public static double round(
            final double input
    ) {
        return Math.round(input * 100.0) / 100.0;
    }
}