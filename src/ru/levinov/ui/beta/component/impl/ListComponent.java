package ru.levinov.ui.beta.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.SmartScissor;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;

public class ListComponent extends Component {

    public MultiBoxSetting option;
    public boolean opened;

    public ListComponent(MultiBoxSetting option) {
        this.option = option;
        this.s = option;
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {
        float off = 4;
        float offset = 17 - 8;
        for (BooleanOption s : option.options) {
            offset += 9;
        }
        if (!opened) offset = 0;

        Fonts.gilroy[14].drawString(matrixStack, option.getName(), x + 5, y + 3, -1);
        off += Fonts.gilroy[14].getFontHeight() / 2f + 2;
        height += offset + 7;

        // Отрисовка фона компонента
        RenderUtil.Render2D.drawShadow(x + 5, y + off, width - 10, 20 - 6, 10, new Color(26, 29, 33, 50).getRGB());
        RenderUtil.Render2D.drawRoundedCorner(x + 5, y + off, width - 10, 20 - 6, 4, new Color(26, 29, 33).getRGB());
        RenderUtil.Render2D.drawShadow(x + 5, y + off + 17, width - 10, offset, 12, new Color(0, 0, 0, 100).getRGB());
        RenderUtil.Render2D.drawRoundedCorner(x + 5, y + off + 17, width - 10, offset, 4, new Color(17, 18, 21).getRGB());

        // Отрисовка текста
        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(x + 5, y + off, width - 10, 20 - 6);
        Fonts.msSemiBold[14].drawString(matrixStack, option.get(), x + 10, y + 20 - 4, -1);
        SmartScissor.unset();
        SmartScissor.pop();

        // Отрисовка элементов списка
        if (opened) {
            int i = 0; // Начинаем с 0 для индекса
            for (BooleanOption s : option.options) {
                boolean hovered = RenderUtil.isInRegion(mouseX, mouseY, x + 5, y + off + 20 + i * 9, width - 10, 8);
                s.anim = AnimationMath.lerp(s.anim, hovered ? 2 : 0, 10);
                Fonts.msSemiBold[14].drawString(matrixStack, s.getName(), x + 9 + s.anim, y + off + 23.5F + i * 9, option.get(s.getName()) ? new Color(74, 166, 218).getRGB() : new Color(163, 176, 188).getRGB());
                i++; // Увеличиваем индекс для следующего элемента
            }
            height += 3; // Увеличиваем высоту компонента
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float off = 3;
        off += Fonts.msSemiBold[14].getFontHeight() / 2f + 2;

        // Проверка на клик по заголовку
        if (RenderUtil.isInRegion(mouseX, mouseY, x + 5, y + off, width - 10, 20 - 5)) {
            opened = !opened; // Меняем состояние открытия
        }

        // Если меню не открыто, выходим
        if (!opened) return;

        // Проверка на клик по элементам списка
        int i = 0; // Начинаем с 0 для индекса
        for (BooleanOption s : option.options) {
            if (RenderUtil.isInRegion(mouseX, mouseY, x + 5, y + off + 20 + i * 9, width - 10, 8)) {
                option.set(s.getName(), !option.get(s.getName())); // Переключаем состояние опции
                break; // Прерываем цикл после выбора
            }
            i++; // Увеличиваем индекс для следующего элемента
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        // Здесь можно добавить логику, если потребуется
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        // Здесь можно добавить логику, если потребуется
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        // Здесь можно добавить логику, если потребуется
    }
}
