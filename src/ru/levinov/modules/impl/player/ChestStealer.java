package ru.levinov.modules.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.misc.TimerUtil;

/**
 * @author levin1337
 * @since 27.07.2023
 */

@FunctionAnnotation(name = "ChestStealer", type = Type.Player,desc = "Забирает ресурсы из сундука")
public class ChestStealer extends Function {

    private final ModeSetting mode = new ModeSetting("Мод", "Топ-Ресы", "Обычный", "Топ-Ресы");

    private final BooleanOption nullitem = new BooleanOption("Закрывать если пусто", true);
    private final BooleanOption openwalls = new BooleanOption("Открывать через блоки", true);
    private final BooleanOption rotations = new BooleanOption("Ротация на сундук", true);
    private final SliderSetting stealDelay = new SliderSetting("Задержка", 100, 0, 1000, 1);
    private final TimerUtil timerUtil = new TimerUtil();


    public ChestStealer() {
        addSettings(mode, stealDelay, nullitem, openwalls,rotations);
    }
    private long cpsLimit = 0;
    @Override
    public void onEvent(final Event event) {
        if (mode.is("Обычный")) {
            if (event instanceof EventUpdate) {
                if (mc.player.openContainer instanceof ChestContainer) {
                    ChestContainer container = (ChestContainer) mc.player.openContainer;
                    for (int index = 0; index < container.inventorySlots.size(); ++index) {
                        if (container.getLowerChestInventory().getStackInSlot(index).getItem() != Item.getItemById(0) && timerUtil.hasTimeElapsed(stealDelay.getValue().longValue())) {
                            mc.playerController.windowClick(container.windowId, index, 0, ClickType.QUICK_MOVE, mc.player);
                            timerUtil.reset();
                            continue;
                        }
                        if (nullitem.get()) {
                            if (container.getLowerChestInventory().isEmpty()) {
                                mc.player.closeScreen();
                            }
                        }
                    }
                }
            }
        }
        if (mode.is("Топ-Ресы")) {
            if (event instanceof EventUpdate) {
                if (mc.player.openContainer instanceof ChestContainer) {
                    ChestContainer container = (ChestContainer) mc.player.openContainer;
                    for (int index = 0; index < container.inventorySlots.size(); ++index) {
                        ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(index);

                        // Проверяем, что предмет не пустой и является "топ-ресурсом"
                        if (!itemStack.isEmpty() && timerUtil.hasTimeElapsed(stealDelay.getValue().longValue())) {
                            if (isTopResource(itemStack)) {
                                mc.playerController.windowClick(container.windowId, index, 0, ClickType.QUICK_MOVE, mc.player);
                                timerUtil.reset();
                            }
                        }
                        if (nullitem.get()) {
                            if (container.getLowerChestInventory().isEmpty()) {
                                mc.player.closeScreen();
                            }
                        }
                    }
                }
            }
        }
        if (mode.is("Новогодний Экспресс")) {
            if (event instanceof EventUpdate) {
                if (mc.player.openContainer instanceof ChestContainer) {
                    ChestContainer container = (ChestContainer) mc.player.openContainer;
                    for (int index = 0; index < container.inventorySlots.size(); ++index) {
                        ItemStack stack = container.getLowerChestInventory().getStackInSlot(index);

                        // Проверяем, является ли предмет стеклянной панелью
                        if (isGlassPane(stack)) {
                            continue; // Пропускаем этот слот
                        }

                        // Если слот не пустой и таймер истек, выполняем действие
                        if (!stack.isEmpty() && timerUtil.hasTimeElapsed(30)) {
                            mc.playerController.windowClick(container.windowId, index, 0, ClickType.QUICK_MOVE, mc.player);
                            timerUtil.reset();
                            break;
                        }
                    }

                    // Проверяем, если сундук пуст, и закрываем его
                    if (nullitem.get()) {
                        if (isChestEmptyExceptForSkippedItems(container)) {
                            mc.player.closeScreen();

                            // IMinecraft.mc.displayGuiScreen(null);
                        } else {

                        }
                    }
                } else {

                }
            }
        }


        if (openwalls.get()) {
            if (event instanceof EventUpdate) {
                if (mc.player.openContainer instanceof ChestContainer) {

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
                                    float[] rotation = rots222(new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5));
                                    if (rotations.get()) {
                                        mc.player.rotationYawHead = rotation[0];
                                        mc.player.renderYawOffset = rotation[1];
                                        mc.player.rotationPitchHead = rotation[1];
                                    }

                                    // Устанавливаем блок (если нужно)
                                    BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(
                                            new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5),
                                            Direction.UP,
                                            blockPos,
                                            false
                                    );

                                    if (cpsLimit > System.currentTimeMillis()) {
                                        cpsLimit--;
                                    }
                                    if ((cpsLimit <= System.currentTimeMillis())) {
                                        cpsLimit = System.currentTimeMillis() + 600;
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
    }

// Метод для проверки, является ли предмет "топ-ресурсом"
    private boolean isChestEmptyExceptForSkippedItems(ChestContainer container) {
        for (int index = 0; index < container.getLowerChestInventory().getSizeInventory(); index++) {
            ItemStack stack = container.getLowerChestInventory().getStackInSlot(index);
            // Если найден предмет, который не должен быть пропущен
            if (!stack.isEmpty() && !isGlassPane(stack)) {
                return false; // Сундук не пуст
            }
        }
        return true; // Сундук пуст, если остались только пропускаемые предметы
    }

    private boolean isGlassPane(ItemStack stack) {
        return stack.getItem() == Items.BLUE_STAINED_GLASS_PANE ||
                stack.getItem() == Items.LIGHT_BLUE_STAINED_GLASS_PANE; // Добавьте другие предметы по мере необходимости
    }
    public static float[] rots222(Vector3d vec) {
        double x = vec.x - mc.player.getPosX();
        double y = vec.y - (mc.player.getPosY() + 2);
        double z = vec.z - mc.player.getPosZ();
        double u = (double) MathHelper.sqrt(x * x + z * z);
        float u2 = (float)(MathHelper.atan2(z, x) * 57.29577951308232 - 90.0);
        float u3 = (float)(-MathHelper.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};
    }
    private boolean isTopResource(ItemStack itemStack) {
        // Проверяем, является ли предмет головой игрока или тотемом
        return itemStack.getItem() instanceof SkullItem || itemStack.getItem() == Items.TOTEM_OF_UNDYING || itemStack.getItem() == Items.ELYTRA || itemStack.getItem() == Items.FIREWORK_ROCKET
                || itemStack.getItem() == Items.GOLDEN_APPLE
                || itemStack.getItem() == Items.FEATHER
                || itemStack.getItem() == Items.POPPED_CHORUS_FRUIT
                || itemStack.getItem() == Items.ENCHANTED_GOLDEN_APPLE
                || itemStack.getItem() == Items.TRIPWIRE_HOOK
                || itemStack.getItem() == Items.ARROW
                || itemStack.getItem() == Items.ENCHANTED_BOOK;
    }
}
