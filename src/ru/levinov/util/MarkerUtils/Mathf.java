package ru.levinov.util.MarkerUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;

public class Mathf {
    public static double clamp(double min, double max, double n) {
        return Math.max(min, Math.min(max, n));
    }

    public static double round(double num, double increment) {
        double v = (double)Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static float randomizeFloat(float min, float max) {
        return (float)((double)min + (double)(max - min) * Math.random());
    }
    public static float getRandomFloat(float max, float min) {
        SecureRandom random = new SecureRandom();
        return random.nextFloat() * (max - min) + min;
    }
    public static double deltaTime() {
        return Minecraft.debugFPS > 0 ? 1.0 / (double)Minecraft.debugFPS : 1.0;
    }
    public static float lerp(float end, float start, float multiple) {
        return (float)((double)end + (double)(start - end) * MathHelper.clamp(Mathf.deltaTime() * (double)multiple, 0.0, 1.0));
    }

    public double lerp(double end, double start, double multiple) {
        return end + (start - end) * MathHelper.clamp(Mathf.deltaTime() * multiple, 0.0, 1.0);
    }

    public static Vector3d fast(Vector3d end, Vector3d start, float multiple) {
        return new Vector3d(Mathf.fast((float)end.getX(), (float)start.getX(), multiple), Mathf.fast((float)end.getY(), (float)start.getY(), multiple), Mathf.fast((float)end.getZ(), (float)start.getZ(), multiple));
    }

    public static float fast(float end, float start, float multiple) {
        return (1.0f - MathHelper.clamp((float)(Mathf.deltaTime() * (double)multiple), 0.0f, 1.0f)) * end + MathHelper.clamp((float)(Mathf.deltaTime() * (double)multiple), 0.0f, 1.0f) * start;
    }
    public static float random(float f, float f2) {
        return (float)(Math.random() * (double)(f2 - f) + (double)f);
    }
    public static float randomize(float min, float max) {
        return (float) (min + (max - min) * Math.random());
    }
}

