package ru.levinov.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ColorSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.ColorUtil;

import java.awt.*;

import static ru.levinov.util.render.RenderUtil.Render2D.*;

/**
 * @author levin1337
 * @since 06.06.2023
 */
@FunctionAnnotation(name = "Arrows", type = Type.Render)
public class ArrowsFunction extends Function {

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventRender render) {
            if (render.isRender2D()) {
                render2D(render);
            }
        }
    }

    /**
     * Обработка события рендера в 2D.
     * Выполняет отрисовку стрелочек до игроков на экране.
     */
    private void render2D(EventRender render) {
        for (Entity entity : mc.world.getPlayers()) {

            if (entity == mc.player || !entity.botEntity) continue;

            double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * mc.getRenderPartialTicks() - mc.getRenderManager().info.getProjectedView().getX();
            double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * mc.getRenderPartialTicks() - mc.getRenderManager().info.getProjectedView().getZ();

            double cos = MathHelper.cos(mc.player.rotationYaw * (Math.PI * 2 / 360));
            double sin = MathHelper.sin(mc.player.rotationYaw * (Math.PI * 2 / 360));
            double rotY = -(z * cos - x * sin);
            double rotX = -(x * cos + z * sin);

            float angle = (float) (Math.atan2(rotY, rotX) * 180 / Math.PI);
            double x2 = 50 * MathHelper.cos(Math.toRadians(angle)) + render.scaledResolution.scaledWidth() / 2f;
            double y2 = 50 * MathHelper.sin(Math.toRadians(angle)) + render.scaledResolution.scaledHeight() / 2f;

            GlStateManager.pushMatrix();
            GlStateManager.disableBlend();
            GlStateManager.translated(x2, y2, 0);
            GlStateManager.rotatef(angle, 0, 0, 1);

            int clr = Managment.FRIEND_MANAGER.isFriend(entity.getName().getString())
                    ? ColorUtil.rgba(0, 255, 0, 255)
                    : Managment.STYLE_MANAGER.getCurrentStyle().getColor(entity.getEntityId()
            );

            drawTriangle(-4, -1F, 4F, 7F, new Color(0, 0, 0, 32));
            drawTriangle(-3F, 0F, 3F, 5F, new Color(clr));
            BloomHelper.registerRenderCall(() -> {
                GlStateManager.pushMatrix();
                GlStateManager.disableBlend();
                GlStateManager.translated(x2, y2, 0);
                GlStateManager.rotatef(angle, 0, 0, 1);
                drawTriangle(-3F, 0F, 3F, 5F, new Color(clr));
                GlStateManager.enableBlend();
                GlStateManager.popMatrix();
            });
            GlStateManager.enableBlend();
            GlStateManager.popMatrix();
        }
    }
}