package ru.levinov.ui.dropdownGUI.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector2i;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.ui.dropdownGUI.objects.ModuleObject;
import ru.levinov.ui.dropdownGUI.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.util.Random;

public class ModeObject extends ru.levinov.ui.dropdownGUI.objects.Object {
    public ModeSetting set;
    public ModuleObject object;

    public ModeObject(ModuleObject object, ModeSetting set) {
        this.object = object;
        this.set = set;
        setting = set;
    }

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        int offset = 0;
        float offsetY = 0;
        int lines = 1;
        float size = 0;
        for (String mode : set.modes) {

            float preOffset = size + Fonts.msSemiBold[11].getWidth(mode) + 3;
            if (preOffset > width - 20) {
                break;
            }
            size += Fonts.msSemiBold[11].getWidth(mode) + 3;
        }

        for (String mode : set.modes) {
            float preOffset = offset + Fonts.msSemiBold[11].getWidth(mode) + 3;
            if (preOffset > size) {
                lines++;
                offset = 0;
            }
            offset += Fonts.msSemiBold[11].getWidth(mode) + 3;
        }

        height += 5;
        Fonts.msMedium[13].drawString(stack, set.getName(), x + 12, y + height / 2f - 5.5f, ColorUtil.rgba(240, 240, 240, 255));

        RenderUtil.Render2D.drawRoundedRect(x + 10.5f, y + 10, 105F, 11f * lines, 3f, ColorUtil.rgba(0, 0, 0, 180));
        height += 11 * (lines - 1);
        offset = 0;
        offsetY = 0;
        int i = 0;
        for (String mode : set.modes) {

            float preOff = offset + Fonts.msSemiBold[11].getWidth(mode) + 3;
            if (preOff > size) {
                offset = 0;
                offsetY += 10;
            }
            if (set.getIndex() == i) {
                Fonts.msSemiBold[11].drawString(stack, mode, x + 13.6f + offset, y + 15.5f + offsetY, -1);
            } else
                Fonts.msSemiBold[11].drawString(stack, mode, x + 13.6f + offset, y + 15.5f + offsetY, ColorUtil.rgba(163, 163, 163, 255));
            offset += Fonts.msSemiBold[11].getWidth(mode) + 3;
            i++;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float offset = -4;
        float offsetY = 0;
        int i = 0;
        float size = 0;
        if (object.module.expanded) {
        for (String mode : set.modes) {

            float preOffset = size + Fonts.msSemiBold[11].getWidth(mode) + 3;
            if (preOffset > width - 20) {
                break;
            }
            size += Fonts.msSemiBold[11].getWidth(mode) + 3;
        }
        }
            if (object.module.expanded) {
        for (String mode : set.modes) {
            float preOff = offset + Fonts.msSemiBold[11].getWidth(mode) + 3;
            if (preOff > size) {
                offset = -4;
                offsetY += 11;
            }
            if (RenderUtil.isInRegion(mouseX, mouseY, x + 15 + offset, y + 11f + offsetY, Fonts.durman[11].getWidth(mode) + 0.5f, Fonts.durman[11].getFontHeight() / 2f + 3.5f)) {
                set.setIndex(i);
            }

            offset += Fonts.msSemiBold[11].getWidth(mode) + 3;
            i++;
        }
        }
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void charTyped(char codePoint, int modifiers) {

    }
}
