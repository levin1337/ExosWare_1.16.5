package ru.levinov.ui.dropdownGUI.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector4i;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.ui.dropdownGUI.objects.ModuleObject;
import ru.levinov.ui.dropdownGUI.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;
import java.util.Random;

public class BooleanObject extends Object {

    public ModuleObject object;
    public BooleanOption set;
    public float enabledAnimation;

    public BooleanObject(ModuleObject object, BooleanOption set) {
        this.object = object;
        this.set = set;
        setting = set;
    }

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        y-=2;
        double max = !set.get() ? 0 : 8.5f;
        this.enabledAnimation = AnimationMath.fast(enabledAnimation, (float) max, 10);
        Fonts.durman[12].drawString(stack, set.getName(), x + 12, y + height / 2f - Fonts.durman[12].getFontHeight() / 2f + 3, Color.WHITE.getRGB());
        RenderUtil.Render2D.drawRoundedCorner(x + width - 24,y - 1,12, 12, 3,ColorUtil.rgba(20, 21, 24, 255));


        if (set.get()) {
            Fonts.icons[12].drawString(stack, "A", x + width - 25 + 4, y + 5, Color.green.getRGB());
        } else {
            Fonts.durman[14].drawString(stack, "X", x + width - 25 + 4.4f, y + 4, Color.red.getRGB());
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (object.module.expanded) {
            if (mouseButton == 0) {
                if (isHovered(mouseX, mouseY)) {
                    set.toggle();
                    int volume = new Random().nextInt(13) + 59;
                }
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
