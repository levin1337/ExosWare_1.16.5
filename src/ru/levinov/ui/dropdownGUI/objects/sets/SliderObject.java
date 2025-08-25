package ru.levinov.ui.dropdownGUI.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector2i;
import ru.levinov.managment.Managment;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.ui.dropdownGUI.objects.ModuleObject;
import ru.levinov.ui.dropdownGUI.objects.Object;
import ru.levinov.ui.midnight.StyleManager;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.math.MathUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;

import static org.joml.Math.lerp;

public class SliderObject extends Object {

    public ModuleObject object;
    public SliderSetting set;
    public boolean sliding;

    public float animatedVal;
    public float animatedThumbX;
    private float slider = 0;

    public SliderObject(ModuleObject object, SliderSetting set) {
        this.object = object;
        this.set = set;
        setting = set;
        animatedThumbX = x + 10 + 3 + ((set.getValue().floatValue() - set.getMin()) / (set.getMax() - set.getMin())) * (width - 20);
    }

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        int firstColor = ColorUtil.getColorStyle(0);
        int secondColor = ColorUtil.getColorStyle(90);
        y -= 1;
        if (sliding) {
            float value = (float) ((mouseX - x - 10) / (width - 20) * (set.getMax() - set.getMin()) + set.getMin());
            value = (float) MathUtil.round(value, set.getIncrement());
            set.setValue(value);
        }
        float sliderWidth = ((set.getValue().floatValue() - set.getMin()) / (set.getMax() - set.getMin())) * (width - 24);
        animatedVal = AnimationMath.fast(animatedVal, sliderWidth, 20);

        RenderUtil.Render2D.drawRoundedCorner(x + 12, y + height / 2f + 3, width - 24, 3, 1, ColorUtil.rgba(35, 10, 45, 128));
        RenderUtil.Render2D.drawRoundedCorner(x + 12, y + height / 2f + 2.5f, animatedVal, 4, 3, ColorUtil.rgba(53,51,89,255));



        RenderUtil.Render2D.drawRoundCircle(x + 12 + animatedVal, y + height / 2f + 4.5f, 5, ColorUtil.rgba(79,81,149,255));

        Fonts.msSemiBold[12].drawString(stack, set.getName(), x + 10, y + height / 2f - 4, Color.WHITE.getRGB());
        Fonts.msSemiBold[12].drawString(stack, String.valueOf(set.getValue().floatValue()), x + width - 10 - Fonts.msSemiBold[12].getWidth(String.valueOf(set.getValue().floatValue())), y + height / 2f - 4, Color.WHITE.getRGB());

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (object.module.expanded) {
            if (isHovered(mouseX, mouseY)) {
                sliding = true;
            }
        }
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public void exit() {
        super.exit();
        sliding = false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        sliding = false;
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void charTyped(char codePoint, int modifiers) {

    }
}
