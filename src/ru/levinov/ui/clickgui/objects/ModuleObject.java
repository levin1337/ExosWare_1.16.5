/* Decompiler 41ms, total 401ms, lines 162 */
package ru.levinov.ui.clickgui.objects;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import org.joml.Vector4i;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BindSetting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ButtonSetting;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.ui.beta.component.impl.ModuleComponent;
import ru.levinov.ui.clickgui.Window;
import ru.levinov.ui.clickgui.binds.BindWindow;
import ru.levinov.ui.clickgui.objects.sets.BindObject;
import ru.levinov.ui.clickgui.objects.sets.BooleanObject;
import ru.levinov.ui.clickgui.objects.sets.ButtonObject;
import ru.levinov.ui.clickgui.objects.sets.ModeObject;
import ru.levinov.ui.clickgui.objects.sets.MultiObject;
import ru.levinov.ui.clickgui.objects.sets.SliderObject;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.RenderUtil.Render2D;
import ru.levinov.util.render.animation.AnimationMath;

public class ModuleObject extends Object {
    public Function function;
    public FunctionAnnotation desc;
    public ArrayList<Object> object = new ArrayList();
    public float animation;
    public BindWindow bindWindow;
    final int hud_color = (new Color(0, 0, 0, 90)).getRGB();

    public void exit() {
        super.exit();
        Iterator var1 = this.object.iterator();

        while(var1.hasNext()) {
            Object object1 = (Object)var1.next();
            object1.exit();
        }

    }

    public ModuleObject(Function function) {
        this.function = function;
        Iterator var2 = function.settingList.iterator();

        while(var2.hasNext()) {
            Setting setting = (Setting)var2.next();
            if (setting instanceof BooleanOption) {
                BooleanOption option = (BooleanOption)setting;
                this.object.add(new BooleanObject(this, option));
            }

            if (setting instanceof SliderSetting) {
                SliderSetting option = (SliderSetting)setting;
                this.object.add(new SliderObject(this, option));
            }

            if (setting instanceof ModeSetting) {
                ModeSetting option = (ModeSetting)setting;
                this.object.add(new ModeObject(this, option));
            }

            if (setting instanceof MultiBoxSetting) {
                MultiBoxSetting option = (MultiBoxSetting)setting;
                this.object.add(new MultiObject(this, option));
            }

            if (setting instanceof BindSetting) {
                BindSetting option = (BindSetting)setting;
                this.object.add(new BindObject(this, option));
            }

            if (setting instanceof ButtonSetting) {
                ButtonSetting option = (ButtonSetting)setting;
                this.object.add(new ButtonObject(this, option));
            }
        }

        this.bindWindow = new BindWindow(this);
        this.bindWindow.x = 10.0F + ThreadLocalRandom.current().nextFloat(0.0F, 200.0F);
        this.bindWindow.y = 10.0F + ThreadLocalRandom.current().nextFloat(0.0F, 200.0F);
        this.bindWindow.width = 89.0F;
        this.bindWindow.height = 36.5F;
    }
    public static ModuleComponent binding;
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        Managment.CLICK_GUI.searching = false;
        Iterator var4 = this.object.iterator();

        while(var4.hasNext()) {
            Object object1 = (Object)var4.next();
            object1.mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (this.isHovered(mouseX, mouseY, 23.0F)) {
            if (mouseButton == 0) {
                this.function.toggle();
            }

            if (mouseButton == 2) {
                this.bindWindow.openAnimation = !this.bindWindow.openAnimation;
            }
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        Iterator var4 = this.object.iterator();

        while(var4.hasNext()) {
            Object object1 = (Object)var4.next();
            object1.mouseReleased(mouseX, mouseY, mouseButton);
        }

    }

    public void keyTyped(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char codePoint, int modifiers) {
    }

    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        Render2D.drawRoundedCorner(this.x, this.y, this.width, this.height, 6.0F, ColorUtil.rgba(47, 79, 79, 255));
        this.animation = AnimationMath.fast(this.animation, this.function.state ? 1.0F : 0.0F, 1000.0F);
        Render2D.drawRoundOutline(this.x, this.y, this.width, this.height, 5.0F, 0.0F, ColorUtil.rgba(25, 26, 33, 255), new Vector4i(ColorUtil.rgba(25, 26, 33, 0), ColorUtil.rgba(25, 26, 33, 0), ColorUtil.rgba(25, 26, 33, 0), ColorUtil.rgba(25, 26, 33, 0)));
        Fonts.msSemiBold[15].drawString(stack, this.function.name, (double)(this.x + 10.0F), (double)(this.y + 10.0F), ColorUtil.interpolateColor(Window.light, ColorUtil.getColorStyle(180.0F), this.animation));
        if (!this.function.settingList.isEmpty()) {
        }


        this.drawObjects(stack, mouseX, mouseY);
    }

    public void drawObjects(MatrixStack stack, int mouseX, int mouseY) {
        float offset = 3.0F;
        Iterator var5 = this.object.iterator();

        while(var5.hasNext()) {
            Object object = (Object)var5.next();
            if (object.setting.visible()) {
                object.x = this.x;
                object.y = this.y + 22.0F + offset;
                object.width = 160.0F;
                object.height = 18.0F;
                object.draw(stack, mouseX, mouseY);
                offset += object.height;
            }
        }

    }
}