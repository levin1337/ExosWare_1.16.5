package ru.levinov.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventJump;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.impl.player.AutoPotionFunction;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;
import static org.lwjgl.opengl.GL11.*;


@FunctionAnnotation(name = "Jump Circle", type = Type.Render)
public class JumpCircleFunction extends Function {
    public List<Circle> circles = new ArrayList<>();

    public SliderSetting radius = new SliderSetting("Радиус", 1, 0.1f, 2, 0.01f);
    public SliderSetting shadow = new SliderSetting("Тень", 60, 10, 100, 0.01f);
    public SliderSetting speed = new SliderSetting("Скорость", 1, 1, 5, 0.01f);

    public final ModeSetting mode = new ModeSetting("Мод", "Обычный", "Обычный", "Шейдер","Новый");


    public JumpCircleFunction() {
        addSettings(mode,radius, speed);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventJump) {
            addCircle();
        } else if (event instanceof EventRender render && render.isRender3D()) {
            updateCircles();
            //Выбор
            if (mode.is("Обычный")) {
                renderCircles();
            }
            if (mode.is("Шейдер")) {
                renderCircles2();
            }
            if (mode.is("Новый")) {
                renderCircles2();
            }
        }
    }

    private void addCircle() {
        // Добавляем круг для текущего игрока
        circles.add(new Circle((float) mc.player.getPosX(), (float) mc.player.getPosY(), (float) mc.player.getPosZ()));

    }


    private void updateCircles() {
        for (Circle circle : circles) {
            circle.factor = AnimationMath.fast(circle.factor, radius.getValue().floatValue() + 0.1f, speed.getValue().floatValue());
            circle.shadow = AnimationMath.fast(circle.shadow, shadow.getValue().floatValue(), speed.getValue().floatValue());
            circle.alpha = AnimationMath.fast(circle.alpha, 0, speed.getValue().floatValue());
        }
        if (circles.size() >= 1)
            circles.removeIf(circle -> circle.alpha <= 0.005f);
    }

    private void renderCircles() {
        setupRenderSettings();
        for (Circle circle : circles) {
            drawJumpCircle(circle, circle.factor, circle.alpha, 0);
        }
        restoreRenderSettings();
    }
    private void renderCircles2() {
        setupRenderSettings();
        for (Circle circle : circles) {
            drawJumpCircle2(circle, circle.factor, circle.alpha, 0);
        }
        restoreRenderSettings();
    }

    /**
     * Устанавливает настройки отрисовки кругов.
     */
    private void setupRenderSettings() {
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(770, 1, 0, 1);
        GlStateManager.translated(-mc.getRenderManager().info.getProjectedView().getX(), -mc.getRenderManager().info.getProjectedView().getY(), -mc.getRenderManager().info.getProjectedView().getZ());
    }

    /**
     * Восстанавливает настройки отрисовки.
     */
    private void restoreRenderSettings() {
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();

    }

    /**
     * Рисует круг прыжка.
     *
     * @param circle     Круг прыжка.
     * @param radius     Радиус круга.
     * @param alpha      Прозрачность круга.
     * @param shadowSize Размер тени круга.
     */
    private void drawJumpCircle(Circle circle, float radius, float alpha, float shadowSize) {
        double x = circle.spawnX;
        double y = circle.spawnY + 0.1;
        double z = circle.spawnZ;
        GlStateManager.translated(x,y,z);
        GlStateManager.rotatef(circle.factor * 70,0,-1,0);

        mc.getTextureManager().bindTexture(new ResourceLocation("client/images/circle.png"));

        buffer.begin(GL_QUAD_STRIP, POSITION_COLOR_TEX);
        for (int i = 0; i <= 360F; i+=1) {
            float[] colors = RenderUtil.IntColor.rgb(ColorUtil.getColorStyle(i * 2));
            double sin = MathHelper.sin(Math.toRadians(i + 0.1F)) * radius;
            double cos = MathHelper.cos(Math.toRadians(i + 0.1F)) * radius;
            buffer.pos(0, 0, 0).color(colors[0], colors[1], colors[2], MathHelper.clamp(circle.alpha ,0,1)).tex(0.5f, 0.5f).endVertex();
            buffer.pos(sin, 0, cos).color(colors[0], colors[1], colors[2], MathHelper.clamp(circle.alpha ,0,1)).tex((float) ((sin / (2 * radius)) + 0.5f), (float) ((cos / (2 * radius)) + 0.5f)).endVertex();
        }
        tessellator.draw();
        GlStateManager.rotatef(-circle.factor * 70,0,-1,0);
        GlStateManager.translated(-x,-y,-z);
    }
    private void drawJumpCircle2(Circle circle, float radius, float alpha, float shadowSize) {
        double x = circle.spawnX;
        double y = circle.spawnY + 0.1;
        double z = circle.spawnZ;

        // Параметры вращения
        long time = System.currentTimeMillis();
        float rotationSpeed = 0.005f; // Скорость вращения
        float rotationAngle = (float) (Math.sin(time * rotationSpeed) * 180); // Вращение от -180 до 180

        // Параметры масштабирования
        float scaleSpeed = 0.002f; // Скорость масштабирования
        float scaleAmount = 0.2f;   // Максимальное изменение масштаба
        float scale = 1.0f + (float) (Math.sin(time * scaleSpeed) * scaleAmount); // Масштаб от 0.8 до 1.2

        GlStateManager.translated(x, y, z);
        GlStateManager.rotatef(rotationAngle, 0, -1, 0); // Применяем вращение
        GlStateManager.scalef(scale, scale, scale); // Применяем масштабирование

        if (mode.is("Шейдер")) {
            mc.getTextureManager().bindTexture(new ResourceLocation("client/images/circle2.png"));
        }
        if (mode.is("Новый")) {
            mc.getTextureManager().bindTexture(new ResourceLocation("client/images/circle3.png"));
        }

        buffer.begin(GL_QUAD_STRIP, POSITION_COLOR_TEX);
        for (int i = 0; i <= 360F; i += 1) {
            float[] colors = RenderUtil.IntColor.rgb(ColorUtil.getColorStyle(360));
            double sin = MathHelper.sin(Math.toRadians(i + 0.1F)) * radius;
            double cos = MathHelper.cos(Math.toRadians(i + 0.1F)) * radius;

            // Изменение прозрачности в зависимости от времени
            float currentAlpha = MathHelper.clamp(circle.alpha * (0.5f + 0.5f * (float)Math.sin(time * 0.001)), 0, 1);

            buffer.pos(0, 0, 0).color(colors[0], colors[1], colors[2], currentAlpha).tex(0.5f, 0.5f).endVertex();
            buffer.pos(sin, 0, cos).color(colors[0], colors[1], colors[2], currentAlpha).tex((float) ((sin / (2 * radius)) + 0.5f), (float) ((cos / (2 * radius)) + 0.5f)).endVertex();
        }
        tessellator.draw();

        GlStateManager.rotatef(-rotationAngle, 0, -1, 0); // Возвращаем вращение обратно
        GlStateManager.scalef(1.0f / scale, 1.0f / scale, 1.0f / scale); // Возвращаем масштаб обратно
        GlStateManager.translated(-x, -y, -z);
    }





    /**
     * Класс, представляющий круг прыжка.
     */
    class Circle {
        public final float spawnX;
        public final float spawnY;
        public final float spawnZ;
        public float factor = 0;
        public float alpha = 5;
        public float shadow = 40;
        public float ticks = 0;

        public Circle(float spawnX, float spawnY, float spawnZ) {
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.spawnZ = spawnZ;
        }
    }
}
