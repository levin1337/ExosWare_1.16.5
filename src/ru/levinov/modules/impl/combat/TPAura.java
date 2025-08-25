package ru.levinov.modules.impl.combat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CEntityActionPacket.Action;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventInput;
import ru.levinov.events.impl.player.EventInteractEntity;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.events.impl.render.EventRender2;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.MarkerUtils.GLUtils;
import ru.levinov.util.MarkerUtils.Interpolator;
import ru.levinov.util.MarkerUtils.Mathf;
import ru.levinov.util.math.*;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.RenderUtil.IntColor;
import ru.levinov.util.world.InventoryUtil;

@FunctionAnnotation(
        name = "TPAura",
        type = Type.Combat,
        desc = "Нанесение урона за 100+ блоков"
)
public class TPAura extends Function {
    public static LivingEntity target = null;

    public Vector2f rotate = new Vector2f(0.0F, 0.0F);
    private Vector3d lastHandledVec = new Vector3d(0.0D, 0.0D, 0.0D);
    private final ModeSetting sortMode = new ModeSetting("Сортировать", "По всему", new String[]{"По всему", "По здоровью", "По дистанции"});


    private final MultiBoxSetting targets = new MultiBoxSetting("Цели", new BooleanOption[]{new BooleanOption("Игроки", true), new BooleanOption("Друзья", false), new BooleanOption("Голые", true), new BooleanOption("Мобы", false)});


    public final MultiBoxSetting settings = new MultiBoxSetting("Настройки", new BooleanOption[]{new BooleanOption("Только критами", true), new BooleanOption("Отжимать щит", true), new BooleanOption("Ломать щит", true), new BooleanOption("Таргет ЕСП", true), new BooleanOption("Коррекция движения", true)});

    private final ModeSetting targetVisualize = new ModeSetting("Визуализация цели", "Прицел","Выключен", "Прицел", "Круг", "Бабл", "Призраки");

    public final SliderSetting distance = new SliderSetting("Дистанция", 100f, 25f, 500f, 5f);


    private final BooleanOption onlySpaceCritical = (new BooleanOption("Только с пробелом", false)).setVisible(() -> {
        return this.settings.get(0);
    });

    private boolean hasRotated;
    private long cpsLimit = 0L;
    private final Vector2d markerPosition = new Vector2d();
    private final ResourceLocation markerLocation = new ResourceLocation("client/images/target.png");
    private final ResourceLocation markerLocation2 = new ResourceLocation("client/images/target2.png");

    public TPAura() {
        addSettings(new Setting[]{this.targets, this.sortMode,targetVisualize, this.distance,  this.settings, this.onlySpaceCritical});
    }
    public static float hpbypass = 0.1F;
    public void onEvent(Event event) {
        if (event instanceof EventInteractEntity entity) {
            if (target != null) {
                entity.setCancel(true);
            }
        }

        if (event instanceof EventUpdate updateEvent) {
            if (target == null || !this.isValidTarget(target)) {
                target = this.findTarget();
            }

            if (target == null) {
                this.rotate = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
                this.cpsLimit = System.currentTimeMillis();
                return;
            }

            this.attackAndRotateOnEntity(target);
        }

        if (event instanceof EventMotion motionEvent) {
            this.handleMotionEvent(motionEvent);
        }

        if (event instanceof EventRender e) {
            if (target != null) {
                if (this.targetVisualize.is("Прицел")) {
                    this.draw();
                }
                if (this.targetVisualize.is("Бабл")) {
                    this.draw2();
                }
            }
        }
        if (event instanceof EventRender2 event2) {
            if (target != null) {
                if (targetVisualize.is("Призраки")) {
                    if (event2.isWorld3D() && target != null) {
                        draw3(event2);
                    }
                }
                if (this.targetVisualize.is("Круг")) {
                    if (event2.isWorld3D() && target != null) {
                        drawCircle(event2);
                    }
                }
            }
        }

    }

    private double distanceToTarget() {
        return (double)mc.player.getDistance(target);
    }
    private final long startTime = System.currentTimeMillis();


    private void draw3(EventRender2 event2) {
        MatrixStack matrixStack = event2.matrixStack;
        Vector3d vector3d = RenderUtil.Render3D.getEntityPosition(target, event2.partialTicks);

        float time = (float) ((System.currentTimeMillis() - startTime) / 1500f + (Math.sin((((System.currentTimeMillis() - startTime) / 1500f))) / 10f));
        float offsetY = 0;

        boolean alternate = true;

        for (int iteration = 0; iteration < 3; iteration++) {
            for (float i = time * 360; i < time * 360 + 90; i += 2) {
                float max = time * 360 + 90;
                float angle = MathUtil.normalize(i, time * 360 - 45, max);
                float radius = 0.71f;

                double radians = Math.toRadians(i);
                double cosPos = Math.cos(radians) * radius;
                double sinPos = Math.sin(radians) * radius;

                float sizeMultiplier = (!alternate ? 0.25f : 0.15f) * (Math.max(alternate ? 0.25f : 0.15f, alternate ? angle : (1 + (0.4f - angle)) / 2f) + 0.45f);
                float size = sizeMultiplier * 1.5f;

                int color1 = ColorUtil.getColorStyle(90);
                int color2 = ColorUtil.getColorStyle(180);

                matrixStack.push();
                matrixStack.translate(vector3d.x + cosPos, vector3d.y + target.getHeight() / 2f + offsetY, vector3d.z + sinPos);
                matrixStack.rotate(mc.getRenderManager().getCameraOrientation());
                RenderUtil.Render2D.drawTexture(matrixStack, new ResourceLocation("client/images/particle/firefly.png"), -size / 2f, -size / 2f - 0.45f, size / 2f, size, size, color1, color1, color2, color2);
                matrixStack.pop();
            }

            time *= -1;
            offsetY += target.getHeight() / 4f;
            alternate = !alternate;
        }
    }


    private void draw() {
        if (target != null && mc.player != null) {
            Vector3d interpolatedPosition = ru.levinov.util.MarkerUtils.RenderUtil.interpolate(target, mc.getRenderPartialTicks());
            double x = interpolatedPosition.x;
            double y = interpolatedPosition.y;
            double z = interpolatedPosition.z;
            Vector2d marker = ru.levinov.util.MarkerUtils.RenderUtil.project(x, y + (double)((target.getEyeHeight() + 0.4F) * 0.5F), z);
            if (marker == null) {
                return;
            }

            this.markerPosition.x = (Double) Interpolator.lerp(this.markerPosition.x, marker.x, 1.0);
            this.markerPosition.y = (Double)Interpolator.lerp(this.markerPosition.y, marker.y, 1.0);
            float size = 120.0F;
            double angle = (double)((float)Mathf.clamp(0.0, 30.0, (Math.sin((double)System.currentTimeMillis() / 150.0) + 1.0) / 2.0 * 30.0));
            double scale = (double)((float)Mathf.clamp(0.8, 1.0, (Math.sin((double)System.currentTimeMillis() / 500.0) + 1.0) / 2.0 * 1.0));
            double rotate = (double)((float)Mathf.clamp(0.0, 360.0, (Math.sin((double)System.currentTimeMillis() / 1000.0) + 1.0) / 2.0 * 360.0));
            GlStateManager.pushMatrix();
            GL11.glTranslatef((float)this.markerPosition.x, (float)this.markerPosition.y, 0.0F);
            GL11.glScaled(scale, scale, 1.0);
            double sc = Mathf.clamp(0.75, 1.0, 1.0 - this.distanceToTarget() / this.distance.getValue().doubleValue());
            sc = (Double)Interpolator.lerp(scale, sc, 0.5);
            GL11.glScaled(sc, sc, sc);
            GL11.glTranslatef((float)(-this.markerPosition.x) - size / 2.0F, (float)(-this.markerPosition.y), 0.0F);
            int color = ColorUtil.getColorStyle(270.0F);
            GLUtils.startRotate((float)this.markerPosition.x + size / 2.0F, (float)this.markerPosition.y, (float)(5.0 - (angle - 5.0) + rotate));
            GlStateManager.enableBlend();
            ru.levinov.util.MarkerUtils.RenderUtil.drawImage(this.markerLocation, this.markerPosition.x, this.markerPosition.y - (double)(size / 2.0F), (double)size, (double)size, color);
            GlStateManager.disableBlend();
            GLUtils.endRotate();
            GlStateManager.popMatrix();
        }

    }

    private void draw2() {
        if (target != null && mc.player != null) {
            Vector3d interpolatedPosition = ru.levinov.util.MarkerUtils.RenderUtil.interpolate(target, mc.getRenderPartialTicks());
            double x = interpolatedPosition.x;
            double y = interpolatedPosition.y;
            double z = interpolatedPosition.z;
            Vector2d marker = ru.levinov.util.MarkerUtils.RenderUtil.project(x, y + (double)((target.getEyeHeight() + 0.4F) * 0.5F), z);
            if (marker == null) {
                return;
            }

            this.markerPosition.x = (Double) Interpolator.lerp(this.markerPosition.x, marker.x, 1.0);
            this.markerPosition.y = (Double)Interpolator.lerp(this.markerPosition.y, marker.y, 1.0);
            float size = 100.0F;
            double angle = (double)((float)Mathf.clamp(0.0, 360.0, (Math.sin((double)System.currentTimeMillis() / 150.0) + 1.0) / 2.0 * 30.0));
            double scale = (double)((float)Mathf.clamp(1.0, 1.0, (Math.sin((double)System.currentTimeMillis() / 500.0) + 1.0) / 2.0 * 1.0));
            GlStateManager.pushMatrix();
            GL11.glTranslatef((float)this.markerPosition.x, (float)this.markerPosition.y, 0.0F);
            GL11.glScaled(scale, scale, 0);
            double sc = Mathf.clamp(0.75, 1.0, 1.0 - this.distanceToTarget() / this.distance.getValue().doubleValue());
            sc = (Double)Interpolator.lerp(scale, sc, 0);
            GL11.glScaled(sc, sc, sc);
            GL11.glTranslatef((float)(-this.markerPosition.x) - size / 2.0F, (float)(-this.markerPosition.y), 0.0F);
            int color = ColorUtil.getColorStyle(270.0F);
            GLUtils.startRotate((float)this.markerPosition.x + size / 2.0F, (float)this.markerPosition.y, (float)(5.0 - (angle - 5.0)));
            GlStateManager.enableBlend();
            ru.levinov.util.MarkerUtils.RenderUtil.drawImage(this.markerLocation2, this.markerPosition.x, this.markerPosition.y - (double)(size / 2.0F), (double)size, (double)size, color);
            GlStateManager.disableBlend();
            GLUtils.endRotate();
            GlStateManager.popMatrix();
        }

    }







    private void drawCircle(EventRender2 event2) {
        MatrixStack matrixStack = event2.matrixStack;
        Vector3d vector3d = RenderUtil.Render3D.getEntityPosition(target, event2.partialTicks);
        boolean alternate = true;
        for (int iteration = 0; iteration < 1; iteration++) {
            for (float i = 2  * 600; i < 3.800f * 340; i += 1) {
                float max = 3 * 360 + 90;
                float angle = MathUtil.normalize(i, 3 * 360, max);
                float radius = 0.68f;
                double radians = Math.toRadians(i * 10);
                double cosPos = Math.cos(radians) * radius;
                double sinPos = Math.sin(radians) * radius;

                float sizeMultiplier = (!alternate ? 0.25f : 0.15f) * (Math.max(alternate ? 0.25f : 0.15f, alternate ? angle : (1 + (0.4f - angle)) / 2f) + 0.45f);
                float size = sizeMultiplier * 1.5f;

                int color1 = ColorUtil.getColorStyle(30);
                int color2 = ColorUtil.getColorStyle(360);



                double duration = 1200.0;


                double elapsed = (double)System.currentTimeMillis() % duration;
                boolean side = elapsed > duration / 2.3;
                double progress = elapsed / (duration / 2.3);
                if (side) {
                    --progress;
                } else {
                    progress = 1.3 - progress;
                }
                progress = progress < 2 ? 1 * progress * progress : 1 - Math.pow(-3.0 * progress + 6.0, 3.0) / 0f;
                matrixStack.push();
                matrixStack.translate(vector3d.x + cosPos, vector3d.y + 0.600f + progress, vector3d.z + sinPos);
                matrixStack.rotate(mc.getRenderManager().getCameraOrientation());
                RenderUtil.Render2D.drawTexture(matrixStack, new ResourceLocation("client/images/particle/firefly.png"), -size / 2f, -size / 2f - 0.45f, size / 2f, size, size, color1, color1, color2, color2);
                matrixStack.pop();
            }
        }
    }

    private void handleMotionEvent(EventMotion motionEvent) {
        if (target != null && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
            motionEvent.setYaw(this.rotate.x);
            motionEvent.setPitch(this.rotate.y);
            mc.player.rotationYawHead = this.rotate.x;
            mc.player.renderYawOffset = this.rotate.x;
            mc.player.rotationPitchHead = this.rotate.y;
        }
    }



    private void attackAndRotateOnEntity(LivingEntity target) {
        hasRotated = false;
        if (shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
            attackTarget(target);
        } else {
        }
        if (!hasRotated) {
            setRotation(target, false);
        }
    }

    private void attackTarget(LivingEntity targetEntity) {
        if (this.settings.get(1) && mc.player.isBlocking()) {
            mc.playerController.onStoppedUsingItem(mc.player);
            mc.playerController.onStoppedUsingItem(mc.player);
        }
        boolean sprint = false;
        if (CEntityActionPacket.lastUpdatedSprint && !mc.player.isInWater()) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.STOP_SPRINTING));
            sprint = true;
        }
        this.cpsLimit = System.currentTimeMillis() + 500L;
        if (forHitAuraRule()) {
            hitAuraTPPre();
        }
        mc.playerController.attackEntity(mc.player, targetEntity);
        mc.player.swingArm(Hand.MAIN_HAND);
        if (forHitAuraRule()) {
            hitAuraTPPost();
        }





        if (sprint) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.START_SPRINTING));
        }

    }





    private double sqrtAt(double val1) {
        return Math.sqrt(val1 * val1);
    }

    private double sqrtAt(double val1, double val2) {
        return Math.sqrt(val1 * val1 + val2 * val2);
    }

    private double sqrtAt(double val1, double val2, double val3) {
        return Math.sqrt(val1 * val1 + val2 * val2 + val3 * val3);
    }

    private double positive(double val) {
        return val < 0.0D ? -val : val;
    }




    private void send(double x, double y, double z, boolean ground) {
        mc.getConnection().sendPacket(new CPlayerPacket.PositionPacket(x, y, z, ground));
    }

    private void send(double x, double y, double z) {
        this.send(x, y, z, false);
    }

    private void send(boolean ground) {
        mc.getConnection().sendPacket(new CPlayerPacket(ground));
    }


    public void teleportActionOfActionType(boolean pre, Vector3d to) {
        Vector3d self = mc.player.getPositionVec();
        double dx = this.positive(self.x - to.x);
        double dy = this.positive(self.y - to.y);
        double dz = this.positive(self.z - to.z);
        int grInt = mc.player.isOnGround() ? 1 : 0;
        float distanceDensity = 1.0F;
        if (pre) {
            double diffs;
            int packetCount;
            diffs = this.sqrtAt(dx, dz);
            for (packetCount = (int) (diffs / (8.317D * (double) distanceDensity)) + grInt; packetCount > 0; --packetCount) {
                this.send(false);
            }
            this.send(to.x, self.y - (double) grInt * 1.0E-4D * (double) packetCount, to.z);
            this.lastHandledVec = new Vector3d(to.x, self.y - (double) grInt * 1.0E-4D * (double) packetCount, to.z);
            if (grInt == 0) {
                mc.player.getPositionVec().y = self.y;
            }
        } else {
            this.send(self.x, self.y - this.positive(grInt - 1) * 1.0E-4D * 2.0D, self.z);
        }
    }

    public boolean vectorRule(Vector3d to, double defaultDistanceMax, double distanceMin) {
        Vector3d self = mc.player.getPositionVec();
        double dx = self.x - to.x;
        double dy = self.y - to.y;
        double dz = self.z - to.z;
        boolean isInRange = false;
        if (this.sqrtAt(dx, dy, dz) < distanceMin) {
            return false;
        } else {
            isInRange = this.positive(dy) < defaultDistanceMax && this.sqrtAt(dx, dz) + this.positive(dy) < distance.getValue().floatValue();
        }
        return isInRange;
    }

    public Vector3d targetWhitePos(LivingEntity target, double distanceMin) {
        distanceMin -= target.getHeight();
        if (distanceMin < 0.0D) {
            distanceMin = 0.0D;
        }
        Vector3d vec = target.getPositionVec();
        double selfX = mc.player.getPosX();
        double targetX = vec.x;
        double selfY = mc.player.getPosY();
        double targetY = vec.y;
        double yDst = this.positive(selfY - targetY);
        double targetW = (double)target.getWidth() / 2.0D;
        double targetH = target.getHeight();
        double selfZ = mc.player.getPosZ();
        double targetZ = vec.z;
        if (yDst > distanceMin) {
            double appendY = MathHelper.clamp(yDst - distanceMin, 0.0D, distanceMin - this.sqrtAt(selfX - targetX, selfZ - targetZ));
            double tempAppend;
            for(tempAppend = 0.0D; tempAppend < appendY; tempAppend += 0.1D) {
                AxisAlignedBB aabb = new AxisAlignedBB(targetX - targetW / 2.0D, targetY + tempAppend, targetZ - targetW / 2.0D, targetX + targetW / 2.0D, targetY + targetH + tempAppend, targetZ + targetW / 2.0D);
                if (aabb == null) {
                    tempAppend -= 0.1D;
                } else if (tempAppend > 0.0D && !mc.world.getCollisionShapes(target, aabb).collect(Collectors.toList()).isEmpty()) {
                    tempAppend -= 0.1D;
                    break;
                }
            }
            appendY = tempAppend;
            if (tempAppend < 0.0D) {
                appendY = 0.0D;
            }
            vec = vec.add(0.0D, appendY, 0.0D);
        }
        return vec;
    }

    public boolean forHitAuraRule() {
        if (target != null && target.isAlive()) {
            boolean sata = this.entityRule();
            if (sata) {
                sata = true;
            }
            double auraRangeMin = MathHelper.clamp(200, 3.0D, 5.2D - (double)target.getHeight());
            double auraRangeMax = MathHelper.clamp(200 - 0.1D, 0.0D, 5.2D);
            return sata && this.vectorRule(this.targetWhitePos(target, auraRangeMin), auraRangeMax, auraRangeMin);
        } else {
            return false;
        }
    }

    public boolean entityRule() {
        if (target != null && target.isAlive()) {
            boolean selfCollided = mc.player.getBoundingBox() == null || mc.world.getCollisionShapes(mc.player, mc.player.getBoundingBox()).collect(Collectors.toList()).isEmpty();
            boolean targetCollided = target.getBoundingBox() == null || mc.world.getCollisionShapes(target, target.getBoundingBox()).collect(Collectors.toList()).isEmpty();
            return selfCollided || !targetCollided;
        } else {
            return false;
        }
    }

    public void hitAuraTPPre() {
        double auraRangeMin = MathHelper.clamp(200, 3.0D, 5.2D - (double)target.getHeight());
        this.teleportActionOfActionType(true, this.targetWhitePos(target, auraRangeMin));
    }

    public void hitAuraTPPost() {
        double auraRangeMin = MathHelper.clamp(200, 3.0D, 5.2D - (double)target.getHeight());
        this.teleportActionOfActionType(false, this.targetWhitePos(target, auraRangeMin));
    }



    private boolean shouldAttack(LivingEntity targetEntity) {
        return this.canAttack() && targetEntity != null && this.cpsLimit <= System.currentTimeMillis();
    }

    private void setRotation(LivingEntity base, boolean attack) {
        this.hasRotated = true;
        Vector3d vec3d = AuraUtil.getVector(base);
        double diffX = vec3d.x;
        double diffY = vec3d.y;
        double diffZ = vec3d.z;
        float[] rotations = new float[]{(float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F, (float)(-Math.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))))};
        float deltaYaw = MathHelper.wrapDegrees(MathUtil.calculateDelta(rotations[0], this.rotate.x));
        float deltaPitch = MathUtil.calculateDelta(rotations[1], this.rotate.y);
        float limitedYaw = Math.min(Math.max(Math.abs(deltaYaw), 1.0F), 360.0F);
        float limitedPitch = Math.min(Math.max(Math.abs(deltaPitch), 1.0F), 90.0F);
        float finalYaw = this.rotate.x + (deltaYaw > 0.0F ? limitedYaw : -limitedYaw) + ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F);
        float finalPitch = MathHelper.clamp(this.rotate.y + (deltaPitch > 0.0F ? limitedPitch : -limitedPitch) + ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F), -90.0F, 90.0F);
        float gcd = GCDUtil.getGCDValue();
        finalYaw = (float)((double)finalYaw - (double)(finalYaw - this.rotate.x) % (double)gcd);
        finalPitch = (float)((double)finalPitch - (double)(finalPitch - this.rotate.y) % (double)gcd);
        this.rotate.x = finalYaw;
        this.rotate.y = finalPitch;
    }


    public boolean canAttack() {

        boolean onSpace = this.onlySpaceCritical.get() && mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown();
        boolean reasonForAttack = mc.player.isPotionActive(Effects.BLINDNESS) || mc.player.isOnLadder() || mc.player.isInWater() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.isRidingHorse() || mc.player.abilities.isFlying || mc.player.isElytraFlying();
        if (!(this.getDistance(target) >= (double)this.distance.getValue().floatValue()) && !(mc.player.getCooledAttackStrength(1.5F) < 0.92F)) {
            if (Managment.FUNCTION_MANAGER.freeCam.player != null) {
                return true;
            } else if (!reasonForAttack && this.settings.get(0)) {
                return onSpace || !mc.player.isOnGround() && mc.player.fallDistance > 0.0F;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private LivingEntity findTarget() {
        List<LivingEntity> targets = new ArrayList();
        Iterator var2 = mc.world.getAllEntities().iterator();

        while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            if (entity instanceof LivingEntity && this.isValidTarget((LivingEntity)entity)) {
                targets.add((LivingEntity)entity);
            }
        }

        if (targets.isEmpty()) {
            return null;
        } else {
            if (targets.size() > 1) {
                switch (this.sortMode.get()) {
                    case "По всему":
                        targets.sort(Comparator.comparingDouble((target) -> {
                            if (target instanceof PlayerEntity player) {
                                return -this.getEntityArmor(player);
                            } else if (target instanceof LivingEntity livingEntity) {
                                return (double)(-livingEntity.getTotalArmorValue());
                            } else {
                                return 0.0;
                            }
                        }).thenComparing((o, o1) -> {
                            double health = this.getEntityHealth((LivingEntity)o);
                            double health1 = this.getEntityHealth((LivingEntity)o1);
                            return Double.compare(health, health1);
                        }).thenComparing((object, object2) -> {
                            double d2 = this.getDistance((LivingEntity)object);
                            double d3 = this.getDistance((LivingEntity)object2);
                            return Double.compare(d2, d3);
                        }));
                        break;
                    case "По здоровью":
                        Comparator var10001 = Comparator.comparingDouble(this::getEntityHealth);
                        ClientPlayerEntity var10002 = mc.player;
                        Objects.requireNonNull(var10002);
                        targets.sort(var10001.thenComparingDouble(entityIn -> var10002.getDistance((Entity) entityIn)));
                }
            } else {
                this.cpsLimit = System.currentTimeMillis();
            }

            return (LivingEntity)targets.get(0);
        }
    }


    private boolean isValidTarget(LivingEntity base) {
        if (!base.getShouldBeDead() && base.isAlive() && base != mc.player) {
            if (base instanceof PlayerEntity) {
                String playerName = base.getName().getString();
                if (Managment.FRIEND_MANAGER.isFriend(playerName) && !this.targets.get(1) || Managment.FUNCTION_MANAGER.freeCam.player != null && playerName.equals(Managment.FUNCTION_MANAGER.freeCam.player.getName().getString()) || base.getTotalArmorValue() == 0 && (!this.targets.get(0) || !this.targets.get(2))) {
                    return false;
                }
            }


            if (AntiBot.checkBot(base)) {
                return false;
            }

            if ((base instanceof MobEntity || base instanceof AnimalEntity) && !this.targets.get(3)) {
                return false;

            } else if (!(base instanceof ArmorStandEntity) && (!(base instanceof PlayerEntity) || !((PlayerEntity)base).isBot)) {
                return this.getDistance(base) <= (double)(this.distance.getValue().floatValue());

            } else {
                return false;
            }

        } else {
            return false;
        }
    }
    private double getDistance(LivingEntity entity) {
        return AuraUtil.getVector(entity).length();
    }

    public double getEntityArmor(PlayerEntity target) {
        double totalArmor = 0.0;
        Iterator var4 = target.inventory.armorInventory.iterator();

        while(var4.hasNext()) {
            ItemStack armorStack = (ItemStack)var4.next();
            if (armorStack != null && armorStack.getItem() instanceof ArmorItem) {
                totalArmor += this.getProtectionLvl(armorStack);
            }
        }

        return totalArmor;
    }

    public double getEntityHealth(Entity ent) {
        if (ent instanceof PlayerEntity player) {
            double armorValue = this.getEntityArmor(player) / 20.0;
            return (double)(player.getHealth() + player.getAbsorptionAmount()) * armorValue;
        } else if (ent instanceof LivingEntity livingEntity) {
            return (double)(livingEntity.getHealth() + livingEntity.getAbsorptionAmount());
        } else {
            return 0.0;
        }
    }

    private double getProtectionLvl(ItemStack stack) {
        ArmorItem armor = (ArmorItem)stack.getItem();
        double damageReduce = (double)armor.getDamageReduceAmount();
        if (stack.isEnchanted()) {
            damageReduce += (double)EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.25;
        }

        return damageReduce;
    }

    public void onDisable() {
        this.rotate = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        target = null;
        this.cpsLimit = System.currentTimeMillis();
        super.onDisable();
    }

    public static LivingEntity getTarget() {
        return target;
    }
}