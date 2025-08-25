package ru.levinov.ui.dropdown.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.ui.dropdown.objects.ModuleObject;
import ru.levinov.ui.dropdown.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;

import static ru.levinov.util.render.ColorUtil.getColorStyle;

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
        y-=1;
        double max = !set.get() ? 0 : 6.5f;
        this.enabledAnimation = AnimationMath.fast(enabledAnimation, (float) max, 10);

        Fonts.durman[14].drawString(stack, set.getName(), x + 10, y + height / 2f - Fonts.msSemiBold[13].getFontHeight() / 2f + 2, Color.WHITE.getRGB());
        RenderUtil.Render2D.drawRoundedCorner(x + width - 18,y - 1,12, 12, 3,ColorUtil.rgba(20, 21, 24, 255));


        if (set.get()) {
            Fonts.icons[12].drawString(stack, "A", x + width - 25 + 10, y + 5, ColorUtil.getColorStyle(90));
        } else {
            Fonts.durman[14].drawString(stack, "X", x + width - 25 + 10.4f, y + 4, Color.red.getRGB());
        }

       // RenderUtil.Render2D.drawRoundedCorner(x + width - 23 + 3 + enabledAnimation - 3 + 1,y + 6,5,7,1, color);



    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (isHovered(mouseX, mouseY)) {
                set.toggle();
            }
        }
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
