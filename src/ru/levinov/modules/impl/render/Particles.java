package ru.levinov.modules.impl.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.game.EventAttack;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.events.impl.world.EventRenderWorld;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.MarkerUtils.Mathf;
import ru.levinov.util.math.PlayerPositionTracker;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.ProjectionUtils;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;
import ru.levinov.util.world.WorldUtil;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

@FunctionAnnotation(name="Particles", type=Type.Render)
public class Particles extends Function {
    private ModeSetting mode = new ModeSetting("Мод", "Орбизы", "Орбизы","Снежинки","Сердечки","Звезда","Доллары");


    public SliderSetting Size = new SliderSetting("Размер", 10.0f, 10.0f, 20.0f, 1.0f);

    public SliderSetting timeslider = new SliderSetting("Время", 10000L, 8000L, 18000L, 1000L);
    private CopyOnWriteArrayList<Orbiz> orbizes = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<ParticlesPoints> points = new CopyOnWriteArrayList();

    public SliderSetting morecolvo = new SliderSetting("Количество", 8f, 2F, 15F, 1F).setVisible(() -> mode.is("Орбизы"));


    public Particles() {
        this.addSettings(mode, Size,timeslider,morecolvo);
    }

    @Override
    public void onEvent(Event event2) {
        if (mode.is("Орбизы")) {
            if (event2 instanceof EventAttack e) {
                if (e.getEntity() != null && e.getEntity() instanceof PlayerEntity) {
                    for (int i = 0; i < morecolvo.getValue().floatValue(); ++i) {
                        orbizes.add(new Orbiz(e.getEntity().getPositionVec().add(0, Mathf.randomize(0, 1), 0), mc.getRenderManager().getCameraOrientation(), e.getEntity().lastTickPosX, e.getEntity().lastTickPosY + Mathf.randomize(0, 1), e.getEntity().lastTickPosZ, ru.levinov.util.MarkerUtils.RenderUtil.twocolor((double) Math.abs(System.currentTimeMillis() / (long) 20) / 100.0D + 1.0D * (0.60D / 50.0D))));
                    }
                }
            }
            if (event2 instanceof EventRenderWorld event) {
                for (Orbiz p : orbizes) {
                    if (System.currentTimeMillis() - p.time > timeslider.getValue().floatValue()) {
                        orbizes.remove(p);
                    }
                    if (mc.player.getPositionVec().distanceTo(p.position) > 15) {
                        orbizes.remove(p);
                    }
                    p.update();
                    EntityRendererManager rm = mc.getRenderManager();
                    final double x = p.lastposition.x + (p.position.x - p.lastposition.x) * mc.timer.renderPartialTicks - rm.info.getProjectedView().getX();
                    final double y = p.lastposition.y + (p.position.y - p.lastposition.y) * mc.timer.renderPartialTicks - rm.info.getProjectedView().getY() + 0.5f;
                    final double z = p.lastposition.z + (p.position.z - p.lastposition.z) * mc.timer.renderPartialTicks - rm.info.getProjectedView().getZ();
                    event.getMatrixStack().push();
                    event.getMatrixStack().translate(x, y, z);
                    event.getMatrixStack().rotate(p.quaternion);
                    event.getMatrixStack().scale(0.01f, 0.01f, 0.01f);
                    ru.levinov.util.MarkerUtils.RenderUtil.vegaRender(() -> {
                        ru.levinov.util.MarkerUtils.RenderUtil.drawCircleM(event.getMatrixStack(), 0, 0, 0, 360, 8, 1, ru.levinov.util.MarkerUtils.RenderUtil.setAlpha(p.color, (int) p.alp / 3));
                        ru.levinov.util.MarkerUtils.RenderUtil.drawCircleM(event.getMatrixStack(), 0, 0, 0, 360, 6, 1, ru.levinov.util.MarkerUtils.RenderUtil.setAlpha(p.color, (int) p.alp / 3));
                        ru.levinov.util.MarkerUtils.RenderUtil.drawCircleM(event.getMatrixStack(), 0, 0, 0, 360, 3, 1, ru.levinov.util.MarkerUtils.RenderUtil.setAlpha(p.color, (int) p.alp));
                    });
                    event.getMatrixStack().pop();
                }
            }
        } else {
            Event e;
            CUseEntityPacket use;
            EventPacket e2;
            IPacket var4;
            if (event2 instanceof EventPacket && (var4 = (e2 = (EventPacket)event2).getPacket()) instanceof CUseEntityPacket && (use = (CUseEntityPacket)var4).getAction() == CUseEntityPacket.Action.ATTACK) {
                Entity entity = use.getEntityFromWorld(Particles.mc.world);
                if (Particles.mc.world != null && entity != null) {
                    this.createPoints(entity.getPositionVec().add(0.0, 1.0, 0.0));
                }
            }
            if (event2 instanceof EventRender && ((EventRender)(e = (EventRender)event2)).isRender2D()) {
                if (this.points.size() > 100) {
                    this.points.remove(0);
                }
                for (ParticlesPoints point : this.points) {
                    long alive = System.currentTimeMillis() - point.createdTime;
                    if (alive <= point.aliveTime) {
                        if (mc.player.canVectorBeSeenFixed(point.position) && PlayerPositionTracker.isInView(point.position)) {
                            Vector2d pos = ProjectionUtils.project(point.position.x, point.position.y, point.position.z);
                            if (pos == null) continue;
                            point.update();
                            float sized = Size.getValue().floatValue();
                            switch (this.mode.get()) {
                                case "Снежинки": {
                                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/slowflame.png"), (float)pos.x, (float)pos.y, sized, sized, ColorUtil.getColorStyle(this.points.indexOf(point)));//картинка снежинки
                                    break;
                                }
                                case "Сердечки": {
                                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/heart.png"), (float)pos.x, (float)pos.y, sized, sized, ColorUtil.getColorStyle(this.points.indexOf(point)));//картинка сердечка
                                    break;
                                }
                                case "Звезда": {
                                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/star.png"), (float)pos.x, (float)pos.y, sized, sized, ColorUtil.getColorStyle(this.points.indexOf(point)));//картинка звезды
                                    break;
                                }
                                case "Доллары": {
                                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/particle/dollar.png"), (float)pos.x, (float)pos.y, sized, sized, ColorUtil.getColorStyle(this.points.indexOf(point)));//картинка звезды
                                    break;
                                }
                            }
                            continue;
                        }
                    }
                    points.remove(point);
                }
                return;
            }
        }
    }
    private void createPoints(Vector3d position2) {
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(6, 25); ++i) {
            points.add(new ParticlesPoints(this, position2));
        }
    }
}
final class Orbiz {
    public Color color;
    public Vector3d position;
    public Vector3d lastposition;
    private Vector3d motion;
    private Vector3d animatedMotion;
    public final long time;
    public double alp = 255;
    public Quaternion quaternion;

    public Orbiz(Vector3d position, Quaternion quaternion, double lastX, double lastY, double lastZ, Color color) {
        this.position = position;
        this.quaternion = quaternion;
        this.lastposition = new Vector3d(lastX, lastY, lastZ);
        this.color = color;
        time = System.currentTimeMillis();
        motion = new Vector3d(Mathf.randomize(-0.1f, 0.1f), 0.05, Mathf.randomize(-0.1f, 0.1f)); // Увеличьте y-скорость
        animatedMotion = new Vector3d(0.0, 0.0, 0.0);
    }

    public void update() {
        if (alp > 0) {
            alp -= 0.5f; // Уменьшите скорость уменьшения альфа-канала
        }
        // Уменьшите скорость падения
        this.motion.y -= 0.0008; // Падение будет медленным
        this.animatedMotion.x = AnimationMath.fast((float) this.animatedMotion.x, (float) this.motion.x, 0.2f);
        this.animatedMotion.y = AnimationMath.fast((float) this.animatedMotion.y, (float) this.motion.y, 0.3f);
        this.animatedMotion.z = AnimationMath.fast((float) this.animatedMotion.z, (float) this.motion.z, 0.4f);

        // Обновите позицию
        this.position = this.position.add(this.animatedMotion);
        this.lastposition = this.position.add(this.animatedMotion);
    }
}
final class ParticlesPoints {
    private final Particles this$0;
    public Vector3d position;
    public Vector3d motion;
    public Vector3d animatedMotion;
    public long aliveTime;
    public float size;
    public long createdTime;

    public ParticlesPoints(Particles var1, Vector3d position2) {
        this.this$0 = var1;
        this.createdTime = System.currentTimeMillis();
        this.position = new Vector3d(position2.x, position2.y, position2.z);
        this.motion = new Vector3d(ThreadLocalRandom.current().nextFloat(-0.01f, 0.02f), 0.0, ThreadLocalRandom.current().nextFloat(-0.01f, 0.02f));
        this.animatedMotion = new Vector3d(0.0, 0.0, 0.0);
        this.size = ThreadLocalRandom.current().nextFloat(5.0f, 8.0f);
        this.aliveTime = ThreadLocalRandom.current().nextLong(3000L, (long) Managment.FUNCTION_MANAGER.particleses.timeslider.getValue().floatValue());
    }

    public void update() {
        if (this.isGround()) {
            this.motion.y = 1.5;
            Vector3d var10000 = this.motion;
            var10000.x *= 1.05;
            var10000 = this.motion;
            var10000.z *= 1.05;
        } else {
            this.motion.y = -0.01;
            Vector3d var10000 = this.motion;
            var10000.y *= 1.5;
        }
        this.animatedMotion.x = AnimationMath.fast((float)this.animatedMotion.x, (float)this.motion.x, 8.0f);
        this.animatedMotion.y = AnimationMath.fast((float)this.animatedMotion.y, (float)this.motion.y, 8.2f);
        this.animatedMotion.z = AnimationMath.fast((float)this.animatedMotion.z, (float)this.motion.z, 8.0f);
        this.position = this.position.add(this.animatedMotion);
    }

    boolean isGround() {
        Vector3d position2 = this.position.add(this.animatedMotion);
        AxisAlignedBB bb = new AxisAlignedBB(position2.x - 0.1, position2.y - 0.1, position2.z - 0.1, position2.x + 0.1, position2.y + 0.1, position2.z + 0.1);
        return WorldUtil.TotemUtil.getSphere(new BlockPos(position2), 2.0f, 4, false, true, 0).stream().anyMatch(blockPos -> !IMinecraft.mc.world.getBlockState((BlockPos)blockPos).isAir() && bb.intersects(new AxisAlignedBB((BlockPos)blockPos)) && AxisAlignedBB.calcSideHit(new AxisAlignedBB(blockPos.add(0, 1, 0)), position2, new double[]{2.0}, (Direction)null, (double)0.1f, (double)0.1f, (double)0.1f) == Direction.DOWN);
    }
}