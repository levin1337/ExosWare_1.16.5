package ru.levinov.ui.dropdown.theme;

import com.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector4i;
import ru.levinov.managment.Managment;
import ru.levinov.ui.beta.component.ColorWindow;
import ru.levinov.ui.dropdown.objects.Object;
import ru.levinov.ui.midnight.Style;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.ScaleMath;
import ru.levinov.util.render.Vec2i;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ru.levinov.ui.dropdown.Window.scrollingOut;

public class ThemeDrawing {

    public List<ThemeObject> objects = new ArrayList<>();

    float animation;

    public ThemeDrawing() {
        Style custom = Managment.STYLE_MANAGER.styles.get(Managment.STYLE_MANAGER.styles.size() - 1);
        for (Style style : Managment.STYLE_MANAGER.styles) {
            if (style.name.equalsIgnoreCase("Свой цвет")) continue;
            objects.add(new ThemeObject(style));
        }
        float[] rgb = RenderUtil.IntColor.rgb(custom.colors[edit]);
        float[] hsb = Color.RGBtoHSB((int) (rgb[0] * 255), (int) (rgb[1] * 255), (int) (rgb[2] * 255), null);
        this.hsb = hsb[0];
        this.satur = hsb[1];
        this.brithe = hsb[2];
        themeDrawing = this;  // Initialize it here if needed

    }

    boolean colorOpen;
    public float openAnimation;

    public int edit;

    float x, y, width, height;

    float hsb;
    float satur;
    float brithe;
    private final ThemeDrawing themeDrawing;

    public void draw(MatrixStack stack, int mouseX, int mouseY, float x,float y,float width ,float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;


            openAnimation = AnimationMath.lerp(openAnimation, colorOpen ? 1 : 0, 15);

            float rowLimit = 6; // Количество тем в ряду
            float offset = 1;
            float off = 10;

            for (int i = 0; i < themeDrawing.objects.size(); i++) {

                ThemeObject object = themeDrawing.objects.get(i);
                object.width = 100;
                object.height = 20;

                // Вычисляем координаты для текущего элемента
                object.x = x + 650 + (i % rowLimit) * (object.width + offset);
                object.y = y + off + offset * (i / rowLimit) + scrollingOut - 100;

                object.draw(stack, mouseX, mouseY);

                RenderUtil.Render2D.drawRoundOutline(object.x, object.y, object.width, object.height, 5f, 0f,
                        ColorUtil.rgba(25, 26, 33, 255), new Vector4i(
                                ColorUtil.rgba(25, 26, 33, 0), ColorUtil.rgba(25, 26, 33, 0),
                                ColorUtil.rgba(25, 26, 33, 0), ColorUtil.rgba(25, 26, 33, 0)
                        ));

                if (i % rowLimit == rowLimit - 1) {
                    // Переход к следующему ряду
                    off += offset + 20;
                }
            }

            for (ThemeObject object : objects) {
                object.draw(stack, mouseX, mouseY);
            }
            Style custom = Managment.STYLE_MANAGER.styles.get(Managment.STYLE_MANAGER.styles.size() - 1);

            animation = (float) AnimationMath.lerp(animation, Managment.STYLE_MANAGER.getCurrentStyle() == custom ? 1 : RenderUtil.isInRegion(mouseX, mouseY, x + 10, y + height - 65, width - 20, 50) ? 0.5f : 0, 5);



    }

    boolean drag;

    public void click(int mouseX, int mouseY, int button) {
        Style custom = Managment.STYLE_MANAGER.styles.get(Managment.STYLE_MANAGER.styles.size() - 1);

        if (RenderUtil.isInRegion(mouseX, mouseY, x + 10, y + height - 65, width - 20, 50) && button == 0) {
            Style c = Managment.STYLE_MANAGER.styles.get(Managment.STYLE_MANAGER.styles.size() - 1);
            Managment.STYLE_MANAGER.setCurrentStyle(c);
        }


        for (ThemeObject object : objects) {
            if (RenderUtil.isInRegion(mouseX, mouseY, object.x, object.y, object.width, object.height)) {
                Managment.STYLE_MANAGER.setCurrentStyle(object.style);
            }
        }
    }
}