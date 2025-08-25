package ru.levinov.modules.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.misc.HudUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.awt.*;

/**
 * @author dedinside
 * @since 26.06.2023
 */
@FunctionAnnotation(name = "Predictions", type = Type.Render)
public class PearlPrediction extends Function {

    private BooleanOption pearl = new BooleanOption("Эндер-Жемчюги", true);
    private BooleanOption предметы = new BooleanOption("Предметы", true);




    public PearlPrediction(){
        addSettings(pearl,предметы);
    }
    @Override
    public void onEvent(Event event) {
        if (pearl.get()) {
            if (event instanceof EventRender && ((EventRender) event).isRender3D()) {
                RenderSystem.pushMatrix();
                RenderSystem.translated(-mc.getRenderManager().renderPosX(), -mc.getRenderManager().renderPosY(), -mc.getRenderManager().renderPosZ());
                RenderSystem.enableBlend();
                RenderSystem.disableTexture();
                RenderSystem.disableDepthTest();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                RenderSystem.lineWidth(2);
                buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);

                for (Entity e : mc.world.getAllEntities()) {
                    if (e instanceof EnderPearlEntity pearl) {
                        renderLine(pearl);
                    }
                }
                buffer.finishDrawing();
                WorldVertexBufferUploader.draw(buffer);
                RenderSystem.enableDepthTest();
                RenderSystem.enableTexture();
                RenderSystem.disableBlend();
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                RenderSystem.popMatrix();
            }
            if (event instanceof EventRender r2d && ((EventRender) event).isRender2D()) {
                for (Entity e : mc.world.getAllEntities()) {
                    if (e instanceof EnderPearlEntity pearl) {
                        renderIcon(pearl);
                    }
                }
            }
        }
        if (предметы.get()) {
            if (event instanceof EventRender && ((EventRender) event).isRender3D()) {
                RenderSystem.pushMatrix();
                RenderSystem.translated(-mc.getRenderManager().renderPosX(), -mc.getRenderManager().renderPosY(), -mc.getRenderManager().renderPosZ());
                RenderSystem.enableBlend();
                RenderSystem.disableTexture();
                RenderSystem.disableDepthTest();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                RenderSystem.lineWidth(2);
                buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);

                for (Entity e : mc.world.getAllEntities()) {
                    if (e instanceof ItemEntity tridentEntity) {
                        renderLineItem(tridentEntity);
                    }
                }
                buffer.finishDrawing();
                WorldVertexBufferUploader.draw(buffer);
                RenderSystem.enableDepthTest();
                RenderSystem.enableTexture();
                RenderSystem.disableBlend();
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                RenderSystem.popMatrix();
            }
            if (event instanceof EventRender r2d && ((EventRender) event).isRender2D()) {
                for (Entity e : mc.world.getAllEntities()) {
                    if (e instanceof ItemEntity pearl) {
                        renderIconItem(pearl);
                    }
                }
            }
        }
    }


    private void renderLine(EnderPearlEntity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec().add(0, 0, 0);
        Vector3d pearlMotion = pearl.getMotion();
        Vector3d lastPosition = pearlPosition;
        for (int i = 0; i <= 150; i++) {
            lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = updatePearlMotion(pearl, pearlMotion);

            if (shouldEntityHit(pearlPosition.add(0, 1, 0), lastPosition.add(0, 1, 0)) || pearlPosition.y <= 0) {
                break;
            }
            float[] colors = getLineColor(i);
            buffer.pos(lastPosition.x, lastPosition.y, lastPosition.z).color(colors[0], colors[1], colors[2], 1).endVertex();
            buffer.pos(pearlPosition.x, pearlPosition.y, pearlPosition.z).color(colors[0], colors[1], colors[2], 1).endVertex();
        }
    }



    private void renderIcon(EnderPearlEntity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec();
        Vector3d pearlMotion = pearl.getMotion();
        Vector3d lastPosition = pearlPosition;

        float ticks = 0;

        for (int i = 0; i <= 150; i++) {
            lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = updatePearlMotion(pearl, pearlMotion);

            if (shouldEntityHit(pearlPosition.add(0, 1, 0), lastPosition.add(0, 1, 0)) || pearlPosition.y <= 0) {
                break;
            }

            ticks++;
        }

        double timeInSeconds = (ticks * 50) / 1000.0;

        if(pearl.getSpawnTime() == 0){
            pearl.setSpawnTime(timeInSeconds);
        }

        float s = (float) (timeInSeconds / pearl.getSpawnTime() * 360);

        Vector2d p = ru.levinov.util.MarkerUtils.RenderUtil.project(pearlPosition.x, pearlPosition.y, pearlPosition.z);
        if (p != null) {
            p.y -= 30;
            GL11.glPushMatrix();
            GL11.glTranslatef((float) p.x, (float) p.y + 3, 0);

            double seconds = Math.round(timeInSeconds * 10) / 10.0;

            float wid = Fonts.msSemiBold[17].getWidth(seconds + " сек.") + 5;

            RenderUtil.Render2D.drawTransparency(-wid/2f,3,wid,10,new Vector4f(),200);

            Fonts.msSemiBold[17].drawCenteredString(new MatrixStack(), seconds + " сек.", 0, 6, ColorUtil.getColorStyle(360));

            RenderUtil.Render2D.drawCircle(0,-12,0,361,9,1,true, Color.black.getRGB());
            RenderUtil.Render2D.drawCircle((float) 0, (float) -12, (float) -s/2+90, s/2+90,8,1,false, Managment.STYLE_MANAGER.getCurrentStyle());

            HudUtil.drawItemStack(new ItemStack(Items.ENDER_PEARL),-8.5f,-20.5f,false,false,1.0F);
            GL11.glPopMatrix();
        }
    }

    private Vector3d updatePearlMotion(EnderPearlEntity pearl, Vector3d originalPearlMotion) {
        Vector3d pearlMotion = originalPearlMotion;
        if (pearl.isInWater()) {
            pearlMotion = pearlMotion.scale(0.8f);
        } else {
            pearlMotion = pearlMotion.scale(0.99f);
        }

        if (!pearl.hasNoGravity())
            pearlMotion.y -= pearl.getGravityVelocity();

        return pearlMotion;
    }












    private void renderLineItem(ItemEntity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec().add(0, 0, 0);
        Vector3d pearlMotion = pearl.getMotion();
        Vector3d lastPosition = pearlPosition;
        for (int i = 0; i <= 150; i++) {
            lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = updateItemMotion(pearl, pearlMotion);

            if (shouldEntityHit(pearlPosition.add(0, 1, 0), lastPosition.add(0, 1, 0)) || pearlPosition.y <= 0) {
                break;
            }
            float[] colors = getLineColor(i);
            buffer.pos(lastPosition.x, lastPosition.y, lastPosition.z).color(colors[0], colors[1], colors[2], 1).endVertex();
            buffer.pos(pearlPosition.x, pearlPosition.y, pearlPosition.z).color(colors[0], colors[1], colors[2], 1).endVertex();
        }
    }



    private void renderIconItem(ItemEntity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec();
        Vector3d pearlMotion = pearl.getMotion();
        Vector3d lastPosition = pearlPosition;

        float ticks = 0;

        for (int i = 0; i <= 150; i++) {
            lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = updateItemMotion(pearl, pearlMotion);

            if (shouldEntityHit(pearlPosition.add(0, 1, 0), lastPosition.add(0, 1, 0)) || pearlPosition.y <= 0) {
                break;
            }

            ticks++;
        }

        double timeInSeconds = (ticks * 50) / 1000.0;

        if(pearl.getSpawnTime() == 0){
            pearl.setSpawnTime(timeInSeconds);
        }

        float s = (float) (timeInSeconds / pearl.getSpawnTime() * 360);

        Vector2d p = ru.levinov.util.MarkerUtils.RenderUtil.project(pearlPosition.x, pearlPosition.y, pearlPosition.z);
        if (p != null) {
            p.y -= 30;
            GL11.glPushMatrix();
            GL11.glTranslatef((float) p.x, (float) p.y + 3, 0);
            GL11.glPopMatrix();
        }
    }

    private Vector3d updateItemMotion(ItemEntity pearl, Vector3d originalPearlMotion) {
        Vector3d pearlMotion = originalPearlMotion;
        if (pearl.isInWater()) {
            pearlMotion = pearlMotion.scale(0.8f);
        } else {
            pearlMotion = pearlMotion.scale(0.99f);
        }

        if (!pearl.hasNoGravity())
            pearlMotion.y -= pearl.hoverStart;

        return pearlMotion;
    }

    private boolean shouldEntityHit(Vector3d pearlPosition, Vector3d lastPosition) {
        final RayTraceContext rayTraceContext = new RayTraceContext(
                lastPosition,
                pearlPosition,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                mc.player
        );
        final BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);

        return blockHitResult.getType() == RayTraceResult.Type.BLOCK;
    }

    private float[] getLineColor(int index) {
        int color = Managment.STYLE_MANAGER.getCurrentStyle().getColor(index * 10);
        return RenderUtil.IntColor.rgb(color);
    }
}
