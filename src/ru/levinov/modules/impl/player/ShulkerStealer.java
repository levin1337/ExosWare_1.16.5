package ru.levinov.modules.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.misc.TimerUtil;

/**
 * @author levin1337
 * @since 27.07.2023
 */

@FunctionAnnotation(name = "ShulkerStealer", type = Type.Player, desc = "Забирает ресурсы из шалкера")
public class ShulkerStealer extends Function {
    private final BooleanOption openwalls = new BooleanOption("Открывать через блоки", true);

    private final SliderSetting stealDelay = new SliderSetting("Задержка", 500, 0, 1000, 1);
    private final TimerUtil timerUtil = new TimerUtil();

    public ShulkerStealer() {
        addSettings(stealDelay,openwalls);
    }

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventUpdate) {
            if (mc.player.openContainer instanceof ShulkerBoxContainer) {
                ShulkerBoxContainer container = (ShulkerBoxContainer) mc.player.openContainer;
                NonNullList<ItemStack> items = container.getInventory();

                for (int index = 0; index < items.size(); ++index) {
                    ItemStack itemStack = items.get(index);
                    if (!itemStack.isEmpty() && timerUtil.hasTimeElapsed(stealDelay.getValue().longValue())) {
                        mc.playerController.windowClick(container.windowId, index, 0, ClickType.QUICK_MOVE, mc.player);
                        timerUtil.reset();
                    }
                }
            }
        }
        if (openwalls.get()) {
            if (event instanceof EventUpdate) {
                if (mc.player.openContainer instanceof ShulkerBoxContainer) {

                } else {
                    // Получаем позицию игрока
                    Vector3d playerPos = mc.player.getPositionVec();
                    BlockPos playerBlockPos = new BlockPos(playerPos);

                    // Определяем радиус проверки
                    int radius = 4;

                    // Перебираем блоки в радиусе
                    for (int x = -radius; x <= radius; x++) {
                        for (int y = -radius; y <= radius; y++) {
                            for (int z = -radius; z <= radius; z++) {
                                BlockPos blockPos = playerBlockPos.add(x, y, z);
                                BlockState blockState = mc.world.getBlockState(blockPos);
                                Block block = blockState.getBlock();

                                // Проверяем, является ли блок сундуком
                                if (block instanceof ChestBlock) {
                                    // Здесь можно добавить проверку на открытость сундука
                                    // Например, проверяя наличие других блоков рядом или другие условия

                                    // Если у вас есть способ проверить, открыт ли сундук, используйте его
                                    // Получаем поворот игрока
                                    float[] rotation = rots(new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5));
                                    mc.player.rotationYawHead = rotation[0];
                                    mc.player.renderYawOffset = rotation[1];
                                    mc.player.rotationPitchHead = rotation[1];

                                    // Устанавливаем блок (если нужно)
                                    BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(
                                            new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5),
                                            Direction.UP,
                                            blockPos,
                                            false
                                    );

                                    if (mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, rayTraceResult) == ActionResultType.SUCCESS) {
                                        mc.player.swingArm(Hand.MAIN_HAND);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static float[] rots(Vector3d vec) {
        double x = vec.x - mc.player.getPosX();
        double y = vec.y - (mc.player.getPosY() + 2);
        double z = vec.z - mc.player.getPosZ();
        double u = (double) MathHelper.sqrt(x * x + z * z);
        float u2 = (float)(MathHelper.atan2(z, x) * 57.29577951308232 - 90.0);
        float u3 = (float)(-MathHelper.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};
    }
}
