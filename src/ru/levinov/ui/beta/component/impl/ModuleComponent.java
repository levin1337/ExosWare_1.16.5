package ru.levinov.ui.beta.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.*;
import ru.levinov.ui.beta.ClickGui;
import ru.levinov.ui.clickgui.Window;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.math.KeyMappings;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.SmartScissor;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ru.levinov.util.render.RenderUtil.reAlphaInt;

public class ModuleComponent extends Component {

    public Function function;

    public List<Component> components = new ArrayList<>();

    public ModuleComponent(Function function) {
        this.function = function;
        for (Setting setting : function.getSettingList()) {
            switch (setting.getType()) {
                case BOOLEAN_OPTION -> components.add(new BooleanComponent((BooleanOption) setting));
                case SLIDER_SETTING -> components.add(new SliderComponent((SliderSetting) setting));
                case MODE_SETTING -> components.add(new ModeComponent((ModeSetting) setting));
                case COLOR_SETTING -> components.add(new ColorComponent((ColorSetting) setting));
                case MULTI_BOX_SETTING -> components.add(new ListComponent((MultiBoxSetting) setting));
                case BIND_SETTING -> components.add(new BindComponent((BindSetting) setting));
                case TEXT_SETTING -> components.add(new TextComponent((TextSetting) setting));
            }
        }
    }

    public float animationToggle;
    public static ModuleComponent binding;

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {

        float totalHeight = 10;
        for (Component component : components) {
            if (component.s != null && component.s.visible()) {
                totalHeight += component.height;
            }
        }

        float off = 1f;

        components.forEach(c -> {
            c.function = function;
            c.parent = parent;
        });

        animationToggle = AnimationMath.lerp(animationToggle, function.state ? 1 : 0, 15);
        RenderUtil.Render2D.drawRoundedCorner(x, y + 4, width, height + totalHeight - 5, 10f,ColorUtil.rgba(17, 18, 21,200) , RenderUtil.Render2D.Corner.ALL);

        RenderUtil.Render2D.drawRect(x + 5, y + 28, width - 10, 0.5f, new Color(26, 28, 33).getRGB());

        if (function.state) {
            Fonts.durman[17].drawString(matrixStack, function.name, x + 7.5f, y + 9f, ColorUtil.getColorStyle(90));
        } else {
            Fonts.durman[17].drawString(matrixStack, function.name, x + 7.5f, y + 9f, new Color(255, 255, 255).getRGB());
        }

        Fonts.msSemiBold[11].drawString(matrixStack, function.desc, x + 7.5f, y + 21f, new Color(255, 255, 255).getRGB());



        //desc инфо о функции
        String key = ClientUtil.getKey(function.bind);

        if (binding == this && key != null) {
            RenderUtil.Render2D.drawRoundedCorner(x + width - 20 - Fonts.gilroy[16].getWidth(key) + 5, y + 10, 10 + Fonts.gilroy[16].getWidth(key), 10, 2, -1);
            Fonts.gilroy[16].drawCenteredString(matrixStack, key, x + width - 19.5f - Fonts.gilroy[16].getWidth(key) + 5 + (10 + Fonts.gilroy[16].getWidth(key)) / 2, y + 13, Color.BLACK.getRGB());
        }

        int color = ColorUtil.interpolateColor(RenderUtil.IntColor.rgba(26, 29, 33, 255), RenderUtil.IntColor.rgba(74, 166, 218, 255), animationToggle);
        RenderUtil.Render2D.drawRoundedRect(x + 5, y + 30 + off, 10, 10, 2f, color);
        SmartScissor.push();

        SmartScissor.setFromComponentCoordinates(x + 5, y + 30 + off, 10 * animationToggle, 10);
        Fonts.icons[12].drawString(matrixStack, "A", x + 7, y + 35 + off, -1);
        SmartScissor.unset();
        SmartScissor.pop();
//Включен
        Fonts.gilroy[14].drawString(matrixStack, "Включен", x + 18f, y + 34 + off, -1);



        float offsetY = 8;
        for (Component component : components) {
            if (component.s != null && component.s.visible()) {
                component.setPosition(x, y + height + offsetY, width, 20);
                component.drawComponent(matrixStack, mouseX, mouseY);
                offsetY += component.height;
            }
        }

    }
    MatrixStack matrixstack = new MatrixStack();
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isInRegion(mouseX, mouseY, x, y + 2, width, 40)) {
            if (mouseButton == 0) {
                function.toggle();
            }
        }
        if (binding == this && mouseButton > 2) {
            function.bind = -100 + mouseButton;
            Managment.NOTIFICATION_MANAGER.add("Модуль " + TextFormatting.GRAY + binding.function.name + TextFormatting.WHITE + " был забинжен на кнопку " + ClientUtil.getKey(-100 + mouseButton), "Module", 5);
            binding = null;
        }

        if (RenderUtil.isInRegion(mouseX, mouseY, x + 5, y, width - 10, 20)) {
            if (mouseButton == 2) {
                ClickGui.typing = false;
                binding = this;
            }
        }

        components.forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        components.forEach(component -> component.mouseReleased(mouseX, mouseY, mouseButton));
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        components.forEach(component -> component.keyTyped(keyCode, scanCode, modifiers));
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        components.forEach(component -> component.charTyped(codePoint, modifiers));
    }
}
