package ru.levinov.ui.dropdownGUI.objects;

import com.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector4i;
import org.lwjgl.glfw.GLFW;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.*;
import ru.levinov.ui.dropdownGUI.objects.sets.*;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static ru.levinov.ui.dropdownGUI.Window.light;

public class ModuleObject extends Object {

    public Function module;
    public ArrayList<Object> object = new ArrayList<>();
    public float animation,animation_height;

    boolean binding;

    public ModuleObject(Function module) {
        this.module = module;
        for (Setting setting : module.settingList) {
            if (setting instanceof BooleanOption option) {
                object.add(new BooleanObject(this, option));
            }
            if (setting instanceof ColorSetting option) {
                object.add(new ColorObject(this, option));
            }
            if (setting instanceof SliderSetting option) {
                object.add(new SliderObject(this, option));
            }
            if (setting instanceof ModeSetting option) {
                object.add(new ModeObject(this, option));
            }

            if (setting instanceof MultiBoxSetting option) {
                object.add(new MultiObject(this, option));
            }
            if (setting instanceof BindSetting option) {
                object.add(new BindObject(this, option));
            }
            if (setting instanceof ButtonSetting option) {
                object.add(new ButtonObject(this, option));
            }
        }
    }

    float lastHeight;
    public boolean isBinding = false;


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for ( Object object1 : object) {
            object1.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (isHovered(mouseX,mouseY, 15)) {
            if (mouseButton == 0)
                module.toggle();
            if (mouseButton == 2)
                isBinding = !isBinding;
        }
    }

    @Override
    public void drawComponent(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for ( Object object1 : object) {
            object1.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        if (isBinding) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE ||
                    keyCode == GLFW.GLFW_KEY_ESCAPE) {
                module.bind = 0;//нуль
            } else {
                module.bind = keyCode; //если гуд
            }
            isBinding = false; //чел забиндил
        }

        for (Object obj : object) {
            if (obj instanceof BindObject m) {
                if (m.isBinding) {
                    if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE ||
                            keyCode == GLFW.GLFW_KEY_ESCAPE) {
                        m.set.setKey(0);
                        m.isBinding = false;
                        continue;
                    }
                    m.set.setKey(keyCode);
                    m.isBinding = false;
                }
            }
            obj.keyTyped(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for( Object obj:  object){
            obj.charTyped(codePoint, modifiers);
        }
    }

    float hover_anim;

    boolean expand = false;
    public float expand_anim;

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        hover_anim = AnimationMath.fast(hover_anim, RenderUtil.isInRegion(mouseX,mouseY,x, y, width, height) ? 1 : 0, 10);
        RenderUtil.Render2D.drawRoundOutline(x, y, width, height, 1, 0f, ColorUtil.rgba(25, 26, 33, 100), new Vector4i(
                ColorUtil.gradient(10,0, ColorUtil.rgba(155, 155, 155, 255*hover_anim), ColorUtil.rgba(26, 26, 26, 0*hover_anim)),
                ColorUtil.gradient(10,90, ColorUtil.rgba(155, 155, 155, 255*hover_anim), ColorUtil.rgba(25, 26, 33, 0*hover_anim)),
                ColorUtil.gradient(10,180, ColorUtil.rgba(155, 155, 155, 255*hover_anim), ColorUtil.rgba(25, 26, 33, 0*hover_anim)),
                ColorUtil.gradient(10,270, ColorUtil.rgba(155, 155, 155, 255*hover_anim), ColorUtil.rgba(25, 26, 33, 0*hover_anim))
        ));
        animation = AnimationMath.fast(animation, module.state ? 1 : 0, 5);
        animation_height = AnimationMath.fast(animation_height, height, 5);

        if(module.state){
            if(module.settingList.isEmpty()){
            }
            else{
            }
        }

        String text = module.name;
        if(binding)text += "...";
        if(module.bind != 0)text += KeyEvent.getKeyText(module.bind);

        if(module.state){
            Fonts.msSemiBold[15].drawString(stack, ClientUtil.gradient(text, Managment.STYLE_MANAGER.getCurrentStyle().getColor(0), Managment.STYLE_MANAGER.getCurrentStyle().getColor(90)), x + 10, y + 10, ColorUtil.interpolateColor(light,-1, animation));
        }else{
            Fonts.msSemiBold[15].drawString(stack, text, x + 10, y + 10, ColorUtil.interpolateColor(light,-1, animation));
        }
        if (!module.settingList.isEmpty()) {
            RenderUtil.Render2D.drawRect(x + 10, y + 22, width - 20, 0.5f, ColorUtil.rgba(32, 35, 57,255));
        }

        drawObjects(stack, mouseX, mouseY);
    }

    public void drawObjects(MatrixStack stack, int mouseX, int mouseY) {
        float offset = -4;
        for ( Object object : object) {
            if (object.setting.visible()) {
                object.x = x;
                object.y = y + 15 + offset;
                object.width = 160;
                object.height = 8;
                object.draw(stack, mouseX, mouseY);
                offset += object.height;
            }
        }
    }
}
