package ru.levinov.ui.dropdownGUI.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import ru.levinov.managment.Managment;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.ui.dropdownGUI.objects.ModuleObject;
import ru.levinov.ui.dropdownGUI.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.font.styled.StyledFont;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.util.Random;

public class MultiObject extends ru.levinov.ui.dropdownGUI.objects.Object {

    public MultiBoxSetting set;
    public ModuleObject object;

    public MultiObject(ModuleObject object, MultiBoxSetting set) {
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

        // Count enabled options and total options
        int totalOptions = set.options.size();  // Use .size() for List
        int enabledOptions = 0;
        for (BooleanOption option : set.options) {
            if (option.get()) {  // Check if the option is enabled
                enabledOptions++;
            }
        }

        // Display "enabled/total" format, e.g. "3/5"
        String enabledStatus = enabledOptions + "/" + totalOptions;

        String enabledStatu = enabledStatus;

        StyledFont newcode = Fonts.msMedium[13];
        float titleWidth = newcode.getWidth(enabledStatus);
        newcode.drawString(stack, enabledStatu, x + 110 - titleWidth, y + height / 2f - 4f, ColorUtil.rgba(240, 240, 240, 255));
        Fonts.msMedium[13].drawString(stack, set.getName(), x + 12.2f, y + height / 2f - 4f, ColorUtil.rgba(240, 240, 240, 255));

        // Calculate width of options to manage the lines
        for (BooleanOption mode : set.options) {
            float preOffset = size + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
            if (preOffset > width - 20) {
                break;
            }
            size += Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
        }

        // Calculate lines required
        for (BooleanOption mode : set.options) {
            float preOffset = offset + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
            if (preOffset > size) {
                lines++;
                offset = 0;
            }
            offset += Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
        }

        // Draw background for options
        height += 5;
        RenderUtil.Render2D.drawRoundedRect(x + 10.9f, y + 9, 105, 10 * lines, 3f, ColorUtil.rgba(0, 0, 0, 180));
        height += 10 * (lines - 1);

        offset = 0;
        offsetY = 0;
        int i = 0;

        // Draw each option in the multi-box
        for (BooleanOption mode : set.options) {
            float preOff = offset + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
            if (preOff > size) {
                offset = 0;
                offsetY += 10;
            }
            if (set.get(i)) {  // If the option is enabled
                int finalOffset = offset;
                float finalOffsetY = offsetY;

                // Draw the enabled option
                Fonts.msMedium[11].drawString(stack, mode.getName(), x + 13.5f + finalOffset, y + 14f + finalOffsetY, ColorUtil.rgba(119, 121, 134, 255));
                Fonts.msMedium[11].drawString(stack, mode.getName(), x + 13.5f + offset, y + 14f + offsetY, -1);
            } else {
                // Draw the disabled option
                Fonts.msMedium[11].drawString(stack, mode.getName(), x + 13.5f + offset, y + 14f + offsetY, ColorUtil.rgba(163, 163, 163, 255));
            }
            offset += Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
            i++;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float offset = 0;
        float offsetY = 0;
        int i = 0;
        float size = 0;

        if (object.module.expanded) {
            // Calculate total width of the options
            for (BooleanOption mode : set.options) {
                float preOffset = size + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
                if (preOffset > width - 20) {
                    break;
                }
                size += Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
            }

            // Handle clicks for each option
            for (BooleanOption mode : set.options) {
                float preOff = offset + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
                if (preOff > size) {
                    offset = 0;
                    offsetY += 10;
                }
                if (RenderUtil.isInRegion(mouseX, mouseY, x + 15 + offset, y + 12f + offsetY, Fonts.msSemiBold[11].getWidth(mode.getName()), Fonts.msSemiBold[11].getFontHeight() / 2f + 3)) {
                    set.set(i, !set.get(i));
                }

                offset += Fonts.msSemiBold[11].getWidth(mode.getName()) + 3;
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
