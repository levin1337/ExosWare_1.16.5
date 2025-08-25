package ru.levinov.ui.beta.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import ru.levinov.managment.Managment;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.misc.AudioUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.SmartScissor;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;

import static ru.levinov.util.render.RenderUtil.reAlphaInt;

public class BooleanComponent extends Component {

    public BooleanOption option;

    public BooleanComponent(BooleanOption option) {
        this.option = option;
        this.s = option;
    }

    public float animationToggle;

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
        height = 15;
        float off = 0.5f;
        // Интерполяция значения animationToggle для плавной анимации
        animationToggle = AnimationMath.lerp(animationToggle, option.get() ? 1 : 0, 10);
        // Интерполяция цвета фона в зависимости от состояния опции
        int color = ColorUtil.interpolateColor(RenderUtil.IntColor.rgba(26, 29, 33, 255),
                RenderUtil.IntColor.rgba(74, 166, 218, 255), animationToggle);

        // Рисуем фон
        RenderUtil.Render2D.drawRoundedRect(x + 5, y + 1 + off, 20, 10, 4f, color);

        // Позиция круга, которая будет анимироваться
        float circleX = x + 10 + (10 * animationToggle); // 10 - это смещение для перемещения круга вправо
        // Рисуем круг
        RenderUtil.Render2D.drawCircle(circleX, y + 5.8f + off, 0, 360, 2, 6, true, Color.WHITE.getRGB());
        SmartScissor.push();

        // Устанавливаем границы для рисования, чтобы ограничить область
        SmartScissor.setFromComponentCoordinates(x + 5, y + 1 + off, 30 * animationToggle, 10);
        SmartScissor.unset();
        SmartScissor.pop();

        // Рисуем текст рядом с опцией
        Fonts.gilroy[14].drawString(matrixStack, option.getName(), x + 35f, y + 4.5f + off, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isInRegion(mouseX, mouseY, x, y, width - 10, 15)) {
            if (Managment.FUNCTION_MANAGER.clientSounds.state && Managment.FUNCTION_MANAGER.clientSounds.soundcheckbox.get()) {
                AudioUtil.playSound("checkbox.wav", 0.2f);
            }
            option.toggle();
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
