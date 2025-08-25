package ru.levinov.util.movement;

import net.minecraft.client.Minecraft;

public class GCDfix {

    public static float getFixedRotation(float rot) {
        return getDeltaMouse(rot) * getGCDValue();
    }

    public static float getGCDValue() {
        return (float)((double)getGCD() * 0.15);
    }

    public static float getGCD() {
        float f1;
        return (f1 = (float)(Minecraft.getInstance().gameSettings.mouseSensitivity * 0.6 + 0.2)) * f1 * f1 * 8.0F;
    }

    public static float getDeltaMouse(float delta) {
        return (float)Math.round(delta / getGCDValue());
    }
}
