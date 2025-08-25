/* Decompiler 15ms, total 402ms, lines 54 */
package ru.levinov.ui.clickgui.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import ru.levinov.modules.settings.imp.ButtonSetting;
import ru.levinov.ui.clickgui.objects.ModuleObject;
import ru.levinov.ui.clickgui.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.RenderUtil.Render2D;

public class ButtonObject extends Object {
    public ButtonSetting set;
    public ModuleObject object;
    private String buttonText = "Открыть";

    public ButtonObject(ModuleObject object, ButtonSetting set) {
        this.object = object;
        this.set = set;
        this.setting = set;
    }

    public void setButtonText(String text) {
        this.buttonText = text;
    }

    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        Fonts.msLight[13].drawString(stack, this.set.getName(), (double)(this.x + 10.0F), (double)(this.y + this.height / 2.0F - Fonts.msLight[13].getFontHeight() / 2.0F + 2.0F), ColorUtil.rgba(161, 166, 179, 255));
        float wwidth = Math.max(10.0F, Fonts.msLight[13].getWidth(this.buttonText) + 4.0F);
        Render2D.drawRoundedRect(this.x + this.width - wwidth - 10.0F, this.y + 2.0F, wwidth, 10.0F, 2.0F, ColorUtil.rgba(20, 21, 24, 255));
        Fonts.msLight[13].drawCenteredString(stack, this.buttonText, (double)(this.x + this.width - wwidth - 10.0F + wwidth / 2.0F), (double)(this.y + this.height / 2.0F - Fonts.msLight[13].getFontHeight() / 2.0F + 2.0F), ColorUtil.rgba(255, 255, 255, 255));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isHovered(mouseX, mouseY)) {
            float wwidth = Math.max(10.0F, Fonts.msLight[13].getWidth(this.buttonText) + 4.0F);
            if (RenderUtil.isInRegion((double)mouseX, (double)mouseY, this.x + this.width - wwidth - 10.0F, this.y + 2.0F, wwidth, 10.0F)) {
                this.set.getRun().run();
            }
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public void keyTyped(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char codePoint, int modifiers) {
    }
}