package ru.levinov.modules.impl.combat;

import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;

@FunctionAnnotation(name = "AttackMouse", type = Type.Combat
,desc = "Альтернатива Триггер-Бота")
public class AttackMouse extends Function {


    private final BooleanOption onlyCritical = new BooleanOption("Только криты", true);
    private final BooleanOption onlySpaceCritical = new BooleanOption("Только с пробелом", false).setVisible(onlyCritical::get);

    public AttackMouse() {
        addSettings(onlyCritical, onlySpaceCritical);
    }

    private long cpsLimit = 0;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate e) {
            if (cpsLimit > System.currentTimeMillis()) {
                cpsLimit--;
            }

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

    // Проверка, падает ли игрок, и условия для отмены критических ударов
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
