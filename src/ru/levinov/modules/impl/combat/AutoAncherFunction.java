package ru.levinov.modules.impl.combat;

import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventPlaceAnchorByPlayer;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.math.MathUtil;
import ru.levinov.util.math.RayTraceUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.world.InventoryUtil;

import static net.minecraft.block.RespawnAnchorBlock.CHARGES;

/**
 * @author levin1337
 * @since 06.06.2023
 */
@FunctionAnnotation(name = "AutoAnchor", type = Type.Combat, desc = "�������������� �������� ����������")
public class AutoAncherFunction extends Function {
    private int oldSlot = -1;
    private BlockPos position = null;
    private final TimerUtil t = new TimerUtil();

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventPlaceAnchorByPlayer e) {
            handleEventPlaceAnchorByPlayer(e);
        } else if (event instanceof EventMotion e) {
            handleEventMotion(e);
        }
    }

    /**
     * ������������ ������� ���� EventPlaceAnchorByPlayer.
     */
    private void handleEventPlaceAnchorByPlayer(final EventPlaceAnchorByPlayer e) {
        position = e.getPos();
    }

    /**
     * ������������ ������� ���� EventMotion.
     *
     * @author  levin1337
     */
    private void handleEventMotion(final EventMotion e) {
        if (position == null) {
            return;
        }

        if (mc.player.getPositionVec().distanceTo(
                new Vector3d(position.getX(), position.getY(), position.getZ()))
                > mc.playerController.getBlockReachDistance()) {
            return;
        }

        if (oldSlot == -1) {
            oldSlot = mc.player.inventory.currentItem;
        }

        final int slot = InventoryUtil.getSlotInHotBar(Items.GLOWSTONE);
        if (slot != -1 && !mc.player.isSneaking()) {
            mc.player.inventory.currentItem = slot;


            double x = position.getX() + 0.5f;
            double y = position.getY() - 1;
            double z = position.getZ() + 0.5f;

            Vector2f rots = MathUtil.rotationToVec(new Vector3d(x, y, z));
            e.setYaw(rots.x);
            e.setPitch(rots.y);
            mc.player.rotationYawHead = rots.x;
            mc.player.renderYawOffset = rots.x;
            mc.player.rotationPitchHead = rots.y;

            BlockState state = mc.world.getBlockState(position);
            if (!(state.getBlock() instanceof RespawnAnchorBlock) || (state.getBlock() instanceof RespawnAnchorBlock && state.get(CHARGES) >= 1)) {
                resetOnFull();
            }

            if (!(state.getBlock() instanceof RespawnAnchorBlock) || (state.getBlock() instanceof RespawnAnchorBlock && state.get(CHARGES) >= 2)) {
                position = null;
            }

            if (position != null && mc.player.getPositionVec().distanceTo(
                    new Vector3d(position.getX(),
                            position.getY(),
                            position.getZ())) <= mc.playerController.getBlockReachDistance()) {
                setFuelToAncher(rots);
            }

        }
    }

    /**
     * ������������ ��������, ����� ����� �������� ���������. ���������� ������� � ������ ���� ��������� ������
     * � ���������� ����������� ������� �����.
     */
    private void resetOnFull() {
        mc.player.inventory.currentItem = oldSlot;
        oldSlot = -1;
    }

    /**
     * ������������� ������� ��� �����, �������� ������ ���� �� �����
     */
    private void setFuelToAncher(Vector2f rots) {
        if (t.hasTimeElapsed(150)) {
            ActionResultType result = mc.playerController.processRightClickBlock(mc.player,
                    mc.world,
                    Hand.MAIN_HAND,
                    (BlockRayTraceResult) RayTraceUtil.rayTrace(6, rots.x, rots.y, mc.player));
            if (result == ActionResultType.SUCCESS) {
                mc.player.swingArm(Hand.MAIN_HAND);
            }
            t.reset();
        }
    }

    @Override
    protected void onDisable() {
        oldSlot = -1;
        super.onDisable();
    }


}
