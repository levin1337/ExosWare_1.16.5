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

@FunctionAnnotation(name = "AutoMyst", type = Type.Player, desc = "���� ���� ��� ��")
public class AutoMyst extends Function {

    private final BooleanOption traceplayer = new BooleanOption("������� ���-���� ������", true);
    private final BooleanOption openwalls = new BooleanOption("��������� ����� �����", true);
    private final BooleanOption nullitems = new BooleanOption("��������� ���� �����", true);
    private final BooleanOption chestesp = new BooleanOption("��������� �������", true);

    private final SliderSetting delay = new SliderSetting("�������� �����", 50, 1, 500, 1);

    private long cpsLimit = 0;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean chestFound = false; // ���� ��� ������������, ������ �� ������

    public AutoMyst() {
        addSettings(traceplayer, openwalls, nullitems, chestesp, delay);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.player.openContainer instanceof ChestContainer) {
                ChestContainer container = (ChestContainer) mc.player.openContainer;
                // ������� ������ ������ �������
                lootSingleChest(container);
            } else {
                // ����� �������� � �������
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

            // ���������, �������� �� ������� ���������� �������
            if (isGlassPane(stack)) {
                continue; // ���������� ���� ����
            }

            // ���� ���� �� ������ � ������ �����, ��������� ��������
            if (!stack.isEmpty() && timerUtil.hasTimeElapsed(30)) {
                mc.playerController.windowClick(container.windowId, index, 0, ClickType.QUICK_MOVE, mc.player);
                timerUtil.reset();
                break; // ������ ������ ���� ������� � �������
            }
        }

        // ���������, ���� ������ ����, � ��������� ���
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

                    // ���������, �������� �� ���� ��������
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
                             chestFound = false; // ���� ��� ������������, ������ �� ������
                            // ��������� ������
                            if (mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, rayTraceResult) == ActionResultType.SUCCESS) {
                                mc.player.swingArm(Hand.MAIN_HAND);
                                chestFound = false; // ������������� ����, ��� ������ ������
                                return; // ������� �� ������ ����� �������� �������

                            }
                        }
                    }
                }
            }
        }

        // ���������� ���� chestFound, ���� ������ ������
        if (mc.player.openContainer == null) {
            chestFound = false; // ����� �����, ����� ����� ������ �������
        }
        if (mc.player.openContainer instanceof ChestContainer) {
        } else {
            chestFound = false; // ����� �����, ����� ����� ������ �������
        }
    }

    private boolean isChestEmptyExceptForSkippedItems(ChestContainer container) {
        for (int index = 0; index < container.getLowerChestInventory().getSizeInventory(); index++) {
            ItemStack stack = container.getLowerChestInventory().getStackInSlot(index);
            // ���� ������ �������, ������� �� ������ ���� ��������
            if (!stack.isEmpty() && !isGlassPane(stack)) {
                return false; // ������ �� ����
            }
        }
        chestFound = false; // ����� �����, ����� ����� ������ �������
        return true; // ������ ����, ���� �������� ������ ������������ ��������
    }

    private boolean isGlassPane(ItemStack stack) {
        return stack.getItem() == Items.BLUE_STAINED_GLASS_PANE ||
                stack.getItem() == Items.LIGHT_BLUE_STAINED_GLASS_PANE; // �������� ������ �������� �� ���� �������������
    }


    @Override
    public void onDisable() {
        chestFound = false; // ����� �����, ����� ����� ������ �������
        super.onDisable();
    }
}
