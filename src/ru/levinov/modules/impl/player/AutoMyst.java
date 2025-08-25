package ru.levinov.modules.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.render.RenderUtil;

@FunctionAnnotation(name = "AutoMyst", type = Type.Player, desc = "Авто мист для фт")
public class AutoMyst extends Function {

    private final BooleanOption traceplayer = new BooleanOption("Убирать хит-бокс игрока", true);
    private final BooleanOption openwalls = new BooleanOption("Открывать через блоки", true);
    private final BooleanOption nullitems = new BooleanOption("Закрывать если пусто", true);
    private final BooleanOption chestesp = new BooleanOption("Подсветка сундука", true);

    private final SliderSetting delay = new SliderSetting("Задержка клика", 50, 1, 500, 1);

    private long cpsLimit = 0;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean chestFound = false; // Флаг для отслеживания, найден ли сундук

    public AutoMyst() {
        addSettings(traceplayer, openwalls, nullitems, chestesp, delay);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.player.openContainer instanceof ChestContainer) {
                ChestContainer container = (ChestContainer) mc.player.openContainer;
                // Лутание только одного сундука
                lootSingleChest(container);
            } else {
                // Поиск сундуков в радиусе
                if (openwalls.get()) {
                    findAndOpenChest();
                }
            }
        }

        if (chestesp.get() && event instanceof EventRender e) {
            if (e.isRender3D()) {
                for (TileEntity t : mc.world.loadedTileEntityList) {
                    if (t instanceof ChestTileEntity) {
                        RenderUtil.Render3D.drawBlockBox(t.getPos(), -1);
                    }
                    if (t instanceof EnderChestTileEntity) {
                        RenderUtil.Render3D.drawBlockBox(t.getPos(), -1);
                    }
                }
            }
        }
    }

    private void lootSingleChest(ChestContainer container) {
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
                break; // Лутаем только один предмет и выходим
            }
        }

        // Проверяем, если сундук пуст, и закрываем его
        if (nullitems.get() && isChestEmptyExceptForSkippedItems(container)) {
            mc.player.closeScreen();
            chestFound = false;
        }
    }

    private void findAndOpenChest() {
        Vector3d playerPos = mc.player.getPositionVec();
        BlockPos playerBlockPos = new BlockPos(playerPos);
        int radius = 4;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos blockPos = playerBlockPos.add(x, y, z);
                    BlockState blockState = mc.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();

                    // Проверяем, является ли блок сундуком
                    if (block instanceof ChestBlock) {
                        BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(
                                new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5),
                                Direction.UP,
                                blockPos,
                                false
                        );

                        if (cpsLimit > System.currentTimeMillis()) {
                            cpsLimit--;
                        }
                        if (cpsLimit <= System.currentTimeMillis()) {
                            cpsLimit = (long) (System.currentTimeMillis() + delay.getValue().floatValue() + 200);
                             chestFound = false; // Флаг для отслеживания, найден ли сундук
                            // Открываем сундук
                            if (mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, rayTraceResult) == ActionResultType.SUCCESS) {
                                mc.player.swingArm(Hand.MAIN_HAND);
                                chestFound = false; // Устанавливаем флаг, что сундук найден
                                return; // Выходим из метода после открытия сундука

                            }
                        }
                    }
                }
            }
        }

        // Сбрасываем флаг chestFound, если сундук закрыт
        if (mc.player.openContainer == null) {
            chestFound = false; // Сброс флага, чтобы снова искать сундуки
        }
        if (mc.player.openContainer instanceof ChestContainer) {
        } else {
            chestFound = false; // Сброс флага, чтобы снова искать сундуки
        }
    }

    private boolean isChestEmptyExceptForSkippedItems(ChestContainer container) {
        for (int index = 0; index < container.getLowerChestInventory().getSizeInventory(); index++) {
            ItemStack stack = container.getLowerChestInventory().getStackInSlot(index);
            // Если найден предмет, который не должен быть пропущен
            if (!stack.isEmpty() && !isGlassPane(stack)) {
                return false; // Сундук не пуст
            }
        }
        chestFound = false; // Сброс флага, чтобы снова искать сундуки
        return true; // Сундук пуст, если остались только пропускаемые предметы
    }

    private boolean isGlassPane(ItemStack stack) {
        return stack.getItem() == Items.BLUE_STAINED_GLASS_PANE ||
                stack.getItem() == Items.LIGHT_BLUE_STAINED_GLASS_PANE; // Добавьте другие предметы по мере необходимости
    }


    @Override
    public void onDisable() {
        chestFound = false; // Сброс флага, чтобы снова искать сундуки
        super.onDisable();
    }
}
