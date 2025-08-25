/* Decompiler 14ms, total 623ms, lines 67 */
package ru.levinov.ui.clickgui.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.ui.clickgui.objects.ModuleObject;
import ru.levinov.ui.clickgui.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.math.MathUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil.Render2D;
import ru.levinov.util.render.animation.AnimationMath;

public class SliderObject extends Object {
    public ModuleObject object;
    public SliderSetting set;
    public boolean sliding;
    public float animatedVal;

    public SliderObject(ModuleObject object, SliderSetting set) {
        this.object = object;
        this.set = set;
        this.setting = set;
    }

    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        --this.y;
        float sliderWidth;
        if (this.sliding) {
            sliderWidth = ((float)mouseX - this.x - 10.0F) / (this.width - 20.0F) * (this.set.getMax() - this.set.getMin()) + this.set.getMin();
            sliderWidth = (float)MathUtil.round((double)sliderWidth, (double)this.set.getIncrement());
            this.set.setValue(sliderWidth);
        }

        sliderWidth = (this.set.getValue().floatValue() - this.set.getMin()) / (this.set.getMax() - this.set.getMin()) * (this.width - 20.0F);
        this.animatedVal = AnimationMath.fast(this.animatedVal, sliderWidth, 20.0F);
        Render2D.drawRoundedRect(this.x + 10.0F, this.y + this.height / 2.0F + 2.0F, this.width - 20.0F, 3.0F, 1.0F, ColorUtil.rgba(105, 105, 105, 255));
        Render2D.drawRoundedRect(this.x + 10.0F, this.y + this.height / 2.0F + 2.0F, this.animatedVal, 3.0F, 1.0F, ColorUtil.rgba(255, 255, 255, 255));
        Render2D.drawRoundCircle(this.x + 10.0F + this.animatedVal, this.y + this.height / 2.0F + 3.5F, 8.0F, ColorUtil.rgba(255, 255, 255, 255));
        Render2D.drawRoundCircle(this.x + 10.0F + this.animatedVal, this.y + this.height / 2.0F + 3.5F, 4.0F, ColorUtil.rgba(0, 0, 0, 255));
        Fonts.msLight[12].drawString(stack, this.set.getName(), (double)(this.x + 10.0F), (double)(this.y + this.height / 2.0F - 4.0F), ColorUtil.rgba(161, 164, 177, 255));
        Fonts.msLight[12].drawString(stack, String.valueOf(this.set.getValue().floatValue()), (double)(this.x + this.width - 10.0F - Fonts.msLight[12].getWidth(String.valueOf(this.set.getValue().floatValue()))), (double)(this.y + this.height / 2.0F - 4.0F), ColorUtil.rgba(161, 164, 177, 255));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isHovered(mouseX, mouseY)) {
            this.sliding = true;
        }

    }

    public void exit() {
        super.exit();
        this.sliding = false;
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.sliding = false;
    }

    public void keyTyped(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char codePoint, int modifiers) {
    }
}