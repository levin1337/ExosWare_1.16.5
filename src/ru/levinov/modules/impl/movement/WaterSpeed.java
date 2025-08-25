package ru.levinov.modules.impl.movement;

import com.ibm.icu.impl.Utility;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Pose;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.*;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.movement.MoveUtil;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author levin1337
 * @since 22.07.2023
 */
@FunctionAnnotation(
        name = "WaterSpeed",
        type = Type.Movement,
        desc = "Ускорение на воде"
)
public class WaterSpeed extends Function {
    private final ModeSetting mode = new ModeSetting("Мод", "Обычный", new String[]{"Обычный", "MiniJump", "FunTime"});
    public SliderSetting Jumping = (new SliderSetting("Скорость для мода MiniJump", 0.41F, 0.1F, 5.5F, 0.01F)).setVisible(() -> {
        return this.mode.is("MiniJump");
    });
    public SliderSetting speed = (new SliderSetting("Скорость", 0.41F, 0.1F, 0.5F, 0.01F)).setVisible(() -> {
        return this.mode.is("Обычный");
    });
    public SliderSetting motionY = (new SliderSetting("Скорость по Y", 0.0F, 0.0F, 0.1F, 0.01F)).setVisible(() -> {
        return this.mode.is("Обычный");
    });
    private float currentValue;
    private float waterTicks = 0.0F;

    public WaterSpeed() {
        this.addSettings(new Setting[]{this.speed, this.motionY, this.mode, this.Jumping});
    }

    public void onEvent(Event event) {
        if (this.mode.is("Обычный") && event instanceof EventTravel move) {
            if (mc.player.collidedVertically || mc.player.collidedHorizontally) {
                return;
            }

            if (mc.player.isSwimming()) {
                float speed = this.speed.getValue().floatValue() / 10.0F;
                Vector3d var10000;
                if (mc.gameSettings.keyBindJump.pressed) {
                    var10000 = mc.player.motion;
                    var10000.y += (double)this.motionY.getValue().floatValue();
                }

                if (mc.gameSettings.keyBindSneak.pressed) {
                    var10000 = mc.player.motion;
                    var10000.y -= (double)this.motionY.getValue().floatValue();
                }

                MoveUtil.setMotion((double)MoveUtil.getMotion());
                move.speed += speed;
            }
        }

        if (this.mode.is("MiniJump")) {
            BlockPos waterBlockPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 0.2, mc.player.getPosZ());
            Block waterBlock = mc.world.getBlockState(waterBlockPos).getBlock();
            if (waterBlock == Blocks.WATER) {
                MoveUtil.setMotion((double)this.Jumping.getValue().floatValue());
            }

            if (mc.player.isInWater() || mc.player.isInLava()) {
                if (mc.gameSettings.keyBindJump.pressed) {
                    mc.player.motion.y = 0.1;
                }

                if (mc.gameSettings.keyBindSneak.pressed) {
                    mc.player.motion.y = -0.3;
                }
            }

            if (mc.player.isSwimming() && mc.gameSettings.keyBindJump.pressed) {
                MoveUtil.setMotion((double)this.Jumping.getValue().floatValue());
                mc.player.motion.y = 0.3;
            }
        }

        if (this.mode.is("FunTime") && event instanceof EventUpdate && mc.player.isSwimming()) {
            float speed = 1F;
            mc.player.setMotion(mc.player.getMotion().x * (double)speed, mc.player.getMotion().y, mc.player.getMotion().z * (double)speed);
        }

    }

    public float calculateNewValue(float value, float increment) {
        return value * Math.min((this.currentValue += increment) / 100.0F, 1.0F);
    }
}

