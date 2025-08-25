package ru.levinov.ui.dropdownGUI.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector4i;
import ru.levinov.modules.settings.imp.ColorSetting;
import ru.levinov.ui.dropdownGUI.objects.ModuleObject;
import ru.levinov.ui.dropdownGUI.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.awt.*;

public class ColorObject extends Object {
    public ModuleObject object;
    public ColorSetting setting;
    private float hue;
    private float saturation;
    private float brightness;
    private float alpha;
    private boolean colorSelectorDragging;
    private boolean hueSelectorDragging;
    private boolean alphaSelectorDragging;

    public ColorObject(ModuleObject object, ColorSetting setting) {
        height = 11;
        this.object = object;
        this.setting = setting;
        float[] hsb = RGBtoHSB(setting.get());
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];

        this.alpha = (setting.get() >> 24 & 0xFF) / 255F;
    }

    protected boolean extended;
    
    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        height = (extended ? 88 : 5.5F);
        Fonts.durman[12].drawString(stack, setting.getName(), x + 12.5f, y + 2.4f, -1);
        RenderUtil.Render2D.drawRoundedRect(x + width - 21, y - 1, 9, 9, 2.5F, setting.get());
        RenderUtil.Render2D.drawRoundOutline(x + width - 21, y - 1, 9, 9, 2.5F, 0f, ColorUtil.rgba(25, 26, 33, 0), new Vector4i(new Color(73, 73, 73, 128).getRGB(), new Color(73, 73, 73, 128).getRGB(), new Color(73, 73, 73, 128).getRGB(), new Color(73, 73, 73, 128).getRGB()));

        if (extended) {
            if (colorSelectorDragging && RenderUtil.isInRegion(mouseX, mouseY, x + 11.5f, y + 12, 90 - 15 - 2, 60)) {
                float xDifference = mouseX - (x + 5);
                saturation = xDifference / 90;
                float yDifference = mouseY - (y + 12);
                brightness = 1 - yDifference / 60;
                setting.setValue(ColorUtil.applyOpacity(new Color(Color.HSBtoRGB(hue, saturation, brightness)), alpha).getRGB());
            }
            //Тута
            RenderUtil.Render2D.drawGradientRound(x + 11.5f, y + 12, 90 - 15 - 2, 60, 1, -1, Color.BLACK.getRGB(), Color.HSBtoRGB(hue, 1, 1), Color.BLACK.getRGB());
            float selectorX = x + 5 + saturation * (90 - 3);
            float selectorY = y + 10 + (1 - brightness) * 60;

            RenderUtil.Render2D.drawRoundedRect(selectorX, selectorY, 4, 4, 2, -1);
            RenderUtil.Render2D.drawRoundOutline(selectorX, selectorY, 4, 4, 2, 0f, ColorUtil.rgba(25, 26, 33, 0), new Vector4i(new Color(78, 76, 76, 128).getRGB(), new Color(78, 76, 76, 128).getRGB(), new Color(78, 76, 76, 128).getRGB(), new Color(78, 76, 76, 128).getRGB()));

            if (hueSelectorDragging && RenderUtil.isInRegion(mouseX, mouseY, x + 5, y, 90, extended ? 93 : 11)) {
                float xDifference = mouseX - (x + 5);
                hue = xDifference / 90;
                setting.setValue(ColorUtil.applyOpacity(new Color(Color.HSBtoRGB(hue, saturation, brightness)), alpha).getRGB());
            }

            float times = 5;
            float size = 91 / times;
            float sliderX = x + 5;
            float sliderY = y + 77;

            float hueSelectorX = hue * 86;

            for (int i = 0; i < times; i++) {
                boolean last = i == 4;
                if (last) {
                    size--;
                }
                int color1 = Color.HSBtoRGB(0.2F * i, 1, 1);
                int color2 = Color.HSBtoRGB(0.2F * (i + 1), 1, 1);

                if (i == 0) {
                    RenderUtil.Render2D.drawGradientRound(sliderX, sliderY, size, 5, 4, 4, 0, 0, color1);
                } else if (last) {
                    RenderUtil.Render2D.drawGradientRound(sliderX, sliderY, size, 5, 0, 0, 4, 4, color1);
                } else {
                    RenderUtil.Render2D.drawGradientRound(sliderX, sliderY, size, 5,2, color1, color1, color2, color2);
                }

                if (!last) {
                    sliderX += size;
                }
            }

            RenderUtil.Render2D.drawRoundedRect(x + 5 + hueSelectorX, sliderY, 5, 5, 2, -1);

            int color = Color.HSBtoRGB(hue, saturation, brightness);

            if (alphaSelectorDragging && RenderUtil.isInRegion(mouseX, mouseY, x + 5, y, 90, extended ? 93 : 11)) {
                float xDifference = mouseX - (x + 5);
                alpha = xDifference / 90;
                setting.set(ColorUtil.applyOpacity(new Color(color), alpha).getRGB());
            }

            RenderUtil.Render2D.drawGradientRound(x + 5, y + 85, 90, 5, 2,
                    -1, -1, color, color);

            float alphaSelectorX = alpha * 86;
            RenderUtil.Render2D.drawRoundedRect(x + 5 + alphaSelectorX, y + 85, 5, 5, 2, -1);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isInRegion(mouseX, mouseY, x, y, width, 11) && mouseButton == 1) {
            extended = !extended;
        }

        if (!colorSelectorDragging && RenderUtil.isInRegion(mouseX, mouseY, x + 5, y + 12, 90, 60) && mouseButton == 0) {
            colorSelectorDragging = true;
        }

        if (!hueSelectorDragging && RenderUtil.isInRegion(mouseX, mouseY, x + 5, y + 77, 90, 5) && mouseButton == 0) {
            hueSelectorDragging = true;
        }

        if (!alphaSelectorDragging && RenderUtil.isInRegion(mouseX, mouseY, x + 5, y + 85, 90, 5) && mouseButton == 0) {
            alphaSelectorDragging = true;
        }
    }

    private float[] RGBtoHSB(int color) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        return Color.RGBtoHSB(red, green, blue, null);
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        colorSelectorDragging = false;
        hueSelectorDragging = false;
        alphaSelectorDragging = false;
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
    }
}
