package ru.levinov.modules.impl.util;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.*;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.impl.render.HUD2;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.math.RayTraceUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.render.RenderUtil;

import java.awt.*;

@FunctionAnnotation(name = "Scaffold", type = Type.Movement)
public class Scaffold extends Function {

    private final SliderSetting speedSetting = new SliderSetting("Скорость", 0.17F, 0.1F, 5.0F, 0.01F);
    private final BooleanOption cancelSprintPacket = new BooleanOption("Отменять пакет Sprint", true);
    private final BooleanOption disableSprint = new BooleanOption("Пакет Sprint", true);
    private final BooleanOption disableShift = new BooleanOption("Пакет Shift", true);
    private final BooleanOption disableSneak = new BooleanOption("Пакет Sneak", true);
    private final BooleanOption disableJump = new BooleanOption("Пакет Jump", true);
    private final BooleanOption edgeSneak = new BooleanOption("Приседать на краю", true);
    private final BooleanOption speedBoost = new BooleanOption("Скорость", true);

    private final BooleanOption rots = new BooleanOption("Ротация", true);


    private BlockCache currentBlockCache, previousBlockCache;
    private Vector2f rotation;
    private float savedY;

    private boolean sneaked;
    private TimerUtil timerUtil = new TimerUtil();

    public Scaffold() {
        addSettings(speedSetting, disableSprint, disableShift, disableJump, disableSneak, edgeSneak,rots, speedBoost);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (sneaked) {
            mc.gameSettings.keyBindSneak.setPressed(false);
            sneaked = false;
        }
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (mc.player != null) {
            savedY = (float) mc.player.getPosY();
        }
    }

    @Override
    public void onEvent(Event event) {
        handleSneak(event);
        handleSprint(event);
        handleMovement(event);
        if (rots.get()) {
            handleBlockPlacement(event);
        }
        if (event instanceof EventRender event2) {
            blockInv(event2);
        }
    }

    private void handleSneak(Event event) {
        if (event instanceof EventMotion) {
            if (edgeSneak.get() && mc.player.isOnGround() && isBlockBelowPlayerAir()) {
                mc.gameSettings.keyBindSneak.setPressed(true);
             //   mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
            } else {
                mc.gameSettings.keyBindSneak.setPressed(false);
             //   mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
            }
        }
    }

    private void handleSprint(Event event) {
        if (cancelSprintPacket.get() && event instanceof EventPacket) {
            EventPacket packetEvent = (EventPacket) event;
            if (packetEvent.isSendPacket() && packetEvent.getPacket() instanceof CEntityActionPacket) {
                CEntityActionPacket actionPacket = (CEntityActionPacket) packetEvent.getPacket();
                if (actionPacket.getAction() == CEntityActionPacket.Action.START_SPRINTING || actionPacket.getAction() == CEntityActionPacket.Action.STOP_SPRINTING) {
                    packetEvent.setCancel(true);
                }
            }
        }

        if (event instanceof EventAction) {
            EventAction actionEvent = (EventAction) event;
            if (disableSprint.get()) {
                actionEvent.setSprintState(false);
            }
        }
    }

    private void handleMovement(Event event) {
        if (event instanceof EventInput) {
            EventInput inputEvent = (EventInput) event;
            if (MoveUtil.isMoving()) {
                if (disableJump.get()) {
                    inputEvent.setJump(false);
                }
                if (disableSneak.get()) {
                    inputEvent.setSneak(false);
                }
            }
        }

        if (event instanceof EventMove) {
            EventMove moveEvent = (EventMove) event;
            if (mc.player.isOnGround() && speedBoost.get()) {
                MoveUtil.MoveEvent.setMoveMotion(moveEvent, Math.min(speedSetting.getValue().floatValue(), MoveUtil.getSpeed()));
            }
        }
    }

    private void handleBlockPlacement(Event event) {
        if (event instanceof EventMotion) {
            EventMotion motionEvent = (EventMotion) event;

            // Проверяем, находится ли игрок на земле
            if (mc.player.isOnGround()) {
                savedY = (float) Math.floor(mc.player.getPosY() - 1.0);
            }

            // Получаем информацию о блоке под игроком
            currentBlockCache = getBlockInfo();

            if (currentBlockCache != null) {
                previousBlockCache = currentBlockCache;

                // Проверяем, находится ли игрок над воздухом
                if (isBlockBelowPlayerAir()) {
                    // Обновляем угол поворота игрока, чтобы он всегда наводился на блок
                    updatePlayerRotation(motionEvent);
                } else {
                    // Если игрок на земле, можно также обновить угол поворота на блок
                    // Это может быть полезно, если вы хотите, чтобы игрок всегда смотрел на блок
                    updatePlayerRotation(motionEvent);
                }
            }
        }


        if (event instanceof EventUpdate) {
            if (currentBlockCache == null || previousBlockCache == null) return;

            int blockSlotIndex = getBlockSlotIndex();
            if (blockSlotIndex == -1) {
                ClientUtil.sendMesage("Для использования этой функции у вас должны блоки в хотбаре!");
                toggle();
                return;
            }

            if (rotation != null) {
                RayTraceResult result = RayTraceUtil.rayTrace(3.5f, rotation.x, rotation.y, mc.player);
                if (isBlockBelowPlayerAir() && result.getType() == RayTraceResult.Type.BLOCK) {
                    placeBlock(blockSlotIndex);
                }
            }
        }
    }

    private boolean isBlockBelowPlayerAir() {
        return mc.world.getBlockState(mc.player.getPosition().add(0, -0.5f, 0)).getBlock() instanceof AirBlock;
    }

    private void updatePlayerRotation(EventMotion motionEvent) {
        float[] rot = getRotations(currentBlockCache.position, currentBlockCache.facing);
        rotation = new Vector2f(rot[0], rot[1]);

        mc.player.renderYawOffset = rotation.y;
        mc.player.rotationPitchHead = rotation.x + 180;
    }


    private void placeBlock(int blockSlotIndex) {
        int previousSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = blockSlotIndex;
        BlockRayTraceResult blockRayTraceResult = new BlockRayTraceResult(getVector(previousBlockCache), previousBlockCache.getFacing(), previousBlockCache.getPosition(), false);
        mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, blockRayTraceResult);
        mc.player.swingArm(Hand.MAIN_HAND);
        mc.player.inventory.currentItem = previousSlot;
    }
    final int newhud_color = new Color(8, 9, 13, 127).getRGB();
    private void blockInv(EventRender render) {
        int screenWidth = mc.getMainWindow().width(); // Ширина экрана
        int screenHeight = mc.getMainWindow().height(); // Ширина экрана
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        RenderUtil.Render2D.drawRoundedRect(centerX - 30, centerY + 80, 60, 20, 3, newhud_color);
        int totalCount = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof BlockItem) {
                totalCount += stack.getCount(); // Суммируем количество блоков
            }
        }
        int textWidth = mc.fontRenderer.getStringWidth(String.valueOf(totalCount));
        mc.fontRenderer.drawString(render.matrixStack, String.valueOf(totalCount), centerX - textWidth / 2, centerY + 85, -1);
    }

    private int getBlockSlotIndex() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof BlockItem) {
                return i;
            }
        }
        return -1;
    }

    private float[] getRotations(BlockPos blockPos, Direction facing) {
        double deltaX = (double) blockPos.getX() + 0.5 - mc.player.getPosX() + (double) facing.getXOffset() * 0.25;
        double deltaZ = (double) blockPos.getZ() + 0.5 - mc.player.getPosZ() + (double) facing.getZOffset() * 0.25;
        double deltaY = mc.player.getPosY() + mc.player.getEyeHeight() - blockPos.getY() - (double) facing.getYOffset() * 0.25;
        double distance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) (Math.atan2(deltaZ, deltaX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (Math.atan2(deltaY, distance) * 180.0 / Math.PI);
        return new float[]{MathHelper.wrapDegrees(yaw), pitch};
    }

    public BlockCache getBlockInfo() {
        int y = (int) (mc.player.getPosY() - 1.0 >= savedY && Math.max(mc.player.getPosY(), savedY) - Math.min(mc.player.getPosY(), savedY) <= 3.0 && !mc.gameSettings.keyBindJump.isKeyDown() ? savedY : mc.player.getPosY() - 1.0);

        BlockPos belowBlockPos = new BlockPos(mc.player.getPosX(), y - (mc.player.isSneaking() ? -1 : 0), mc.player.getPosZ());
        if (mc.world.getBlockState(belowBlockPos).getBlock() instanceof AirBlock) {
            return findSolidBlock(belowBlockPos);
        }
        return null;
    }

    private BlockCache findSolidBlock(BlockPos belowBlockPos) {
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                for (int i = -1; i < 4; i++) {
                    BlockPos blockPos = belowBlockPos.add(x * i, 0, z * i);
                    if (mc.world.getBlockState(blockPos).getBlock() instanceof AirBlock) {
                        for (Direction direction : Direction.values()) {
                            BlockPos adjacentBlock = blockPos.offset(direction);
                            Material material = mc.world.getBlockState(adjacentBlock).getBlock().getDefaultState().getMaterial();
                            if (material.isSolid() && !material.isLiquid()) {
                                return new BlockCache(adjacentBlock, direction.getOpposite());
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public Vector3d getVector(BlockCache data) {
        BlockPos pos = data.position;
        Direction face = data.facing;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        switch (face) {
            case UP:
                y += 0.5; // Для UP, поднимаемся выше центра блока
                break;
            case DOWN:
                break;
            case NORTH:
                z -= 0.15; // Сдвигаемся к северу
                break;
            case SOUTH:
                z += 0.15; // Сдвигаемся к югу
                break;
            case WEST:
                x -= 0.15; // Сдвигаемся на запад
                break;
            case EAST:
                x += 0.15; // Сдвигаемся на восток
                break;
        }
        return new Vector3d(x, y, z);
    }
    public static class BlockCache {
        private final BlockPos position;
        private final Direction facing;
        public BlockCache(BlockPos position, Direction facing) {
            this.position = position;
            this.facing = facing;
        }
        public BlockPos getPosition() {
            return position;
        }
        public Direction getFacing() {
            return facing;
        }
    }
    public static class TimedPacket {
        private final IPacket<?> packet;
        private final long time;

        public TimedPacket(IPacket<?> packet, long time) {
            this.packet = packet;
            this.time = time;
        }
        public IPacket<?> getPacket() {
            return packet;
        }

        public long getTime() {
            return time;
        }
    }
}
