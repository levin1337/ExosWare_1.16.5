package ru.levinov.ui.dropdown.objects.sets;

import com.mojang.blaze3d.matrix.MatrixStack;
import ru.levinov.modules.settings.imp.ButtonSetting;
import ru.levinov.ui.dropdown.objects.ModuleObject;
import ru.levinov.ui.dropdown.objects.Object;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.awt.*;

public class ButtonObject extends Object {

    public ButtonSetting set;
    public ModuleObject object;




    public ButtonObject(ModuleObject object, ButtonSetting set) {
        this.object = object;
        this.set = set;
        setting = set;

    }
    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        Fonts.msSemiBold[13].drawString(stack, set.getName(), x + 10, y + height / 2f - Fonts.msSemiBold[13].getFontHeight() / 2f + 2, Color.WHITE.getRGB());

        float wwidth = Math.max(10, Fonts.msSemiBold[13].getWidth("Открыть") + 4);
        RenderUtil.Render2D.drawRoundedCorner(x + width - wwidth - 10,y + 2, wwidth,10, 2.5f,ColorUtil.rgba(20, 21, 24, 255));

        Fonts.msSemiBold[13].drawCenteredString(stack, "Открыть", x + width - wwidth - 10  + wwidth / 2f, y + height / 2f - Fonts.msSemiBold[13].getFontHeight() / 2f + 2, Color.WHITE.getRGB());

    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX,mouseY)) {
            float wwidth = Math.max(10, Fonts.msSemiBold[13].getWidth("Открыть") + 4);
            if (RenderUtil.isInRegion(mouseX,mouseY,x + width - wwidth - 10,y + 2, wwidth,10)) {
                set.getRun().run();
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
