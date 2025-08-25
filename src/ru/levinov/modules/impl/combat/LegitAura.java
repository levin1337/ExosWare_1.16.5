package ru.levinov.modules.impl.combat;


import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;

import java.util.Comparator;

import static ru.levinov.util.render.RenderUtil.Render2D.drawCircle;

@FunctionAnnotation(name = "LegitAura", type = Type.Combat)
public class LegitAura extends Function {
    private final SliderSetting range = new SliderSetting("Дистанция", 5.0f, 2.0f, 15.0f, 0.05f);
    private final SliderSetting smoothFactor = new SliderSetting("Плавность", 5.0f, 1.0f, 15.0f, 0.1f);
    private final SliderSetting attackRadius = new SliderSetting("Радиус атаки", 3.0f, 1.0f, 10.0f, 0.1f);
    private final SliderSetting circleRadius = new SliderSetting("Радиус круга", 3.0f, 1.0f, 100f, 1f);

    private final BooleanOption onlyCritical = new BooleanOption("Умные криты", true);
    private final BooleanOption onlySpaceCritical = new BooleanOption("Только с пробелом", false)
            .setVisible(onlyCritical::get);

    private Minecraft mc;

    public LegitAura() {
        addSettings(range, smoothFactor, attackRadius, circleRadius,onlyCritical,onlySpaceCritical);
        mc = Minecraft.getInstance();
    }
    private long cpsLimit = 0;
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            PlayerEntity target = getClosestTarget();
            if (target != null && mc.player.canEntityBeSeen(target)) {
                // Плавная наводка

                float[] targetRotations = rotations(target);
                mc.player.rotationYaw = smoothRotation(mc.player.rotationYaw, targetRotations[0], smoothFactor.getValue().intValue());
                mc.player.rotationPitch = smoothRotation(mc.player.rotationPitch, targetRotations[1], smoothFactor.getValue().intValue());

                // Автоудар, если игрок находится в радиусе атаки
// Проверка на то, что игрок наводится на цель
                if (cpsLimit > System.currentTimeMillis()) {
                    cpsLimit--;
                }
                if (mc.player.isElytraFlying()) {
                    if (whenFalling() && (cpsLimit <= System.currentTimeMillis())) {
                        cpsLimit = System.currentTimeMillis() + 550;
                        mc.playerController.attackEntity(mc.player, target);
                        mc.player.swingArm(Hand.MAIN_HAND);
                    }
                } else {
                    if (mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                        if (whenFalling() && (cpsLimit <= System.currentTimeMillis())) {
                            cpsLimit = System.currentTimeMillis() + 550;
                            if (mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                                mc.playerController.attackEntity(mc.player, ((EntityRayTraceResult) mc.objectMouseOver).getEntity());
                                mc.player.swingArm(Hand.MAIN_HAND);
                            }
                        }
                    }
                }
            }
        }
        if (event instanceof EventRender render) {
            if (mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
                return;
            }
            final MainWindow mainWindow = mc.getMainWindow();
            final float x = (float) mainWindow.scaledWidth() / 2.0F;
            final float y = (float) mainWindow.scaledHeight() / 2.0F;

            drawCircle(x, y, 0, 360, circleRadius.getValue().floatValue(), 2f, false, -1);
        }
    }

    private PlayerEntity getClosestTarget() {
        return mc.world.getPlayers().stream()
                .filter(entityPlayer -> entityPlayer != mc.player).filter(entityPlayer -> entityPlayer.getDistance(mc.player) <= range.getValue().floatValue()).min(Comparator.comparing(entityPlayer -> entityPlayer.getDistance(mc.player)))
                .orElse(null);
    }

    private float smoothRotation(float current, float target, float factor) {
        float difference = MathHelper.wrapDegrees(target - current);
        return current + difference / factor; // Линейная интерполяция
    }

    public float[] rotations(LivingEntity entity) {
        double x = entity.getPosX() - mc.player.getPosX();
        double y = entity.getPosY() - (mc.player.getPosY() + mc.player.getEyeHeight()) + 1.5;
        double z = entity.getPosZ() - mc.player.getPosZ();

        double u = MathHelper.sqrt(x * x + z * z);

        float yaw = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float pitch = (float) (-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[]{yaw, pitch};
    }


    public boolean whenFalling() {
        boolean critWater = mc.player.areEyesInFluid(FluidTags.WATER);

        final boolean reasonForCancelCritical = mc.player.isPotionActive(Effects.BLINDNESS)
                || mc.player.isOnLadder()
                || mc.player.isInWater() && critWater
                || mc.player.isRidingHorse()
                || mc.player.abilities.isFlying
                || mc.player.isElytraFlying();

        final boolean onSpace = onlySpaceCritical.get()
                && mc.player.isOnGround()
                && !mc.gameSettings.keyBindJump.isKeyDown();

        if (mc.player.getCooledAttackStrength(1.5F) < 0.92F)
            return false;
        if (!reasonForCancelCritical && onlyCritical.get()) {
            return onSpace || !mc.player.isOnGround() && mc.player.fallDistance > 0.0F;
        }

        return true;
    }
}