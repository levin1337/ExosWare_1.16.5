/* Decompiler 12ms, total 379ms, lines 50 */
package ru.levinov.ui.clickgui.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.ui.clickgui.objects.ModuleObject;
import ru.levinov.ui.clickgui.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil.Render2D;
import ru.levinov.util.render.animation.AnimationMath;

public class BooleanObject extends Object {
    public ModuleObject object;
    public BooleanOption set;
    public float enabledAnimation;

    public BooleanObject(ModuleObject object, BooleanOption set) {
        this.object = object;
        this.set = set;
        this.setting = set;
    }

    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        --this.y;
        double max = !this.set.get() ? 0.0D : 6.5D;
        this.enabledAnimation = AnimationMath.fast(this.enabledAnimation, (float)max, 10.0F);
        Fonts.msLight[13].drawString(stack, this.set.getName(), (double)(this.x + 10.0F), (double)(this.y + this.height / 2.0F - Fonts.msLight[13].getFontHeight() / 2.0F + 2.0F), ColorUtil.rgba(161, 166, 179, 255));
        Render2D.drawRoundedRect(this.x + this.width - 23.5F, this.y + 5.0F, 15.0F, 8.0F, 1.0F, ColorUtil.rgba(20, 21, 24, 255));
        int color = ColorUtil.interpolateColor(ColorUtil.rgba(255, 0, 0, 255), ColorUtil.rgba(0, 0, 255, 255), this.enabledAnimation / 6.5F);
        Render2D.drawRoundedCorner(this.x + this.width - 23.0F + 1.5F + this.enabledAnimation, this.y + 6.5F, 5.0F, 5.0F, 1.0F, color);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovered(mouseX, mouseY)) {
            this.set.toggle();
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public void keyTyped(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char codePoint, int modifiers) {
    }
}