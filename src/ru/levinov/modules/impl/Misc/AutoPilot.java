package ru.levinov.modules.impl.Misc;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.math.AuraUtil;
import ru.levinov.util.math.GCDUtil;

import java.util.concurrent.ThreadLocalRandom;

import static ru.levinov.util.math.MathUtil.calculateDelta;

@FunctionAnnotation(name = "AutoPilot", type = Type.Misc, desc = "Наведение головы на ресурсы")
public class AutoPilot extends Function {
    public final ModeSetting mode = new ModeSetting("Мод", "ReallyWorld","ReallyWorld", "FunTime");


    public final MultiBoxSetting elements = new MultiBoxSetting("Идти на",
            new BooleanOption("Шары", true),
            new BooleanOption("Элитры", true)
    );

    private final SliderSetting radius = new SliderSetting("Дистанция", 10F, 3F, 30.0F, 1F); // Радиус наведения

    public float yaw;
    public float pith;
    public AutoPilot() {
        addSettings(mode,elements, radius);
    }

    public void onEvent(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            if (mode.is("ReallyWorld")) {
                if (mc.player != null) {
                    yaw = mc.player.rotationYawHead; // Инициализируем yaw здесь
                    for (Entity entity : mc.world.getAllEntities()) {
                        if (entity instanceof ItemEntity) {
                            ItemStack itemStack = ((ItemEntity) entity).getItem();
                            // Проверка на предметы
                            if ((itemStack.getItem() instanceof SkullItem && elements.get(0)) ||
                                    (itemStack.getItem() instanceof ElytraItem && elements.get(1))) {

                                // Позиция
                                BlockPos headPos = new BlockPos(entity.getPosX(), entity.getPosY() + entity.getHeight(), entity.getPosZ());
                                // Головка на земле
                                if (mc.world.getBlockState(headPos.down()).getBlock() != Blocks.AIR ||
                                        mc.world.getBlockState(headPos.down()).getBlock() == Blocks.GRASS_BLOCK ||
                                        mc.world.getBlockState(headPos.down()).getBlock() == Blocks.DIRT || // Земля
                                        mc.world.getBlockState(headPos.down()).getBlock() == Blocks.STONE ||
                                        mc.world.getBlockState(headPos.down()).getBlock() == Blocks.SAND || // Песок
                                        mc.world.getBlockState(headPos.down()).getBlock() == Blocks.GRAVEL // Гравий
                                ) {
                                    // Проверка радиуса
                                    if (mc.player.getDistance(entity) <= radius.getValue().floatValue()) {
                                        yaw = rotations(entity)[0];
                                        mc.player.rotationPitchHead = rotations(entity)[1];
                                        // Выходим из цикла, если голову подняли
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (mode.is("FunTime")) {
                if (mc.player != null) {
                    yaw = mc.player.rotationYaw; // Инициализируем yaw здесь
                    pith = mc.player.rotationPitch; // Инициализируем yaw здесь
                    for (Entity entity : mc.world.getAllEntities()) {
                        if (entity instanceof ItemEntity) {
                            ItemStack itemStack = ((ItemEntity) entity).getItem();
                            // Проверка на предметы
                            if ((itemStack.getItem() instanceof SkullItem && elements.get(0)) || (itemStack.getItem() instanceof ElytraItem && elements.get(1))) {

                                // Позиция предмета
                                double itemX = entity.getPosX();
                                double itemY = entity.getPosY() + entity.getHeight() / 2; // Центр предмета
                                double itemZ = entity.getPosZ();

                                // Проверка, что предмет находится в воздухе (не на земле)
                                BlockPos itemPos = new BlockPos(itemX, itemY, itemZ);
                                if (mc.world.getBlockState(itemPos.down()).getBlock() == Blocks.AIR ||
                                        mc.world.getBlockState(itemPos.down()).getBlock() == Blocks.GRASS_BLOCK ||
                                        mc.world.getBlockState(itemPos.down()).getBlock() == Blocks.DIRT || // Земля
                                        mc.world.getBlockState(itemPos.down()).getBlock() == Blocks.STONE ||
                                        mc.world.getBlockState(itemPos.down()).getBlock() == Blocks.SAND || // Песок
                                        mc.world.getBlockState(itemPos.down()).getBlock() == Blocks.GRAVEL) {
                                    // Проверка радиуса
                                    if (mc.player.getDistance(entity) <= radius.getValue().floatValue()) {
                                        // Получаем углы для поворота к предмету
                                        float[] rotations = rotations(entity);
                                        yaw = rotations[0];
                                        pith = rotations[1];
                                        // Выходим из цикла, если голову подняли
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public float[] rotations(Entity entity) {
        double x = entity.getPosX() - mc.player.getPosX();
        double y = entity.getPosY() - mc.player.getPosY() - 1.5;
        double z = entity.getPosZ() - mc.player.getPosZ();

        double u = Math.sqrt(x * x + z * z);
        float u2 = (float) (Math.atan2(z, x) * 57.29577951308232 - 90.0);
        float u3 = (float) (-Math.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};
    }
}
