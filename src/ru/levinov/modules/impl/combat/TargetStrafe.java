package ru.levinov.modules.impl.combat;


import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventInput;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.movement.MoveUtil;

import java.util.List;
@SuppressWarnings("all")
@FunctionAnnotation(
        name = "TargetStrafe",
        type = Type.Combat,
        desc = "Стрейфы вокруг текущего таргета"
)
public class TargetStrafe extends Function {

    public ModeSetting mode = new ModeSetting("Мод", "Grim", "Grim", "Default");


    private final SliderSetting range = new SliderSetting("Дистанция", 1.0F, 0.001F, 5.0F, 0.001F);
    private final SliderSetting speed = new SliderSetting("Скорость", 1.0F, 0.001F, 5.0F, 0.001F);
    private final SliderSetting hx = new SliderSetting("Размер", 0.002F, 0.001F, 1.0F, 0.001F);

    private boolean switchDir = true;
    public final BooleanOption autojump = new BooleanOption("Авто прыжок", true);

    public TargetStrafe() {
        this.addSettings(mode,this.range, this.speed,hx,autojump);
    }

    public void onEvent(Event event) {
        if (mode.is("Default")) {
            if (event instanceof EventMotion e) {
                LivingEntity entity = Aura.target;
                if (entity != null && entity.isAlive()) {
                    float auraRange = Managment.FUNCTION_MANAGER.auraFunction.distance.getValue().floatValue() + Managment.FUNCTION_MANAGER.auraFunction.rotateDistance.getValue().floatValue();
                    float distanceToEntity = mc.player.getDistance(entity);
                    if (!(distanceToEntity > auraRange)) {
                        float speed = this.speed.getValue().floatValue();
                        double var13 = mc.player.getPosZ() - entity.getPosZ();
                        double wrap = Math.atan2(var13, mc.player.getPosX() - entity.getPosX());
                        float additionalWrap = (float) MathHelper.clamp((double) speed / MathHelper.clamp((double) distanceToEntity, 0.01, (double) auraRange), 0.009999999776482582, 1.0);
                        wrap += this.switchDir ? (double) additionalWrap : (double) (-additionalWrap);
                        double x = entity.getPosX() + (double) this.range.getValue().floatValue() * Math.cos(wrap);
                        double z = entity.getPosZ() + (double) this.range.getValue().floatValue() * Math.sin(wrap);
                        if (this.switchCheck(x, z)) {
                            this.switchDir = !this.switchDir;
                            wrap += (double) (2.0F * (this.switchDir ? additionalWrap : -additionalWrap));
                            x = entity.getPosX() + (double) this.range.getValue().floatValue() * Math.cos(wrap);
                            z = entity.getPosZ() + (double) this.range.getValue().floatValue() * Math.sin(wrap);
                        }
                        mc.player.motion.x = (double) speed * -Math.sin(Math.toRadians(this.wrapToDegrees(x, z)));
                        mc.player.motion.z = (double) speed * Math.cos(Math.toRadians(this.wrapToDegrees(x, z)));
                    }
                }
            }
        }
        if (event instanceof EventMotion e) {
            if (mode.is("Grim")) {

                AxisAlignedBB aabb = mc.player.getBoundingBox().grow(hx.getValue().floatValue());
                List<ArmorStandEntity> armorStandEntities = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb);
                List<LivingEntity> livingEntities = mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb);
                int armorStandsCount = armorStandEntities.size();
                int signItemsCount = armorStandEntities.size();
                boolean canBoost = signItemsCount > 1 ||  armorStandsCount > 1 || livingEntities.size() > 1;
                if (canBoost && !mc.player.isOnGround()) {
                    mc.player.jumpMovementFactor = armorStandsCount > 1 ? 1.0f / (float) armorStandsCount : speed.getValue().floatValue();
                }
            }
        }
        if (event instanceof EventUpdate e) {
            if (mode.is("Grim")) {
                // Проверяем, есть ли цель
                if (Aura.target != null) {
                    mc.gameSettings.keyBindForward.setPressed(true);
                    if (autojump.get()) {
                        mc.gameSettings.keyBindJump.setPressed(true);
                    }
                } else {
                    // Если цель пропала, сбрасываем состояние клавиш
                    mc.gameSettings.keyBindForward.setPressed(false);
                    if (autojump.get()) {
                        mc.gameSettings.keyBindJump.setPressed(false); // Сбрасываем прыжок
                    }
                }
            }
        }



    }

    private boolean checkAir() {
        for(int i = (int)mc.player.getPosY(); i > 0; --i) {
            double nadal = (double)i;
            if (!(mc.world.getBlockState(new BlockPos(mc.player.getPosX(), nadal, mc.player.getPosZ())).getBlock() instanceof AirBlock)) {
                return false;
            }
        }

        return true;
    }

    public boolean switchCheck(double x, double z) {
        if (!mc.player.collidedHorizontally && !mc.gameSettings.keyBindLeft.isPressed() && !mc.gameSettings.keyBindRight.isPressed()) {
            for(int i = (int)(mc.player.getPosY() + 4.0); i >= 0; --i) {
                BlockPos blockPos = new BlockPos(x, (double)i, z);
                if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.FIRE)) {
                    return true;
                }

                if (mc.world.getBlockState(blockPos).getBlock() == Blocks.COBWEB) {
                    return true;
                }

                if (this.checkAir()) {
                    return true;
                }

                if (!mc.world.isAirBlock(blockPos)) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    private double wrapToDegrees(double x, double z) {
        double diffX = x - mc.player.getPosX();
        double diffZ = z - mc.player.getPosZ();
        return Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0;
    }
    public void onDisable() {
        super.onEnable();
    }
}
