package ru.levinov.modules.impl.render;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.math.PlayerPositionTracker;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.ProjectionUtils;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;
import ru.levinov.util.world.WorldUtil;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

@FunctionAnnotation(
        name = "ParticleTrails",
        type = Type.Render
)
public class ParticleTrails extends Function {
    private final ModeSetting mode = new ModeSetting("Мод", "Снежинки", new String[]{"Снежинки", "Сердечки", "Звездочки", "Шарики", "Доллары", "Орбизы / PNG"});
    CopyOnWriteArrayList<Point> points = new CopyOnWriteArrayList();

    public ParticleTrails() {
        this.addSettings(new Setting[]{this.mode});
    }

    public void onEvent(Event event) {
        if (event instanceof EventPacket e) {
            IPacket var4 = e.getPacket();
            if (var4 instanceof CUseEntityPacket var3) {
                ;
            }
        }

        Iterator var13;
        if (event instanceof EventMotion e) {
            var13 = mc.world.getAllEntities().iterator();

            label83:
            while(true) {
                do {
                    Entity entity;
                    do {
                        if (!var13.hasNext()) {
                            break label83;
                        }

                        entity = (Entity)var13.next();
                    } while(!(entity instanceof ClientPlayerEntity));

                    ClientPlayerEntity l = (ClientPlayerEntity)entity;
                } while(mc.player.motion.z == 0.0 && mc.player.motion.x == 0.0);

                float rcord = 0.0F;
                float rycord = 0.0F;
                rcord = ThreadLocalRandom.current().nextFloat(-0.18F, 0.18F);
                rycord = ThreadLocalRandom.current().nextFloat(0.3F, 1.93F);
                this.createPoints(mc.player.getPositionVec().add((double)rcord, (double)rycord, (double)rcord));
            }
        }

        if (event instanceof EventRender e) {
            if (e.isRender2D()) {
                if (this.points.size() > 100) {
                }

                var13 = this.points.iterator();

                while(true) {
                    while(var13.hasNext()) {
                        Point point = (Point)var13.next();
                        long alive = System.currentTimeMillis() - point.createdTime;
                        if (alive <= point.aliveTime * 3L && mc.player.canVectorBeSeenFixed(point.position) && PlayerPositionTracker.isInView(point.position)) {
                            Vector2d pos = ProjectionUtils.project(point.position.x, point.position.y, point.position.z);
                            if (pos != null) {
                                float sizeDefault = point.size;
                                point.update();
                                float size = 1.0F;
                                int alpha = 255;
                                if (this.mode.is("Снежинки")) {
                                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/slowflame.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt(ColorUtil.getColorStyle((float)this.points.indexOf(point)), alpha));
                                //    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/slowflame.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt((new Color(255, 255, 255, 100)).getRGB(), alpha / 2));
                                }

                                if (this.mode.is("Сердечки")) {
                                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/heart.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt(ColorUtil.getColorStyle((float)this.points.indexOf(point)), alpha));
                            //        RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/heart.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt((new Color(255, 255, 255, 100)).getRGB(), alpha / 2));
                                }

                                if (this.mode.is("Доллары")) {
                                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/dollar.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt(ColorUtil.getColorStyle((float)this.points.indexOf(point)), alpha));
                        //            RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/dollar.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt((new Color(255, 255, 255, 100)).getRGB(), alpha / 2));
                                }

                                if (this.mode.is("Звездочки")) {
                                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/star.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt(ColorUtil.getColorStyle((float)this.points.indexOf(point)), alpha));
                          //          RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/star.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt((new Color(255, 255, 255, 100)).getRGB(), alpha / 2));
                                }

                                if (this.mode.is("Орбизы / PNG")) {
                                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/firefly.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt(ColorUtil.getColorStyle((float)this.points.indexOf(point)), alpha));
                           //         RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/firefly.png"), (float)pos.x, (float)pos.y, sizeDefault * size * 1.6F, sizeDefault * size * 1.6F, RenderUtil.reAlphaInt((new Color(255, 255, 255, 100)).getRGB(), alpha / 2));
                                }

                                if (this.mode.is("Шарики")) {
                                    BloomHelper.registerRenderCall(() -> {
                                        RenderUtil.Render2D.drawRoundCircle((float)pos.x, (float)pos.y, (sizeDefault + 1.0F) * size, Color.BLACK.getRGB());
                                        RenderUtil.Render2D.drawRoundCircle((float)pos.x, (float)pos.y, sizeDefault * size, ColorUtil.getColorStyle((float)this.points.indexOf(point)));
                                    });
                                    RenderUtil.Render2D.drawRoundCircle((float)pos.x, (float)pos.y, (sizeDefault + 1.0F) * size, Color.BLACK.getRGB());
                                    RenderUtil.Render2D.drawRoundCircle((float)pos.x, (float)pos.y, sizeDefault * size, ColorUtil.getColorStyle((float)this.points.indexOf(point)));
                                }
                            }
                        } else {
                            this.points.remove(point);
                        }
                    }

                    return;
                }
            }
        }

    }

    private void createPoints(Vector3d position) {
        for(int i = 0; i < ThreadLocalRandom.current().nextInt(1, 2); ++i) {
            this.points.add(new Point(this, position));
        }

    }
}
final class Point {
    public Vector3d position;
    public Vector3d motion;
    public Vector3d animatedMotion;
    public long aliveTime;
    public float size;
    public long createdTime;

    public Point(ParticleTrails var1, Vector3d position) {
        this.createdTime = System.currentTimeMillis();
        Vector3d mot = new Vector3d((double)ThreadLocalRandom.current().nextFloat(-0.002F, 0.002F), 0.001, (double)ThreadLocalRandom.current().nextFloat(-0.008F, 0.008F));
        this.position = new Vector3d(position.x, position.y, position.z);
        this.motion = new Vector3d((double)ThreadLocalRandom.current().nextFloat(-0.04F, 0.04F), 0.0, (double)ThreadLocalRandom.current().nextFloat(-0.09F, 0.09F));
        this.animatedMotion = mot;
        this.size = ThreadLocalRandom.current().nextFloat(14.0F, 15.0F);
        this.aliveTime = ThreadLocalRandom.current().nextLong(5400L, 6600L);
    }

    public void update() {
        if (this.isGround()) {
            this.motion.y = 0.01;
            Vector3d var10000 = this.motion;
            var10000.y *= 2.0;
        }

        this.animatedMotion.x = (double)AnimationMath.fast((float)this.animatedMotion.x, (float)this.motion.x, 0.04F);
        this.animatedMotion.y = (double) AnimationMath.fast((float)this.animatedMotion.y, (float)this.motion.y, 0.0F);
        this.animatedMotion.z = (double)AnimationMath.fast((float)this.animatedMotion.z, (float)this.motion.z, 0.04F);
        this.position = this.position.add(this.animatedMotion);
    }

    boolean isGround() {
        Vector3d position = this.position.add(this.animatedMotion);
        AxisAlignedBB bb = new AxisAlignedBB(position.x - 0.1, position.y - 0.1, position.z - 0.1, position.x + 0.1, position.y + 0.1, position.z + 0.1);
        return WorldUtil.TotemUtil.getSphere(new BlockPos(position), 2.0F, 4, true, true, 0).stream().anyMatch((blockPos) -> {
            return !IMinecraft.mc.world.getBlockState(blockPos).isAir() && bb.intersects(new AxisAlignedBB(blockPos)) && AxisAlignedBB.calcSideHit(new AxisAlignedBB(blockPos.add(0, 1, 0)), position, new double[]{2.0}, (Direction)null, 0.10000000149011612, 0.10000000149011612, 0.10000000149011612) == Direction.DOWN;
        });
    }
}
