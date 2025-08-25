package ru.levinov.modules.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.events.impl.render.EventRender3D2;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.util.math.MathUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR_TEX;
import static org.lwjgl.opengl.GL11.GL_QUADS;

@FunctionAnnotation(name = "Trails", type = Type.Render)
public class TrailsFunction extends Function {

    ModeSetting mode = new ModeSetting("Режим", "След", "След", "Дэш");

    public TrailsFunction() {
        addSettings(mode);
    }

    public List<Point> points = new ArrayList<>();

    TimerUtil timerUtil = new TimerUtil();

    @Override
    public void onEvent(Event event) {
        if (mode.is("Дэш")) {
            if (event instanceof EventRender3D2 render) {
                if (mc.world != null && mc.player != null) {
                    if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                    } else {
                        if (render.isRender3D2()) {
                            if (mc.world == null) return;
                            if (Math.abs(mc.player.motion.x) > 0 || Math.abs(mc.player.motion.y) > 0 || Math.abs(mc.player.motion.z) > 0) {
                                Vector3d playerPos = interpolatePlayerPosition(render.partialTicks);
                                if (timerUtil.hasTimeElapsed(5, true)) {
                                    points.add(new Point(playerPos, new ResourceLocation("client/images/particle/firefly.png")));
                                }
                            }
                            long currentTime = System.currentTimeMillis();
                            points.removeIf(p -> (currentTime - p.time) > 400);
                            RenderSystem.pushMatrix();
                            RenderSystem.disableLighting();
                            RenderSystem.depthMask(false);
                            RenderSystem.enableBlend();
                            RenderSystem.shadeModel(7425);
                            RenderSystem.disableCull();
                            RenderSystem.disableAlphaTest();
                            RenderSystem.blendFuncSeparate(770, 1, 0, 1);
                            int i = 1;
                            for (Point p : points) {
                                MatrixStack ms = RenderUtil.Render2D.matrixFrom(render.matrixStack, mc.getRenderManager().info);
                                ms.push();
                                double x = p.pos.x;
                                double y = p.pos.y;
                                double z = p.pos.z;
                                double sizeX = 0.5f;
                                double sizeY = 0.5f;
                                ActiveRenderInfo camera = mc.getRenderManager().info;
                                ms.translate(-mc.getRenderManager().info.getProjectedView().getX(), -mc.getRenderManager().info.getProjectedView().getY(), -mc.getRenderManager().info.getProjectedView().getZ());
                                ms.translate((x + 0.2f), (y + 0.4f), z);
                                Quaternion r = camera.getRotation().copy();
                                mc.getTextureManager().bindTexture(new ResourceLocation("client/images/particle/firefly.png"));
                                buffer.begin(GL_QUADS, POSITION_COLOR_TEX);
                                ms.translate(-sizeX / 2f, -sizeY / 2f, 0);
                                ms.rotate(r);
                                ms.translate(sizeX / 2f, sizeY / 2f, 0);
                                int color = ColorUtil.getColorStyle(i);
                                int alpha = (int) (129 - (System.currentTimeMillis() - p.time) / 5);
                                buffer.pos(ms.getLast().getMatrix(), 0, -sizeY, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(0, 0).endVertex();
                                buffer.pos(ms.getLast().getMatrix(), -sizeX, -sizeY, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(0, 1).endVertex();
                                buffer.pos(ms.getLast().getMatrix(), -sizeX, 0, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(1, 1).endVertex();
                                buffer.pos(ms.getLast().getMatrix(), 0, 0, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(1, 0).endVertex();
                                tessellator.draw();
                                ms.translate(-sizeX / 2f, -sizeY / 2f, 0);
                                ms.rotate(Vector3f.XP.rotation(90));
                                r.conjugate();
                                ms.rotate(r);
                                ms.translate(sizeX / 2f, sizeY / 2f, 0);
                                ms.translate(-x, -y, -z);
                                ms.pop();
                                i++;
                            }
                            RenderSystem.defaultBlendFunc();
                            RenderSystem.disableBlend();
                            RenderSystem.enableCull();
                            RenderSystem.enableAlphaTest();
                            RenderSystem.depthMask(true);
                            RenderSystem.popMatrix();
                        }
                    }
                }
            }
        }
        if (mode.is("След")) {
            if (event instanceof EventRender render && render.isRender3D()) {
                if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                } else {
                    long currentTime = System.currentTimeMillis();
                    points.removeIf(p -> (currentTime - p.time) > 400);
                    Vector3d playerPos = interpolatePlayerPosition(render.partialTicks);
                    points.add(new Point(playerPos));
                    render3DPoints();
                    RenderSystem.color4f(1, 1, 1, 1);
                }
            }
        }
    }
    private Vector3d interpolatePlayerPosition(float partialTicks) {
        return new Vector3d(MathUtil.interpolate(mc.player.getPosX(), mc.player.prevPosX, partialTicks), MathUtil.interpolate(mc.player.getPosY(), mc.player.prevPosY, partialTicks), MathUtil.interpolate(mc.player.getPosZ(), mc.player.prevPosZ, partialTicks)
        );
    }

    private void render3DPoints() {
        startRendering();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        int index = 0;
        for (Point p : points) {
            int c = ColorUtil.getColorStyle(index);
            float red = (float) (c >> 16 & 255) / 255.0F;
            float green = (float) (c >> 8 & 255) / 255.0F;
            float blue = (float) (c & 255) / 255.0F;
            float alpha = (float) index / (float) points.size() * 0.8f;
            Vector3d pos = p.pos.subtract(mc.getRenderManager().info.getProjectedView());
            buffer.pos(pos.x, pos.y + mc.player.getHeight(), pos.z).color(red, green, blue, alpha).endVertex();
            buffer.pos(pos.x, pos.y, pos.z).color(red, green, blue, alpha).endVertex();
            index++;
        }
        tessellator.draw();
        RenderSystem.lineWidth(2);
        stopRendering();
    }
    class Point {
        public ResourceLocation location;
        public Vector3d pos;
        public long time;
        public float rotation = 0,scale = ThreadLocalRandom.current().nextInt(1,5);
        public Vector3d rotate = new Vector3d(ThreadLocalRandom.current().nextDouble(0,1), ThreadLocalRandom.current().nextDouble(0,1), ThreadLocalRandom.current().nextDouble(0,1));
        public Point(Vector3d pos) {
            this.pos = pos;
            this.time = System.currentTimeMillis();
        }
        public Point(Vector3d pos,ResourceLocation location) {
            this.location=location;
            this.pos = pos;
            this.time = System.currentTimeMillis();
        }
    }

    private void startRendering() {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.color4f(1, 1, 1, 0.5f);
    }

    private void stopRendering() {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}
