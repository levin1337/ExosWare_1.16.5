package ru.levinov.ui.dropdown.objects;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.TextFormatting;
import org.joml.Vector4i;
import org.lwjgl.glfw.GLFW;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.*;
import ru.levinov.ui.dropdown.binds.BindWindow;
import ru.levinov.ui.dropdown.objects.sets.*;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.GaussianBlur;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static ru.levinov.ui.dropdown.Window.light;

public class ModuleObject extends Object {

    public Function function;
    public ArrayList< Object> object = new ArrayList<>();
    public float animation,animation_height;
    public BindWindow bindWindow;

    boolean binding;

    public ModuleObject(Function function) {
        this.function = function;
        for (Setting setting : function.settingList) {
            if (setting instanceof BooleanOption option) {
                object.add(new BooleanObject(this, option));
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
//        Managment.CLICK_GUI.searching = false;
        for ( Object object1 : object) {
            object1.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (isHovered(mouseX,mouseY, 23)) {
            if (mouseButton == 0)
                function.toggle();
            if (mouseButton == 2)
                isBinding = !isBinding;
//                bindWindow.openAnimation = !bindWindow.openAnimation;
//            if (mouseButton == 1){
//                expand = !expand;
//                if(!expand){
//                    lastHeight = height;
//                    height=22;
//                }
//                else{
//                    height = lastHeight;
//                }
//            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for ( Object object1 : object) {
            object1.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        if(isBinding){
            if(keyCode == GLFW.GLFW_KEY_ESCAPE){
                function.bind = 0;
            }else{
                function.bind = keyCode;
                Managment.NOTIFICATION_MANAGER.add("Модуль " + TextFormatting.GRAY + function.name + TextFormatting.WHITE + " был забинжен на кнопку " + ClientUtil.getKey(keyCode).toUpperCase(), "Module", 5);
            }
            isBinding = false;

        }
        for( Object obj:  object){
            if(obj instanceof BindObject m){
                if(m.isBinding){
                    if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                        m.set.setKey(0);
                        m.isBinding = false;
                        continue;
                    }
                    m.set.setKey(keyCode);
                    Managment.NOTIFICATION_MANAGER.add("Функция " + TextFormatting.GRAY + m.object.function.name + TextFormatting.WHITE + " была забинжена на кнопку " + ClientUtil.getKey(keyCode).toUpperCase(), "Module", 5);
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
        int c1 =  RenderUtil.reAlphaInt(Managment.STYLE_MANAGER.getCurrentStyle().getColor(0),255);
        int c2 =  RenderUtil.reAlphaInt(Managment.STYLE_MANAGER.getCurrentStyle().getColor(30),255);
        int c3 =  RenderUtil.reAlphaInt(Managment.STYLE_MANAGER.getCurrentStyle().getColor(90),255);
        int c4 =  RenderUtil.reAlphaInt(Managment.STYLE_MANAGER.getCurrentStyle().getColor(180),255);
//        height = 20;
        hover_anim = AnimationMath.fast(hover_anim, RenderUtil.isInRegion(mouseX,mouseY,x, y, width, height) ? 1 : 0, 10);
        RenderUtil.Render2D.drawRoundOutline(x, y, width, height, 1, 0f, ColorUtil.rgba(25, 26, 33, 100), new Vector4i(
                ColorUtil.gradient(10,0,ColorUtil.rgba(155, 155, 155, 255*hover_anim),ColorUtil.rgba(26, 26, 26, 0*hover_anim)),
                ColorUtil.gradient(10,90,ColorUtil.rgba(155, 155, 155, 255*hover_anim),ColorUtil.rgba(25, 26, 33, 0*hover_anim)),
                ColorUtil.gradient(10,180,ColorUtil.rgba(155, 155, 155, 255*hover_anim),ColorUtil.rgba(25, 26, 33, 0*hover_anim)),
                ColorUtil.gradient(10,270,ColorUtil.rgba(155, 155, 155, 255*hover_anim),ColorUtil.rgba(25, 26, 33, 0*hover_anim))
        ));

        animation = AnimationMath.fast(animation, function.state ? 1 : 0, 5);
        animation_height = AnimationMath.fast(animation_height, height, 5);
        GaussianBlur.startBlur();
        RenderUtil.Render2D.drawRoundOutline(x, y, width, height, 3, 0f, ColorUtil.rgba(25, 26, 33, 255), new Vector4i(
                ColorUtil.rgba(25, 26, 33, 0),
                ColorUtil.rgba(25, 26, 33, 0),
                ColorUtil.rgba(25, 26, 33, 0),
                ColorUtil.rgba(25, 26, 33, 0)
        ));
        GaussianBlur.endBlur(10, 3);

        if(function.state){
            if(function.settingList.isEmpty()){
//                RenderUtil.SmartScissor.push();
//                RenderUtil.SmartScissor.setFromComponentCoordinates((int)x-5, (int)y-5, (int)(width+10),10);
//                RenderUtil.Render2D.drawGradientRound(x+1, y+1, width-2,10, 3f,
//                        RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0), (int) (200*animation)),
//                        RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0), (int) (200*animation)),
//                        RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0), (int) (200*animation)),
//                        RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0), (int) (200*animation)));
//                RenderUtil.SmartScissor.pop();
            }
            else{
//                RenderUtil.SmartScissor.push();
//                RenderUtil.SmartScissor.setFromComponentCoordinates((int)x-5, (int)y-5, (int)(width+10),10);
////                RenderUtil.Render2D.drawRect(0,0,1000,1000,-1);
//                RenderUtil.Render2D.drawGradientRound(x+1, y+1, width-2,10, 3f,
//                        RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0), (int) (200*animation)),
//                        RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0), (int) (200*animation)),
//                        RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0), (int) (200*animation)),
//                        RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0), (int) (200*animation)));
//                RenderUtil.SmartScissor.pop();
            }
        }

        String text = function.name;


      //  if(!Window.searchText.isEmpty())text+=" -> "+function.category;
        if(binding)text += "...";
        if(function.bind != 0)text += KeyEvent.getKeyText(function.bind);

        String finalText = text;
        BloomHelper.registerRenderCall(() -> {
//            Fonts.msSemiBold[15].drawString(stack, finalText, x + 10, y + 10, RenderUtil.reAlphaInt(-1, (int) (255 * animation)));
            if(function.state){
                Fonts.msSemiBold[15].drawString(stack, ClientUtil.gradient(finalText,Managment.STYLE_MANAGER.getCurrentStyle().getColor(0),Managment.STYLE_MANAGER.getCurrentStyle().getColor(90)), x + 10, y + 10, RenderUtil.reAlphaInt(-1, (int) (255 * animation)));
            }else{
                Fonts.msSemiBold[15].drawString(stack, finalText, x + 10, y + 10, RenderUtil.reAlphaInt(-1, (int) (255 * animation)));
            }
        });
//        Fonts.msSemiBold[15].drawString(stack, text, x + 10, y + 10, ColorUtil.interpolateColor(light,-1, animation));
        if(function.state){
            Fonts.msSemiBold[15].drawString(stack, ClientUtil.gradient(text,Managment.STYLE_MANAGER.getCurrentStyle().getColor(0),Managment.STYLE_MANAGER.getCurrentStyle().getColor(90)), x + 10, y + 10, ColorUtil.interpolateColor(light,-1, animation));
        }else{
            Fonts.msSemiBold[15].drawString(stack, text, x + 10, y + 10, ColorUtil.interpolateColor(light,-1, animation));
        }
        if (!function.settingList.isEmpty()) {
            //Fonts.icons1[15].drawString(stack, "E", x + width - 17, y + 25 / 2f - 1, light);
            RenderUtil.Render2D.drawRect(x + 10, y + 22, width - 20, 0.5f, ColorUtil.rgba(32, 35, 57,255));
        }


        drawObjects(stack, mouseX, mouseY);
    }

    public void drawObjects(MatrixStack stack, int mouseX, int mouseY) {
        float offset = 3;
        for ( Object object : object) {
            if (object.setting.visible()) {
                object.x = x;
                object.y = y + 22 + offset;
                object.width = 160;
                object.height = 16;
                object.draw(stack, mouseX, mouseY);
                offset += object.height;
            }
        }
    }

}
