package ru.levinov.modules.impl.render;

import net.minecraft.client.MainWindow;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.MathHelper;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.ui.midnight.Style;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import static ru.levinov.util.render.ColorUtil.*;
import static ru.levinov.util.render.RenderUtil.Render2D.*;

/**
 * @author levin1337
 * @since 29.06.2023
 */
@FunctionAnnotation(name = "Crosshair", type = Type.Render)
public class Crosshair extends Function {

    private final ModeSetting mode = new ModeSetting("Мод", "Тень", "Круг", "Плюс");
    public SliderSetting size = new SliderSetting("Размер", 2.5f, 0.5f, 5f, 0.5f);


    public Crosshair() {
        addSettings(mode,size);
    }

    private float circleAnimation = 0.0F;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {
                handleCrosshairRender();
        }
    }


    private void handleCrosshairRender() {
        if (mode.is("Круг")) {
            if (mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
                return;
            }

            final MainWindow mainWindow = mc.getMainWindow();

            final float x = (float) mainWindow.scaledWidth() / 2.0F;
            final float y = (float) mainWindow.scaledHeight() / 2.0F;

            final float calculateCooldown = mc.player.getCooledAttackStrength(1.0F);
            final float endRadius = MathHelper.clamp(calculateCooldown * 360, 0, 360);

            this.circleAnimation = AnimationMath.lerp(this.circleAnimation, endRadius, 5);

            final int mainColor = rgba(30, 30, 30, 255);
            Style style = Managment.STYLE_MANAGER.getCurrentStyle();

            drawCircle(x, y, 0, 360, 4.2f, size.getValue().floatValue(), false, mainColor);
            drawCircle(x, y, circleAnimation, 0, 4.2f, size.getValue().floatValue(), false, style);
        }
        if (mode.is("Плюс")) {
            if (mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
                return;
            }

            MainWindow mainWindow = mc.getMainWindow();
            float x;
            float y;
            x = (float)mainWindow.scaledWidth() / 2.0F;
            y = (float)mainWindow.scaledHeight() / 2.0F;
            int mainColor = ColorUtil.rgba(255, 255, 255, 255);
            RenderUtil.Render2D.drawCircle(x, y, 0.0F, 360.0F, 1.5F, 2.0F, true, mainColor);
            RenderUtil.Render2D.drawRoundedCorner(x + 6.0F, y - 1.0F, 6.0F, 2.0F, 2.0F, -1);
            RenderUtil.Render2D.drawRoundedCorner(x - 12.0F, y - 1.0F, 6.0F, 2.0F, 2.0F, -1);
            RenderUtil.Render2D.drawRoundedCorner(x - 0.6F, y + 5.0F, 2.0F, 6.0F, 2.0F, -1);
            RenderUtil.Render2D.drawRoundedCorner(x - 0.6F, y - 11.0F, 2.0F, 6.0F, 2.0F, -1);
        }
    }
}
