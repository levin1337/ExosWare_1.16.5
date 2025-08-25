/* Decompiler 47ms, total 540ms, lines 121 */
package ru.levinov.ui.clickgui.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Iterator;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.ui.clickgui.objects.ModuleObject;
import ru.levinov.ui.clickgui.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.RenderUtil.Render2D;

public class MultiObject extends Object {
    public MultiBoxSetting set;
    public ModuleObject object;

    public MultiObject(ModuleObject object, MultiBoxSetting set) {
        this.object = object;
        this.set = set;
        this.setting = set;
    }

    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        int offset = 0;
        float offsetY = 0.0F;
        int lines = 1;
        float size = 0.0F;

        Iterator var8;
        BooleanOption mode;
        float preOffset;
        for(var8 = this.set.options.iterator(); var8.hasNext(); size += Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F) {
            mode = (BooleanOption)var8.next();
            preOffset = size + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F;
            if (preOffset > this.width - 20.0F) {
                break;
            }
        }

        for(var8 = this.set.options.iterator(); var8.hasNext(); offset = (int)((float)offset + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F)) {
            mode = (BooleanOption)var8.next();
            preOffset = (float)offset + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F;
            if (preOffset > size) {
                ++lines;
                offset = 0;
            }
        }

        this.height += 8.0F;
        Fonts.durman[12].drawString(stack, this.set.getName(), (double)(this.x + 10.0F), (double)(this.y + this.height / 2.0F - 8.0F), ColorUtil.rgba(255, 255, 255, 255));
        Render2D.drawRoundedRect(this.x + 10.0F, this.y + 9.0F, size + 7.0F, (float)(11 * lines), 3.0F, ColorUtil.rgba(0, 0, 0, 255));
        this.height += (float)(11 * (lines - 1));
        offset = 0;
        offsetY = 0.0F;
        int i = 0;

        for(Iterator var13 = this.set.options.iterator(); var13.hasNext(); ++i) {
            mode = (BooleanOption) var13.next();
            float preOff = (float)offset + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F;
            if (preOff > size) {
                offset = 0;
                offsetY += 11.0F;
            }

            if (this.set.get(i)) {
                Fonts.msSemiBold[11].drawString(stack, mode.getName(), (double)(this.x + 15.0F + (float)offset), (double)(this.y + 14.0F + offsetY), ColorUtil.rgba(119, 121, 134, 255));
            } else {
                Fonts.msSemiBold[11].drawString(stack, mode.getName(), (double)(this.x + 15.0F + (float)offset), (double)(this.y + 14.0F + offsetY), ColorUtil.rgba(26, 30, 41, 255));
            }

            offset = (int)((float)offset + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F);
        }

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float offset = 0.0F;
        float offsetY = 0.0F;
        int i = 0;
        float size = 0.0F;

        Iterator var8;
        BooleanOption mode;
        float preOff;
        for(var8 = this.set.options.iterator(); var8.hasNext(); size += Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F) {
            mode = (BooleanOption)var8.next();
            preOff = size + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F;
            if (preOff > this.width - 20.0F) {
                break;
            }
        }

        for(var8 = this.set.options.iterator(); var8.hasNext(); ++i) {
            mode = (BooleanOption)var8.next();
            preOff = offset + Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F;
            if (preOff > size) {
                offset = 0.0F;
                offsetY += 11.0F;
            }

            if (RenderUtil.isInRegion((double)mouseX, (double)mouseY, this.x + 15.0F + offset, this.y + 12.0F + offsetY, Fonts.msSemiBold[11].getWidth(mode.getName()), Fonts.msSemiBold[11].getFontHeight() / 2.0F + 3.0F)) {
                this.set.set(i, !this.set.get(i));
            }

            offset += Fonts.msSemiBold[11].getWidth(mode.getName()) + 3.0F;
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public void keyTyped(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char codePoint, int modifiers) {
    }
}