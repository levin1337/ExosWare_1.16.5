package ru.levinov.util.misc;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.font.styled.StyledFont;
import ru.levinov.util.math.TPSUtils;
import ru.levinov.util.render.animation.AnimationMath;

import java.util.List;

import static ru.levinov.util.render.RenderUtil.Render2D.drawTexture;
import static ru.levinov.util.render.RenderUtil.Render2D.getHurtPercent;

public class HudUtil implements IMinecraft {

    public static String calculateBPS() {
        return String.format("%.2f", Math.hypot(mc.player.getPosX() - mc.player.prevPosX, mc.player.getPosZ() - mc.player.prevPosZ) * (double) mc.timer.timerSpeed * 20.0D);
    }
    public static void drawSquareItemStack(ItemStack stack, float x, float y, float size, boolean withoutOverlay, boolean scale, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0);

        // Применяем масштабирование, если это необходимо
        if (scale) {
            GL11.glScaled(scaleValue, scaleValue, scaleValue);
        }

        // Вычисляем размер иконки, чтобы она поместилась в квадрат
        float iconSize = size * 0.8f; // Например, иконка будет 80% от размера квадрата

        // Рисуем квадратный фон (можно использовать drawRect или аналогичный метод)
       // drawRect(0, 0, size, size, getColorForStack(stack, 1.0f)); // Замените на нужный вам цвет

        // Рисуем иконку предмета
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, (int) ((size - iconSize) / 2), (int) ((size - iconSize) / 2));


        // Рисуем оверлей, если это необходимо
        if (withoutOverlay) {
            mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, (int) ((size - iconSize) / 2), (int) ((size - iconSize) / 2));
        }

        RenderSystem.popMatrix();
    }


    public static void drawItemStack(ItemStack stack, float x, float y, boolean withoutOverlay, boolean scale, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0);
        if (scale) GL11.glScaled(scaleValue, scaleValue, scaleValue);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (withoutOverlay) mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, 0, 0);
        RenderSystem.popMatrix();
    }

    public static int calculatePing() {
        return mc.player.connection.getPlayerInfo(mc.player.getUniqueID()) != null ?
                mc.player.connection.getPlayerInfo(mc.player.getUniqueID()).getResponseTime() : 0;
    }

    public static String serverIP() {
        return mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null && !mc.isSingleplayer() ? mc.getCurrentServerData().serverIP : "";
    }

    public static List<Function> getSorted(StyledFont font) {
        List<Function> modules = Managment.FUNCTION_MANAGER.getFunctions();
        modules.sort((o1, o2) -> {
            float width1 = font.getWidth(o1.name) + 4;
            float width2 = font.getWidth(o2.name) + 4;
            return Float.compare(width2, width1);
        });
        return modules;
    }

    public static void drawFace(float x, float y, float width, float height, float radius, AbstractClientPlayerEntity target) {
        ResourceLocation skin = target.getLocationSkin();
        mc.getTextureManager().bindTexture(skin);
        drawTexture(x, y, width, height, radius, 1F);
    }

}
