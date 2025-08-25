package ru.levinov.ui.midnight;


import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.awt.*;

import static ru.levinov.ui.midnight.StyleManager.astolfo;

public class Style {
    public String name;
    public int[] colors;

    public Style(String name, int... colors) {
        this.name = name;
        this.colors = colors;
    }


    public int getColor(int index) {
        if (name.equals("Разно цветный")) {
            return ColorUtil.astolfo(10,index, 0.5F, 1.0F, 1.0F);
        }
        return ColorUtil.gradient(5, index, colors);
    }

}