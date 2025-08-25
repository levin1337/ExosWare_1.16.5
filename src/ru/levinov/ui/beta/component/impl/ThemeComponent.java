package ru.levinov.ui.beta.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector4f;
import ru.levinov.managment.Managment;
import ru.levinov.ui.beta.component.ColorThemeWindow;
import ru.levinov.ui.midnight.Style;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.awt.*;

public class ThemeComponent extends Component {

    public Style config;
    public ColorThemeWindow[] colors = new ColorThemeWindow[2];
    public static ColorThemeWindow selected = null;

    public boolean opened;


    public ThemeComponent(Style config) {
        this.config = config;
    }

    @Override
    public void onConfigUpdate() {
        super.onConfigUpdate();
        for (ColorThemeWindow colorThemeWindow : colors) {
            if (colorThemeWindow != null)
                colorThemeWindow.onConfigUpdate();
        }

        if (config.name.equalsIgnoreCase("Свой цвет")) {
            if (colors.length >= 2) {
                colors[0] = new ColorThemeWindow(new Color(config.colors[0]), new Vector4f(0, 0, 0, 0));
                colors[1] = new ColorThemeWindow(new Color(config.colors[1]), new Vector4f(0, 0, 0, 0));
            }
        }

    }

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (opened) {
            height += 12;
        }
        if (!(colors[0] == null && colors[1] == null)) {
            config.colors[0] = colors[0].getColor();
            config.colors[1] = colors[1].getColor();
        }

        int color1 = config.name.contains("Разноцветный") ? ColorUtil.astolfo(10, 0, 0.7f, 1, 1) : config.getColor(0);
        int color2 = config.name.contains("Разноцветный") ? ColorUtil.astolfo(10, 90, 0.7f, 1, 1) : config.getColor(90);
        int color3 = config.name.contains("Разноцветный") ? ColorUtil.astolfo(10, 180, 0.7f, 1, 1) : config.getColor(180);
        int color4 = config.name.contains("Разноцветный") ? ColorUtil.astolfo(10, 270, 0.7f, 1, 1) : config.getColor(270);

        RenderUtil.Render2D.drawRoundedCorner(x + 3, y, width - 220, height, 5, Managment.STYLE_MANAGER.getCurrentStyle() == config ? new Color(32, 36, 42).brighter().getRGB() : new Color(32, 36, 42).getRGB());
        RenderUtil.Render2D.drawGradientRound(x + width - 320, y + 2.5f, 100, 15, 5, color1, color2, color3, color4);

        Fonts.gilroy[16].drawString(matrixStack, config.name, x + 8, y + 8, -1);

        if (opened) {
            RenderUtil.Render2D.drawRect(x + 10, y + 19, 8, 8, colors[0].getColor());
            RenderUtil.Render2D.drawRect(x + 10 + 12, y + 19, 8, 8, colors[1].getColor());
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY, 25)) {
            Managment.STYLE_MANAGER.setCurrentStyle(config);
        }

        if (selected != null)
            selected.click(mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (selected != null)
            selected.unclick(mouseX, mouseY);
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void charTyped(char codePoint, int modifiers) {

    }
}
