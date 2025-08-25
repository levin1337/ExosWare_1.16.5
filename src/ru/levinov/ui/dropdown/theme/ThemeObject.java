package ru.levinov.ui.dropdown.theme;

import com.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector4i;
import ru.levinov.managment.Managment;
import ru.levinov.ui.midnight.Style;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

public class ThemeObject {

    public float x, y, width, height;

    public Style style;
    public float anim;



    public ThemeObject(Style style) {
        this.style = style;
    }


    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        anim = AnimationMath.lerp(anim,  Managment.STYLE_MANAGER.getCurrentStyle() == style ? 1 : RenderUtil.isInRegion(mouseX,mouseY, x,y,width,height) ? 0.7f : 0, 5);
        Vector4i colors = new Vector4i(
                style.colors[0],
                style.colors[0],
                style.colors[1],
                style.colors[1]
        );
        
        String ss = style.name;
            Fonts.msSemiBold[16].drawString(stack, ClientUtil.gradient( ss, style.colors[0], style.colors[1]), x + 7, y + 7 , -1);



        Vector4i finalColors = colors;

        RenderUtil.Render2D.drawRoundOutline(x, y, width, height, 5f, 0f, ColorUtil.rgba(25, 26, 33, 0), new Vector4i(
                finalColors.x,
                finalColors.y,
                finalColors.z,
                finalColors.w
        ));
     }
}
