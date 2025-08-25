package ru.levinov.modules.impl.combat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CEntityActionPacket.Action;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.*;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.events.impl.render.EventRender2;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.MarkerUtils.GLUtils;
import ru.levinov.util.MarkerUtils.Interpolator;
import ru.levinov.util.MarkerUtils.Mathf;
import ru.levinov.util.glu.GLU;
import ru.levinov.util.math.*;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.GCDfix;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.movement.RotationUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.GaussianBlur;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.world.InventoryUtil;

import static com.mojang.blaze3d.platform.GlStateManager.GL_QUADS;
import static java.lang.Math.hypot;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR_TEX;
import static net.minecraft.util.math.MathHelper.*;
import static org.lwjgl.opengl.GL11C.GL_LINE_LOOP;
import static ru.levinov.modules.impl.combat.Criticals.*;
import static ru.levinov.util.math.MathUtil.calculateDelta;


@SuppressWarnings("all")
@FunctionAnnotation(
        name = "Aura",
        type = Type.Combat,
        desc = "Нанесение урона в радиусе",
        keywords = {"Ебать","AttackAura","Хуячить"}
)

//Reiser пастер
public class Aura extends Function {
    private final ModeSetting rotationMode = new ModeSetting("Мод ротации", "Обычная", "Обычная", "Смарт", "Снапы", "Снапы2", "AAC", "1.8.8", "FunTime","SunRise","KoopinAc");
    private final ModeSetting sortMode = new ModeSetting("Сортировать", "По всему","По всему", "По здоровью", "По дистанции");
    private final MultiBoxSetting targets = new MultiBoxSetting("Цели", new BooleanOption("Игроки", true), new BooleanOption("Друзья", false), new BooleanOption("Голые", true), new BooleanOption("Мобы", false));
    public final MultiBoxSetting settings = new MultiBoxSetting("Настройки",
            new BooleanOption("Только критами", true),
            new BooleanOption("Отжимать щит", true),
            new BooleanOption("Ломать щит", true),
            new BooleanOption("Таргет ЕСП", true),
            new BooleanOption("Коррекция движения", true));

    private final ModeSetting targetVisualize = new ModeSetting("Визуализация цели", "Прицел", "Выключен", "Прицел", "Круг","Круг2","Круг3", "Бабл", "Призраки");
    private final SliderSetting ticksnap = (new SliderSetting("Скорость снапов", 2.0F, 1.0F, 10.0F, 1.0F)).setVisible(() -> {
        return this.rotationMode.is("Снапы2");
    });

    public final SliderSetting distance = new SliderSetting("Дистанция атаки", 3.2F, 2.0F, 6.0F, 0.05F);
    public final SliderSetting rotateDistance = (new SliderSetting("Дистанция ротации", 1.0F, 0.0F, 10.0F, 0.05F)).setVisible(() -> {
        return this.rotationMode.is("Обычная") || this.rotationMode.is("Смарт") || this.rotationMode.is("1.8.8") || this.rotationMode.is("SunRise") || rotationMode.is("KoopinAc");
    });
    public final SliderSetting distanceelytra = new SliderSetting("Дистанция на элитре", 18.0F, 5.0F, 100.0F, 1F);
    private final ModeSetting elytratargetmode = new ModeSetting("Метод таргета на элитре", "Стрейфический", "Стрейфический", "Обгоняющий","Нету");
    private final BooleanOption onlySpaceCritical = (new BooleanOption("Только с пробелом", false)).setVisible(() -> {
        return this.settings.get(0);
    });

    private SliderSetting forwardfacrot = (new SliderSetting("Форвард фактор", 2.6F, 0.5F, 8.0F, 0.1F));
    private final BooleanOption speedrots = new BooleanOption("Ускорять ротацию", true);
    private final BooleanOption foodattack = new BooleanOption("Не бить если ешь", false);

    public static final BooleanOption silent = new BooleanOption("Коррекция | silent", true);
    public static BooleanOption fixHP = new BooleanOption("Фикс Здоровья", true);
    public static BooleanOption clientlook = new BooleanOption("Поворачивать экран", false);

    private final ModeSetting sprintbypass = new ModeSetting("Метод обхода спринта", "Обычный", "Обычный", "Тайминг", "Нету");

    private final Vector2d markerPosition = new Vector2d();
    private final ResourceLocation markerLocation = new ResourceLocation("client/images/target.png");
    private final ResourceLocation markerLocation2 = new ResourceLocation("client/images/target2.png");
    public static LivingEntity target = null;
    public Vector2f rotate = new Vector2f(0.0F, 0.0F);
    private final long startTime = System.currentTimeMillis();
    int ticksUntilNextAttack;
    private boolean hasRotated;
    private long cpsLimit = 0L;
    double x;
    double y;
    double z;

    public float prevAdditionYaw;
    private TimerUtil timerUtil = new TimerUtil();
    public Aura() {
        addSettings(rotationMode, elytratargetmode, targets, sortMode, targetVisualize, distance,rotateDistance, distanceelytra, forwardfacrot,ticksnap, this.settings, this.onlySpaceCritical, this.speedrots, this.foodattack, this.silent, fixHP,sprintbypass,clientlook);
    }

    private boolean targetVisible;
    public void onEvent(Event event) {
        if (event instanceof EventInteractEntity entity) {
            if (target != null) {
                entity.setCancel(true);
            }
        }

        if (event instanceof EventInput eventInput) {
            if (Managment.FUNCTION_MANAGER.targetStrafe.state && Managment.FUNCTION_MANAGER.targetStrafe.mode.is("Grim")) {

            } else {
                if (silent.get()) {
                    if (!Managment.FUNCTION_MANAGER.strafeFunction.state) {
                        MoveUtil.fixMovement(eventInput, Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion ? rotate.y : rotate.x);
                    }
                }
            }
        }

        if (event instanceof EventUpdate updateEvent) {

            if (target == null || !this.isValidTarget(target)) {
                target = this.findTarget();
            }
            if (target == null) {
                rotate = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
                cpsLimit = System.currentTimeMillis();
                return;
            }
            attackAndRotateOnEntity(target);
        }
        if (event instanceof EventMotion motionEvent) {
            handleMotionEvent(motionEvent);
        }
        if (event instanceof EventRender e) {
            if (target != null) {
                if (targetVisualize.is("Прицел")) {
                    draw();
                }
                if (targetVisualize.is("Бабл")) {
                    draw2();
                }

            }
            if (target != null) {
                if (mc.player.isElytraFlying()) {
                    if (elytratargetmode.is("Обгоняющий")) {
                        drawCalcFactor();
                    }
                    if (elytratargetmode.is("Стрейфический")) {
                        drawCalcFactor();
                    }
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
        if (event instanceof EventRender event2) {
            if (targetVisualize.is("Круг2")) {
                if (target != null) {
                    drawTest(event2);
                }
            }
            if (targetVisualize.is("Круг3")) {
                if (target != null) {
                    draw4(event2);
                }
            }
        }
    }


    private double distanceToTarget() {
        return mc.player.getDistance(target);
    }


    private void handleMotionEvent(EventMotion motionEvent) {
        if (target != null && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
            if (clientlook.get()) {
                motionEvent.setYaw(rotate.x);
                motionEvent.setPitch(rotate.y);
                mc.player.rotationYaw = rotate.x;
                mc.player.renderYawOffset = rotate.x;
                mc.player.rotationPitch = rotate.y;
            } else {
                motionEvent.setYaw(rotate.x);
                motionEvent.setPitch(rotate.y);
                mc.player.rotationYawHead = rotate.x;
                mc.player.renderYawOffset = rotate.x;
                mc.player.rotationPitchHead = rotate.y;
            }
        }
    }
    private int ticks;
    private void attackAndRotateOnEntity(LivingEntity target) {
        hasRotated = false;
        switch (rotationMode.getIndex()) {
            //Плавная
            case 0:
                if (shouldAttack(target) && RayTraceUtil.getMouseOver(target, this.rotate.x, this.rotate.y, (double) this.distance.getValue().floatValue()) == target && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                    attackTarget(target);
                }
                if (!hasRotated && mc.player.isElytraFlying()) {
                    if (elytratargetmode.is("Обгоняющий")) {
                        elytraTarget(target, false);
                    }
                    if (elytratargetmode.is("Стрейфический")) {
                        elytraTargetmove(target, false);
                    }
                    if (elytratargetmode.is("Нету")) {
                        setRotation(target, false);
                    }
                } else {
                    setRotation(target, false);
                }
                break;
            //Смарт
            case 1:
                if (mc.player.isElytraFlying()) {
                    if (shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                        attackTarget(target);
                    } else {

                    }
                } else {
                    if (shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                        attackTarget(target);
                    } else {

                    }
                }
                if (mc.player.isElytraFlying()) {
                    if (elytratargetmode.is("Обгоняющий")) {
                        elytraTarget(target, false);
                    }
                    if (elytratargetmode.is("Стрейфический")) {
                        elytraTargetmove(target, false);
                    }

                    if (elytratargetmode.is("Нету")) {
                        setRotation(target, false);
                    }
                } else {
                    setRotation(target, false);
                }
                break;
            //Снапы
            case 2:
                if (this.shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                    this.attackTarget(target);
                    this.ticksUntilNextAttack = 2;
                }

                if (this.ticksUntilNextAttack > 0) {
                    this.setRotation(target, false);
                    --ticksUntilNextAttack;
                } else {
                    this.rotate.x = mc.player.rotationYaw;
                    this.rotate.y = mc.player.rotationPitch;
                }
                break;
            //Снапы2
            case 3:
                if (this.shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                    this.ticksUntilNextAttack = this.ticksnap.getValue().intValue();
                    this.attackTarget(target);
                }

                if (this.ticksUntilNextAttack > 0) {
                    this.setRotation(target, false);
                    --this.ticksUntilNextAttack;
                } else {
                    this.rotate.x = mc.player.rotationYaw;
                    this.rotate.y = mc.player.rotationPitch;
                }
                break;
            //AAC
            case 4:
                if (this.shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                    this.attackTarget(target);
                }

                if (!this.hasRotated) {
                    this.setRotation(target, false);
                }

                mc.player.rotationYaw = aac(target)[0];
                mc.player.rotationPitch = aac(target)[1];
                break;
            //1.8.8
            case 5:
                this.attackTarget(target);
                this.koopinAcRotation(target, false);
                break;
            case 6:
                if (mc.player.isElytraFlying()) {
                    if (shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                        attackTarget(target);
                    }
                } else {
                    if (shouldAttack(target) && RayTraceUtil.getMouseOver(target, rotate.x, rotate.y, (double) this.distance.getValue().floatValue()) == target && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                        attackTarget(target);
                    }
                }
                if (!hasRotated && mc.player.isElytraFlying()) {
                    if (elytratargetmode.is("Обгоняющий")) {
                        elytraTarget(target, false);
                    }
                    if (elytratargetmode.is("Стрейфический")) {
                        elytraTargetmove(target, false);
                    }

                    if (elytratargetmode.is("Нету")) {
                        funtimeVector(false, 1.5f, 1.4f);
                    }
                } else {
                    funtimeVector(false, 1.5f, 1.4f);
                }
                break;
            case 7:
                if (mc.player.isElytraFlying()) {
                    if (shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                        attackTarget(target);
                    }
                } else {
                    if (shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                        attackTarget(target);
                    }
                }
                if (!hasRotated && mc.player.isElytraFlying()) {
                    if (elytratargetmode.is("Обгоняющий")) {
                        elytraTarget(target, false);
                    }
                    if (elytratargetmode.is("Стрейфический")) {
                        elytraTargetmove(target, false);
                    }
                    if (elytratargetmode.is("Нету")) {
                        getVectorRotation(target, false);
                    }
                } else {
                    getVectorRotation(target, false);
                }
                break;
            case 8:
                if (mc.player.isElytraFlying()) {
                    if (shouldAttack(target) && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                        attackTarget(target);
                    }
                } else {
                    if (shouldAttack(target) && RayTraceUtil.getMouseOver(target, rotate.x, rotate.y, (double) this.distance.getValue().floatValue()) == target && !Managment.FUNCTION_MANAGER.autoPotionFunction.isActivePotion) {
                        attackTarget(target);
                    }
                }
                if (!hasRotated && mc.player.isElytraFlying()) {
                    if (elytratargetmode.is("Обгоняющий")) {
                        elytraTarget(target, false);
                    }
                    if (elytratargetmode.is("Стрейфический")) {
                        elytraTargetmove(target, false);
                    }

                    if (elytratargetmode.is("Нету")) {
                        koopinAcRotation(target, false);
                    }
                } else {
                    koopinAcRotation(target, false);
                }
                break;
        }
    }
    private void attackTarget(LivingEntity targetEntity) {
        if (this.settings.get(1) && mc.player.isBlocking()) {
            mc.playerController.onStoppedUsingItem(mc.player);
        }

        if (this.foodattack.get()) {
            PlayerEntity p = (PlayerEntity) mc.world.getEntityByID(mc.player.getEntityId());
            assert p != null;
            if (p.getActiveItemStack().getItem().isFood() && this.foodattack.get()) {
                return;
            }
        }

        boolean sprint = false;

        if (sprintbypass.is("Обычный")) {
            if (CEntityActionPacket.lastUpdatedSprint && !mc.player.isInWater()) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.STOP_SPRINTING));
                sprint = true;
            }
        }
        if (sprintbypass.is("Тайминг")) {
            if (!mc.player.isInWater()) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                sprint = true;
            }
        }


        if (rotationMode.is("1.8.8")) {
        } else {
            cpsLimit = System.currentTimeMillis() + 500L;
        }
        if (Managment.FUNCTION_MANAGER.criticals.state) {
            if (Managment.FUNCTION_MANAGER.criticals.mode.is("OldNCP")) {
                doCrit(0.00001058293536);
                doCrit(0.00000916580235);
                doCrit(0.00000010371854);
            }
            if (Managment.FUNCTION_MANAGER.criticals.mode.is("NCP")) {
                doCrit(0.0625D);
                doCrit(0);
            }
            if (Managment.FUNCTION_MANAGER.criticals.mode.is("UpdatedNCP")) {
                doCrit(0.000000271875);
                doCrit(0);
            }
            if (Managment.FUNCTION_MANAGER.criticals.mode.is("Strict")) {
                doCrit(0.062600301692775);
                doCrit(0.07260029960661);
                doCrit(0);
                doCrit(0);
            }
            if (Managment.FUNCTION_MANAGER.criticals.mode.is("Default")) {
                doCrit(0.01250004768372);
            }
            if (Managment.FUNCTION_MANAGER.criticals.mode.is("Elytra")) {
                for (int i = 0; i < 9; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA && !mc.player.isInLava()) {
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                        mc.player.motion.y = 0.2;
                       // doCrit(0.07260029960661);
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        mc.player.jumpMovementFactor = 0.225f;
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                    }
                }
            }
        }
        mc.playerController.attackEntity(mc.player, targetEntity);
        mc.player.swingArm(Hand.MAIN_HAND);

        if (Managment.FUNCTION_MANAGER.criticals.state) {
            if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            }
        }
        if (settings.get(2)) {
            breakShieldAndSwapSlot();
        }

        if (sprintbypass.is("Обычный")) {
            if (sprint) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.START_SPRINTING));
            }
        }
        if (sprintbypass.is("Тайминг")) {
            if (sprint) {
                mc.player.setSprinting(true);
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
            }
        }
    }

    private void breakShieldAndSwapSlot() {
        LivingEntity targetEntity = target;
        if (targetEntity instanceof PlayerEntity player) {
            if (target.isActiveItemStackBlocking(2) && !player.isSpectator() && !player.isCreative() && (target.getHeldItemOffhand().getItem() == Items.SHIELD || target.getHeldItemMainhand().getItem() == Items.SHIELD)) {
                int slot = this.breakShield(player);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }
            }
        }
    }

    public int breakShield(LivingEntity target) {
        if (this.settings.get(2)) {
            int hotBarSlot = InventoryUtil.getAxe(true);
            if (hotBarSlot != -1) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(hotBarSlot));
                mc.playerController.attackEntity(mc.player, target);
                mc.player.swingArm(Hand.MAIN_HAND);
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                return hotBarSlot;
            } else {
                int inventorySLot = InventoryUtil.getAxe(false);
                if (inventorySLot != -1) {
                    mc.playerController.pickItem(inventorySLot);
                    mc.playerController.attackEntity(mc.player, target);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    return inventorySLot;
                } else {
                    return -1;
                }
            }
        } else {
            return 0;
        }
    }

    private boolean shouldAttack(LivingEntity targetEntity) {
        return this.canAttack() && targetEntity != null && this.cpsLimit <= System.currentTimeMillis();
    }

    public static float[] Moon(Entity entityIn) {
        double x = entityIn.getPosX() - mc.player.getPosX();
        double y = entityIn.getPosY() - (mc.player.getPosY() - ThreadLocalRandom.current().nextFloat(-1, 2F) + ThreadLocalRandom.current().nextFloat(-1, 10F));
        double z = entityIn.getPosZ() - mc.player.getPosZ() + ThreadLocalRandom.current().nextFloat(-0.3F, 0.3F);
        double u = (double) MathHelper.sqrt(x * x + z * z);
        float u2 = (float) (MathHelper.atan2(z, x) * 57.29577951308232 - 90);
        float u3 = (float) (-MathHelper.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};
    }

    public static float[] aac(Entity entityIn) {
        double x = entityIn.getPosX() - mc.player.getPosX();
        double y = entityIn.getPosY() - (mc.player.getPosY() + (double) mc.player.getEyeHeight() - 1);
        double z = entityIn.getPosZ() - mc.player.getPosZ();
        double u = (double) MathHelper.sqrt(x * x + z * z);
        float u2 = (float) (MathHelper.atan2(z, x) * 57.29577951308232 - 90.0);
        float u3 = (float) (-MathHelper.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};
    }


    public float[] matrix(LivingEntity entity) {
        double n = this.x + 0.5;
        double diffX = n - mc.player.getPosX();
        double n2 = (this.y + 0.5) / 2.0;
        double posY = mc.player.getPosY();
        double diffY = n2 - (posY + (double) mc.player.getEyeHeight());
        double n3 = this.z + 0.5;
        double diffZ = n3 - mc.player.getPosZ();
        double dist = (double) MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }
    private double deffX;
    private double deffY;
    private double deffZ;
    private void setRotation(LivingEntity base, boolean attack) {
        this.hasRotated = true;
        Vector3d vec3d = AuraUtil.getVector(base);
        deffX = vec3d.x;
        deffY = vec3d.y;
        deffZ = vec3d.z;

        double diffX = vec3d.x;
        double diffY = vec3d.y - 0.5f;
        double diffZ = vec3d.z;

        float[] rotations = new float[]{
                (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F,
                (float) (-Math.toDegrees(Math.atan2(diffY, hypot(diffX, diffZ))))
        };

        float deltaYaw = wrapDegrees(calculateDelta(rotations[0], this.rotate.x));
        float deltaPitch = calculateDelta(rotations[1], this.rotate.y);
        float limitedYaw = Math.min(Math.max(Math.abs(deltaYaw), 1.0F), 360.0F);
        float limitedPitch = Math.min(Math.max(Math.abs(deltaPitch), 1.0F), 90.0F);

        float finalYaw = this.rotate.x + (deltaYaw > 0.0F ? limitedYaw : -limitedYaw) + ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F);
        float finalPitch = MathHelper.clamp(this.rotate.y + (deltaPitch > 0.0F ? limitedPitch : -limitedPitch) + ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F), -90.0F, 90.0F);

        float gcd = GCDUtil.getGCDValue();
        finalYaw = (float) ((double) finalYaw - (double) (finalYaw - this.rotate.x) % (double) gcd);
        finalPitch = (float) ((double) finalPitch - (double) (finalPitch - this.rotate.y) % (double) gcd);
        rotate.x = finalYaw;
        rotate.y = finalPitch;
    }
    float finalYaw;
    float finalPitch;
    private double targetX;
    private double targetY;
    private double targetZ;


    public void elytraTarget(LivingEntity base, boolean attack) {
        this.hasRotated = true;
        Vector3d targetPos = base.getPositionVec();
        Vector3d forward = base.getForward().normalize().scale(forwardfacrot.getValue().floatValue());
        targetPos = targetPos.add(forward);
        targetX = targetPos.x;
        targetY = targetPos.y;
        targetZ = targetPos.z;
        double diffX = targetPos.x - mc.player.getPosX();
        double diffY = targetPos.y - mc.player.getPosY();
        double diffZ = targetPos.z - mc.player.getPosZ();
        float[] rotations = new float[]{(float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f, (float) (-Math.toDegrees(Math.atan2(diffY, hypot(diffX, diffZ))))};
        float deltaYaw = wrapDegrees(calculateDelta(rotations[0], this.rotate.x));
        float deltaPitch = calculateDelta(rotations[1], this.rotate.y);
        float limitedYaw = Math.min(Math.max(Math.abs(deltaYaw), 1.0f), 360.0f);
        float limitedPitch = Math.min(Math.max(Math.abs(deltaPitch), 1.0f), 90.0f);
        finalYaw = this.rotate.x + (deltaYaw > 0.0f ? limitedYaw : -limitedYaw);
        finalPitch = MathHelper.clamp(this.rotate.y + (deltaPitch > 0.0f ? limitedPitch : -limitedPitch), -90.0f, 90.0f);

        float gcd = GCDUtil.getGCDValue();
        finalYaw = (float) ((double) this.finalYaw - (double) (this.finalYaw - this.rotate.x) % (double) gcd);
        finalPitch = (float) ((double) this.finalPitch - (double) (this.finalPitch - this.rotate.y) % (double) gcd);
        rotate.x = this.finalYaw;
        rotate.y = this.finalPitch;
    }

    private double targetXmove;
    private double targetYmove;
    private double targetZmove;

    public void elytraTargetmove(LivingEntity base, boolean attack) {
        this.hasRotated = true;
        Vector3d targetPos = base.getPositionVec();
        Vector3d forward = base.getForward().scale(forwardfacrot.getValue().floatValue());
        targetPos = targetPos.add(forward);
        targetXmove = targetPos.x;
        targetYmove = targetPos.y;
        targetZmove = targetPos.z;
        double diffX = targetPos.x - mc.player.getPosX();
        double diffY = targetPos.y - mc.player.getPosY();
        double diffZ = targetPos.z - mc.player.getPosZ();
        float[] rotations = new float[]{
                (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F,
                (float) (-Math.toDegrees(Math.atan2(diffY, hypot(diffX, diffZ))))
        };

        float deltaYaw = wrapDegrees(calculateDelta(rotations[0], this.rotate.x));
        float deltaPitch = calculateDelta(rotations[1], this.rotate.y);
        float limitedYaw = Math.min(Math.max(Math.abs(deltaYaw), 1.0F), 360.0F);
        float limitedPitch = Math.min(Math.max(Math.abs(deltaPitch), 1.0F), 90.0F);

        finalYaw = this.rotate.x + ((deltaYaw > 0.0F) ? limitedYaw : -limitedYaw);
        finalPitch = MathHelper.clamp(this.rotate.y + (deltaPitch > 0.0F ? limitedPitch : -limitedPitch), -90.0F, 90.0F);

        float gcd = GCDUtil.getGCDValue();
        finalYaw = (float) ((double) this.finalYaw - (double) (this.finalYaw - this.rotate.x) % (double) gcd);
        finalPitch = (float) ((double) this.finalPitch - (double) (this.finalPitch - this.rotate.y) % (double) gcd);

        rotate.x = this.finalYaw;
        rotate.y = this.finalPitch;
    }
    public void koopinAcRotation(LivingEntity entity, boolean attackContext) {
        float rotateRangeValue = mc.player.isElytraFlying() ? distanceelytra.getValue().floatValue() : rotateDistance.getValue().floatValue();
        float rangeValue = distance.getValue().floatValue() + rotateRangeValue;
        hasRotated = true;
        Vector3d vec = getHitBox(entity, rangeValue);
        if (vec == null) {
            vec = entity.getEyePosition(1.0F);
        }
        double x = vec.x - mc.player.getPosX();
        double y = vec.y - mc.player.getEyePosition((float) 1.0f).y;
        double z = vec.z - mc.player.getPosZ();
        double dst = Math.sqrt(Math.pow(x, 2.0) + Math.pow(z, 2.0));
        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90.0);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        //    yawToTarget = (float)((double)yawToTarget + 15);
        float yawToTarget2 = wrapDegrees(yawToTarget);
        float yawDelta = wrapDegrees(yawToTarget2 - rotate.x) / 1.0001f;
        int yawDeltaAbs = (int) Math.abs(yawDelta);

        float f2 = pitchToTarget / 1.0001f - rotate.y / 1.0001f;
        float pitchDeltaAbs = Math.abs(f2);
        float additionYaw = Math.min(Math.max(yawDeltaAbs, 1), 80);
        float additionPitch = Math.max(attackContext && target != null ? pitchDeltaAbs : 1.0f, 2.0f);
        if (Math.abs(additionYaw - this.prevAdditionYaw) <= 3.0f) {
            additionYaw = this.prevAdditionYaw + 3.1f;
        }
        float f3 = rotate.x + (yawDelta > 0.0f ? additionYaw : -additionYaw) * 1.0001f;
        float f4 = MathHelper.clamp(rotate.y + (f2 > 0.0f ? additionPitch : -additionPitch) * 1.0001f, -90.0f, 90.0f);
        rotate.x = f3;
        rotate.y = f4;
        this.prevAdditionYaw = additionYaw;
    }
    public void funtimeVector(boolean attack, float rotationYawSpeed, float rotationPitchSpeed) {
        Vector3d targetPos;
        targetPos = target.getPositionVec().add(0, clamp(mc.player.getPosYEye() - target.getPosY(), 0, target.getHeight() * (mc.player.getDistance(target) / (distance.getValue().floatValue()))), 0);
        Vector3d vec = targetPos.subtract(mc.player.getEyePosition(2.0F));
        hasRotated = true;

        if (vec == null) {
            vec = target.getEyePosition(1.0F);
        }

        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(vec.y, hypot(vec.x, vec.z))));
        float yawDelta = wrapDegrees(yawToTarget - rotate.x);
        float pitchDelta = wrapDegrees(pitchToTarget - rotate.y);
        float yawSpeed = yawDelta / rotationYawSpeed; // Используем rotationYawSpeed для плавности
        float pitchSpeed = pitchDelta / rotationPitchSpeed; // Используем rotationPitchSpeed для плавности
        float yaw = rotate.x + yawSpeed;
        float pitch = clamp(rotate.y + pitchSpeed, -90, 90); // Убираем случайные колебания

        // Проверка на 180 градусов
        float angleDifference = Math.abs(wrapDegrees(yawToTarget - mc.player.rotationYaw));
        if (angleDifference <= 90.0f) {
            if (!shouldAttack(target)) {
                yaw = rotate.x + (mc.player.rotationYaw - rotate.x) / (float) 3; // Убираем случайные колебания
                pitch = clamp(rotate.y + (mc.player.rotationPitch - rotate.y) / (float) 3, -90, 90);
            }
        } else {
            // Если цель за пределами 180 градусов, не обновляем yaw и pitch
            yaw = mc.player.rotationYaw; // Сохраняем текущий угол
            pitch = mc.player.rotationPitch; // Сохраняем текущий угол
        }

        float gcd = GCDfix.getGCDValue();
        yaw -= (yaw - rotate.x) % gcd;
        pitch -= (pitch - rotate.y) % gcd;
        rotate = new Vector2f(yaw, pitch);

        mc.player.rotationYawHead = yaw; // Обновляем угол поворота головы
    }



    public void getVectorRotation(LivingEntity entity, boolean attackContext) {
        float rotateRangeValue = mc.player.isElytraFlying() ? distanceelytra.getValue().floatValue() : rotateDistance.getValue().floatValue();
        float rangeValue = distance.getValue().floatValue() + rotateRangeValue;
        hasRotated = true;
        Vector3d vec = getHitBox(entity, rangeValue);
        if (vec == null) {
            vec = entity.getEyePosition(1.0F);
        }
        double x = vec.x - mc.player.getPosX();
        double y = vec.y - mc.player.getEyePosition((float) 1.0f).y;
        double z = vec.z - mc.player.getPosZ();
        double dst = Math.sqrt(Math.pow(x, 2.0) + Math.pow(z, 2.0));
        float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90.0);
        float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(y, dst)));
        //    yawToTarget = (float)((double)yawToTarget + 15);
        float yawToTarget2 = wrapDegrees(yawToTarget);
        float yawDelta = wrapDegrees(yawToTarget2 - rotate.x) / 1.0001f;
        int yawDeltaAbs = (int) Math.abs(yawDelta);

        float f2 = pitchToTarget / 1.0001f - rotate.y / 1.0001f;
        float pitchDeltaAbs = Math.abs(f2);
        float additionYaw = Math.min(Math.max(yawDeltaAbs, 1), 80);
        float additionPitch = Math.max(attackContext && target != null ? pitchDeltaAbs : 1.0f, 2.0f);
        if (Math.abs(additionYaw - this.prevAdditionYaw) <= 3.0f) {
            additionYaw = this.prevAdditionYaw + 3.1f;
        }
        float f3 = rotate.x + (yawDelta > 0.0f ? additionYaw : -additionYaw) * 1.0001f;
        float f4 = MathHelper.clamp(rotate.y + (f2 > 0.0f ? additionPitch : -additionPitch) * 1.0001f, -90.0f, 90.0f);
        rotate.x = f3;
        rotate.y = f4;
        this.prevAdditionYaw = additionYaw;
    }
    //Smart
    private void updateRotation2(LivingEntity base, boolean attack) {
        hasRotated = true;
        Vector3d targetVector;
        targetVector = target.getPositionVec().add(0, MathHelper.clamp(mc.player.getPosYEye() - target.getPosY(), 0, target.getHeight() * (mc.player.getDistance(target) / distance.getValue().floatValue())), 0).subtract(mc.player.getEyePosition(1));
        float deltaYaw = (float) wrapDegrees(Math.toDegrees(Math.atan2(targetVector.z, targetVector.x)) - 90 - rotate.x);
        float deltaPitch = (float) -Math.toDegrees(Math.atan2(targetVector.y, hypot(targetVector.x, targetVector.z))) - rotate.y;
        float newYaw = rotate.x + deltaYaw;
        float newPitch = MathHelper.clamp(rotate.y + deltaPitch, -90, 90);
        newYaw -= (newYaw - rotate.x) % GCDUtil.getGCDValue();
        newPitch -= (newPitch - rotate.y) % GCDUtil.getGCDValue();
        rotate = new Vector2f(newYaw, newPitch);
    }
    //1.8.8
    private void setRotationfast(LivingEntity base, boolean attack) {
        this.hasRotated = true;
        Vector3d vec3d = AuraUtil.getVector(base);
        double diffX = vec3d.x;
        double diffY = vec3d.y;
        double diffZ = vec3d.z;
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, hypot(diffX, diffZ))));
        float deltaYaw = wrapDegrees(calculateDelta(yaw, this.rotate.x));
        float deltaPitch = calculateDelta(pitch, this.rotate.y);
        float limitedYaw = Math.min(Math.max(Math.abs(deltaYaw), 1.0F), 180.0F);
        float limitedPitch = Math.min(Math.max(Math.abs(deltaPitch), 1.0F), 15.0F);
        float finalYaw = this.rotate.x + (deltaYaw > 0.0F ? limitedYaw : -limitedYaw) + ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F);
        float finalPitch = MathHelper.clamp(this.rotate.y + (deltaPitch > 0.0F ? limitedPitch : -limitedPitch) + ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F), -89.0F, 89.0F);
        float gcd = GCDUtil.getGCDValue();
        finalYaw = (float) ((double) finalYaw - (double) (finalYaw - this.rotate.x) % (double) gcd);
        finalPitch = (float) ((double) finalPitch - (double) (finalPitch - this.rotate.y) % (double) gcd);
        rotate.x = finalYaw;
        rotate.y = finalPitch;
    }


    public boolean canAttack() {
        boolean onSpace = this.onlySpaceCritical.get() && mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown();
        boolean reasonForAttack = mc.player.isPotionActive(Effects.BLINDNESS) || mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ())).getMaterial() == Material.WEB || mc.player.isOnLadder() || mc.player.isInWater() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.isRidingHorse() || mc.player.abilities.isFlying || mc.player.isElytraFlying();
        if (!(this.getDistance(target) >= (double) this.distance.getValue().floatValue()) && !(mc.player.getCooledAttackStrength(1.5F) < 0.92F)) {
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

        while (var2.hasNext()) {
            Entity entity = (Entity) var2.next();
            if (entity instanceof LivingEntity && this.isValidTarget((LivingEntity) entity)) {
                targets.add((LivingEntity) entity);
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
                                return (double) (-livingEntity.getTotalArmorValue());
                            } else {
                                return 0.0;
                            }
                        }).thenComparing((o, o1) -> {
                            double health = this.getEntityHealth((LivingEntity) o);
                            double health1 = this.getEntityHealth((LivingEntity) o1);
                            return Double.compare(health, health1);
                        }).thenComparing((object, object2) -> {
                            double d2 = this.getDistance((LivingEntity) object);
                            double d3 = this.getDistance((LivingEntity) object2);
                            return Double.compare(d2, d3);
                        }));
                        break;
                    case "По дистанции":
                        Aura var6 = Managment.FUNCTION_MANAGER.auraFunction;
                        Objects.requireNonNull(var6);
                        targets.sort(Comparator.comparingDouble(var6::getDistance).thenComparingDouble(this::getEntityHealth));
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
            return (LivingEntity) targets.get(0);
        }
    }
    float elytrarotate1 = 0.0F;


    private boolean isValidTarget(LivingEntity base) {
        if (!base.getShouldBeDead() && base.isAlive() && base != mc.player) {
            if (base instanceof PlayerEntity) {
                String playerName = base.getName().getString();
                if (Managment.FRIEND_MANAGER.isFriend(playerName) && !this.targets.get(1) || Managment.FUNCTION_MANAGER.freeCam.player != null && playerName.equals(Managment.FUNCTION_MANAGER.freeCam.player.getName().getString()) || base.getTotalArmorValue() == 0 && (!this.targets.get(0) || !this.targets.get(2))) {
                    return false;
                }
            }
            if (mc.player.isElytraFlying()) {
                elytrarotate1 = this.distanceelytra.getValue().floatValue();
            }

            if (!mc.player.isElytraFlying()) {
                elytrarotate1 = 0.0F;
            }

            if (AntiBot.checkBot(base)) {
                return false;
            }

            if ((base instanceof MobEntity || base instanceof AnimalEntity) && !this.targets.get(3)) {
                return false;

            } else if (!(base instanceof ArmorStandEntity) && (!(base instanceof PlayerEntity) || !((PlayerEntity) base).isBot)) {
                return this.getDistance(base) <= (double) (this.distance.getValue().floatValue() + (!this.rotationMode.is("Обычная") && !this.rotationMode.is("Смарт") && !this.rotationMode.is("1.8.8") && !this.rotationMode.is("SunRise") && !this.rotationMode.is("KoopinAc") ? 0.0F : rotateDistance.getValue().floatValue() + elytrarotate1));

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

        while (var4.hasNext()) {
            ItemStack armorStack = (ItemStack) var4.next();
            if (armorStack != null && armorStack.getItem() instanceof ArmorItem) {
                totalArmor += this.getProtectionLvl(armorStack);
            }
        }

        return totalArmor;
    }

    public double getEntityHealth(Entity ent) {
        if (ent instanceof PlayerEntity player) {
            double armorValue = this.getEntityArmor(player) / 20.0;
            return (double) (player.getHealth() + player.getAbsorptionAmount()) * armorValue;
        } else if (ent instanceof LivingEntity livingEntity) {
            return (double) (livingEntity.getHealth() + livingEntity.getAbsorptionAmount());
        } else {
            return 0.0;
        }
    }

    private double getProtectionLvl(ItemStack stack) {
        ArmorItem armor = (ArmorItem) stack.getItem();
        double damageReduce = (double) armor.getDamageReduceAmount();
        if (stack.isEnchanted()) {
            damageReduce += (double) EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.25;
        }

        return damageReduce;
    }


    public static Vector3d findHitboxCoord(ListAttack box, Entity target) {
        double yCoord = 0.0;
        switch (box.ordinal()) {
            case 0: {
                yCoord = target.getEyeHeight();
                break;
            }
            case 1: {
                yCoord = target.getEyeHeight() / 2.0f;
                break;
            }
            case 2: {
                yCoord = 0.05;
            }
        }
        return target.getPositionVec().add(0.0, yCoord, 0.0);
    }


    public Vector3d getHitBox(Entity entity, double rotateDistance) {
        if (entity.getDistanceSq(entity) >= 36.0) {
            return null;
        }
        Vector3d head = findHitboxCoord(ListAttack.HEAD, entity);
        Vector3d chest = findHitboxCoord(ListAttack.CHEST, entity);
        Vector3d legs = findHitboxCoord(ListAttack.LEGS, entity);
        ArrayList<Vector3d> points = new ArrayList<Vector3d>(Arrays.asList(head, chest, legs));
       // points.removeIf(point -> {
       //    targetVisible = !isHitBoxVisible(entity, (Vector3d)point, rotateDistance);
      //      return targetVisible;
     //   });
        if (points.isEmpty()) {
            return null;
        }
        points.sort((d1, d2) -> {
            Vector2f r1 = RotationUtil.getDeltaForCoord(rotate, d1);
            Vector2f r2 = RotationUtil.getDeltaForCoord(rotate, d2);
            float y1 = Math.abs(r1.y);
            float y2 = Math.abs(r2.y);
            return (int)((y1 - y2) * 1000.0f);
        });
        return points.get(0);
    }

    private void drawCalcFactor() {
        BlockPos targetBlockPos = null;
        if (elytratargetmode.is("Обгоняющий")) {
            targetBlockPos = new BlockPos(targetX, targetY, targetZ);
        } else if (elytratargetmode.is("Стрейфический")) {
            targetBlockPos = new BlockPos(targetXmove, targetYmove, targetZmove);
        } else if (elytratargetmode.is("Нету")) {
            targetBlockPos = new BlockPos(deffX, deffY, deffZ);
        }

        if (targetBlockPos != null) {
            RenderUtil.Render3D.drawBlockBox(targetBlockPos, ColorUtil.rgba(128, 255, 128, 255));
        }
    }


    private void draw3(EventRender2 e) {
        MatrixStack ms = RenderUtil.Render2D.matrixFrom(e.matrixStack, mc.gameRenderer.getActiveRenderInfo());
        ms.push();
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(770, 1, 0, 1);
        double x = target.getPosX();
        double y = target.getPosY() + target.getHeight() / 2f;
        double z = target.getPosZ();
        double radius = 0.7f;
        float speed = 35;
        float size = 0.50f;
        double distance = 20;
        int lenght = 24;
        int maxAlpha = 255;
        int alphaFactor = 15;
        ActiveRenderInfo camera = mc.getRenderManager().info;
        ms.translate(-mc.getRenderManager().info.getProjectedView().getX(), -mc.getRenderManager().info.getProjectedView().getY(), -mc.getRenderManager().info.getProjectedView().getZ());
        Vector3d interpolated = RenderUtil.Render2D.interpolateDobles(target.getPositionVec(), new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), e.partialTicks);
        interpolated.y += 0.75f;
        ms.translate(interpolated.x + 0.2f, interpolated.y + 0.5f, interpolated.z);
        mc.getTextureManager().bindTexture(new ResourceLocation("client/images/particle/firefly.png"));

        //y1
        for (int i = 0; i < lenght; i++) {
            Quaternion r = camera.getRotation().copy();
            buffer.begin(GL_QUADS, POSITION_COLOR_TEX);
            double angle = 0.15f * (System.currentTimeMillis() - startTime - (i * distance)) / (speed); // Изменение скорости вращения
            double s = sin(angle) * radius;
            double c = cos(angle) * radius;
            ms.translate(s, (c), -c);
            ms.translate(-size / 2f, -size / 2f, 0);
            ms.rotate(r);
            ms.translate(size / 2f, size / 2f, 0);
            int color = ColorUtil.getColorStyle(i);
            int alpha = MathHelper.clamp(maxAlpha - (i * alphaFactor), 0, maxAlpha);
            buffer.pos(ms.getLast().getMatrix(), 0, -size, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(0, 0).endVertex();
            buffer.pos(ms.getLast().getMatrix(), -size, -size, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(0, 1).endVertex();
            buffer.pos(ms.getLast().getMatrix(), -size, 0, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(1, 1).endVertex();
            buffer.pos(ms.getLast().getMatrix(), 0, 0, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(1, 0).endVertex();
            tessellator.draw();
            ms.translate(-size / 2f, -size / 2f, 0);
            r.conjugate();
            ms.rotate(r);
            ms.translate(size / 2f, size / 2f, 0);
            ms.translate(-(s), -(c), (c));
        }
        //y2
        for (int i = 0; i < lenght; i++) {
            Quaternion r = camera.getRotation().copy();
            buffer.begin(GL_QUADS, POSITION_COLOR_TEX);
            double angle = 0.15f * (System.currentTimeMillis() - startTime - (i * distance)) / (speed);
            double s = sin(angle) * radius;
            double c = cos(angle) * radius;
            ms.translate(-s, s, -c);
            ms.translate(-size / 2f, -size / 2f, 0);
            ms.rotate(r);
            ms.translate(size / 2f, size / 2f, 0);
            int color = ColorUtil.getColorStyle(i);
            int alpha = MathHelper.clamp(maxAlpha - (i * alphaFactor), 0, maxAlpha);
            buffer.pos(ms.getLast().getMatrix(), 0, -size, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(0, 0).endVertex();
            buffer.pos(ms.getLast().getMatrix(), -size, -size, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(0, 1).endVertex();
            buffer.pos(ms.getLast().getMatrix(), -size, 0, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(1, 1).endVertex();
            buffer.pos(ms.getLast().getMatrix(), 0, 0, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(1, 0).endVertex();
            tessellator.draw();
            ms.translate(-size / 2f, -size / 2f, 0);
            r.conjugate();
            ms.rotate(r);
            ms.translate(size / 2f, size / 2f, 0);
            ms.translate((s), -(s), (c));
        }
        //y3
        for (int i = 0; i < lenght; i++) {
            Quaternion r = camera.getRotation().copy();
            buffer.begin(GL_QUADS, POSITION_COLOR_TEX);
            double angle = 0.15f * (System.currentTimeMillis() - startTime - (i * distance)) / (speed); // Изменение скорости вращения
            double s = sin(angle) * radius;
            double c = cos(angle) * radius;
            ms.translate(c, c, s);
            ms.translate(-size / 2f, -size / 2f, 0);
            ms.rotate(r);
            ms.translate(size / 2f, size / 2f, 0);
            int color = ColorUtil.getColorStyle(i);
            int alpha = MathHelper.clamp(maxAlpha - (i * alphaFactor), 0, maxAlpha);
            buffer.pos(ms.getLast().getMatrix(), 0, -size, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(0, 0).endVertex();
            buffer.pos(ms.getLast().getMatrix(), -size, -size, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(0, 1).endVertex();
            buffer.pos(ms.getLast().getMatrix(), -size, 0, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(1, 1).endVertex();
            buffer.pos(ms.getLast().getMatrix(), 0, 0, 0).color(RenderUtil.reAlphaInt(color, alpha)).tex(1, 0).endVertex();
            tessellator.draw();
            ms.translate(-size / 2f, -size / 2f, 0);
            r.conjugate();
            ms.rotate(r);
            ms.translate(size / 2f, size / 2f, 0);
            ms.translate(-(c), -(c), -(s));
        }
        ms.translate(-x, -y, -z);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
        ms.pop();
    }


    private float alpha = 0.0f; // Переменная для хранения текущего значения альфа-канала

    private void draw() {
        if (target != null && mc.player != null) {
            Vector3d interpolatedPosition = ru.levinov.util.MarkerUtils.RenderUtil.interpolate(target, mc.getRenderPartialTicks());
            double x = interpolatedPosition.x;
            double y = interpolatedPosition.y;
            double z = interpolatedPosition.z;
            Vector2d marker = ru.levinov.util.MarkerUtils.RenderUtil.project(x, y + (double) ((target.getEyeHeight() + 0.4F) * 0.5F), z);
            if (marker == null) {
                return;
            }

            // Интерполяция позиции маркера
            markerPosition.x = (Double) Interpolator.lerp(this.markerPosition.x, marker.x, 1.0);
            markerPosition.y = (Double) Interpolator.lerp(this.markerPosition.y, marker.y, 1.0);

            // Плавная анимация появления
            alpha += 0.01f; // Увеличиваем альфа на каждом кадре
            if (alpha > 1.0f) {
                alpha = 1.0f; // Ограничиваем значение альфа до 1
            }

            float size = 120.0F;
            double angle = (double) ((float) Mathf.clamp(0.0, 30.0, (Math.sin((double) System.currentTimeMillis() / 150.0) + 1.0) / 2.0 * 30.0));
            double scale = (double) ((float) Mathf.clamp(0.8, 1.0, (Math.sin((double) System.currentTimeMillis() / 500.0) + 1.0) / 2.0 * 1.0));
            double rotate = (double) ((float) Mathf.clamp(0.0, 360.0, (Math.sin((double) System.currentTimeMillis() / 1000.0) + 1.0) / 2.0 * 360.0));

            GlStateManager.pushMatrix();
            GL11.glTranslatef((float) this.markerPosition.x, (float) this.markerPosition.y, 0.0F);
            GL11.glScaled(scale, scale, 1.0);
            double sc = Mathf.clamp(0.75, 1.0, 1.0 - this.distanceToTarget() / this.distance.getValue().doubleValue());
            sc = (Double) Interpolator.lerp(scale, sc, 0.5);
            GL11.glScaled(sc, sc, sc);
            GL11.glTranslatef((float) (-this.markerPosition.x) - size / 2.0F, (float) (-this.markerPosition.y), 0.0F);

            // Получаем цвет с учетом альфа-канала
            int color = ColorUtil.getColorStyle(270.0F);
            GLUtils.startRotate((float) this.markerPosition.x + size / 2.0F, (float) this.markerPosition.y, (float) (5.0 - (angle - 5.0) + rotate));
            GlStateManager.enableBlend();

            // Вызов метода drawImage с цветом и альфа-каналом
            ru.levinov.util.MarkerUtils.RenderUtil.drawImage(
                    this.markerLocation,
                    this.markerPosition.x,
                    this.markerPosition.y - (double) (size / 2.0F),
                    (double) size,
                    (double) size,
                    ColorUtil.setAlphaColor2(color, alpha) // Используем alpha для прозрачности
            );

            GlStateManager.disableBlend();
            GLUtils.endRotate();
            GlStateManager.popMatrix();
        }
    }

    private double circleAnim;
    private void drawTest(EventRender event2) {
        if (target != null && mc.player != null) {
            // Обновление анимации круга
            circleAnim += 0.01F; // Увеличиваем значение circleAnim для анимации

            // Добавление вертикального движения
            float verticalOffset = (float) Math.sin(circleAnim * 2) * 0.6f; // 0.1f - амплитуда движения

            // Рисуем круги с учетом вертикального смещения
            RenderUtil.Render2D.drawCircle3D(Aura.target, 0.8f, event2.partialTicks, 50, 4, Color.black.getRGB(), verticalOffset);
            RenderUtil.Render2D.drawCircle3D(Aura.target, 0.8f, event2.partialTicks, 50, 2, ColorUtil.getColorStyle(360), verticalOffset);
        }
    }



    private void draw2() {
        if (target != null && mc.player != null) {
            Vector3d interpolatedPosition = ru.levinov.util.MarkerUtils.RenderUtil.interpolate(target, mc.getRenderPartialTicks());
            double x = interpolatedPosition.x;
            double y = interpolatedPosition.y;
            double z = interpolatedPosition.z;
            Vector2d marker = ru.levinov.util.MarkerUtils.RenderUtil.project(x, y + (double) ((target.getEyeHeight() + 0.4F) * 0.5F), z);
            if (marker == null) {
                return;
            }
            this.markerPosition.x = (Double) Interpolator.lerp(this.markerPosition.x, marker.x, 1.0);
            this.markerPosition.y = (Double) Interpolator.lerp(this.markerPosition.y, marker.y, 1.0);
            float size = 100.0F;
            double angle = (double) ((float) Mathf.clamp(0.0, 360.0, (Math.sin((double) System.currentTimeMillis() / 150.0) + 1.0) / 2.0 * 30.0));
            double scale = (double) ((float) Mathf.clamp(1.0, 1.0, (Math.sin((double) System.currentTimeMillis() / 500.0) + 1.0) / 2.0 * 1.0));
            GlStateManager.pushMatrix();
            GL11.glTranslatef((float) this.markerPosition.x, (float) this.markerPosition.y, 0.0F);
            GL11.glScaled(scale, scale, 0);
            double sc = Mathf.clamp(0.75, 1.0, 1.0 - this.distanceToTarget() / this.distance.getValue().doubleValue());
            sc = (Double) Interpolator.lerp(scale, sc, 0);
            GL11.glScaled(sc, sc, sc);
            GL11.glTranslatef((float) (-this.markerPosition.x) - size / 2.0F, (float) (-this.markerPosition.y), 0.0F);
            int color = ColorUtil.getColorStyle(270.0F);
            GLUtils.startRotate((float) this.markerPosition.x + size / 2.0F, (float) this.markerPosition.y, (float) (5.0 - (angle - 5.0)));
            GlStateManager.enableBlend();
            ru.levinov.util.MarkerUtils.RenderUtil.drawImage(this.markerLocation2, this.markerPosition.x, this.markerPosition.y - (double) (size / 2.0F), (double) size, (double) size, color);
            GlStateManager.disableBlend();
            GLUtils.endRotate();
            GlStateManager.popMatrix();
        }
    }
    private void draw4(EventRender e) {
        if (target != null && mc.player != null) {
            EntityRendererManager rm = mc.getRenderManager();
            double x = Aura.target.lastTickPosX + (Aura.target.getPosX() - Aura.target.lastTickPosX) * e.partialTicks - rm.info.getProjectedView().getX();
            double y = Aura.target.lastTickPosY + (Aura.target.getPosY() - Aura.target.lastTickPosY) * e.partialTicks - rm.info.getProjectedView().getY() + Math.sin(System.currentTimeMillis() / 4E+2) + 0.95;
            double z = Aura.target.lastTickPosZ + (Aura.target.getPosZ() - Aura.target.lastTickPosZ) * e.partialTicks - rm.info.getProjectedView().getZ();
            float radius = 0.6f;
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_POINT_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glDepthMask(false);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableCull();
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            for (float i = 0; i < Math.PI * 2; i += Math.PI * 5 / 100) {
                alpha = MathHelper.clamp(alpha, 0, 255);
                final double vecX = x + radius * Math.cos(i);
                final double vecZ = z + radius * Math.sin(i);
                RenderUtil.color222(ColorUtil.alpha(ru.levinov.util.MarkerUtils.RenderUtil.twocolor(20 / 100.0D + 1.0D * (i * 50.0D / 56.0D)),(int) 0));
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 4E+2) / 2, vecZ);
                RenderUtil.color222(ColorUtil.alpha(ru.levinov.util.MarkerUtils.RenderUtil.twocolor(20 / 100.0D + 1.0D * (i * 50.0D / 56.0D)), (int) 255));
                GL11.glVertex3d(vecX, y, vecZ);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableCull();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_POINT_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
            GL11.glColor3f(255, 255, 255);
        }
    }

    private void drawCircle(EventRender2 event2) {
        MatrixStack matrixStack = event2.matrixStack;
        Vector3d vector3d = RenderUtil.Render3D.getEntityPosition(target, event2.partialTicks);
        boolean alternate = true;
        for (int iteration = 0; iteration < 1; iteration++) {
            for (float i = 2 * 600; i < 3.800f * 340; i += 1) {
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
                double elapsed = (double) System.currentTimeMillis() % duration;
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
    public static LivingEntity getTarget() {
        return target;
    }


    private void reset() {
        rotate = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
    }
    public enum ListAttack {
        HEAD,
        CHEST,
        LEGS;
    }
    public void onDisable() {
        this.rotate = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        target = null;
        this.cpsLimit = System.currentTimeMillis();
        super.onDisable();
    }
}
