package ru.levinov.ui.midnight;

import net.minecraft.util.math.MathHelper;
import ru.levinov.command.impl.ThemeCommand;
import ru.levinov.util.render.ColorUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StyleManager {
    public static List<Style> styles = new ArrayList<>();
    private static Style currentStyle = null;

    public void init() {
        styles.addAll(Arrays.asList(
                        new Style("Клиентский", HexColor.toColor("#0000CD"), HexColor.toColor("#87CEEB")),
                        new Style("Пурпурно фиолетовый", HexColor.toColor("#00923f"), HexColor.toColor("#0d0fee"),HexColor.toColor("#ffd332")),
                        new Style("Романтика", HexColor.toColor("#f2039e"), HexColor.toColor("#0327f2")),
                        new Style("Радужное мерцание", HexColor.toColor("#FF00AA"), HexColor.toColor("#AA00FF")),

                        new Style("Осень", HexColor.toColor("#FF7F00"), HexColor.toColor("#FFA500"), HexColor.toColor("#FFD700")),
                        new Style("Зима", HexColor.toColor("#808080"), HexColor.toColor("#ADD8E6"), HexColor.toColor("#FFFFFF")),
                        new Style("Лето", HexColor.toColor("#00FFFF"), HexColor.toColor("#00FF7F"), HexColor.toColor("#00FF00")),
                        new Style("Весна", HexColor.toColor("#FFFF00"), HexColor.toColor("#FFDAB9"), HexColor.toColor("#FF69B4")),
                        new Style("Ночь", HexColor.toColor("#191970"), HexColor.toColor("#884C41")),


                        new Style("Свой цвет", HexColor.toColor("#765AA5"), HexColor.toColor("#F4ECFF"))
                )
        );
        currentStyle = styles.get(0);
    }

    public static Color astolfo(float yDist, float yTotal, float saturation, float speedt) {
        float speed = 1800f;
        float hue = (System.currentTimeMillis() % (int) speed) + (yTotal - yDist) * speedt;
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return Color.getHSBColor(hue, saturation, 1F);
    }

    public void setCurrentStyle(Style style) {
        currentStyle = style;
    }

    public static Style getCurrentStyle() {
        if (currentStyle == null) {
            System.out.println("Color null - " + currentStyle);
        }
        return currentStyle;
    }

    public static class HexColor {
        public static int toColor(String hexColor) {
            int argb = Integer.parseInt(hexColor.substring(1), 16);
            return reAlphaInt(argb, 255);
        }

        public static int reAlphaInt(final int color, final int alpha) {
            return (MathHelper.clamp(alpha, 0, 255) << 24) | (color & 16777215);
        }
    }
}
