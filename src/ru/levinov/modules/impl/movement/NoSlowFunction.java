package ru.levinov.modules.impl.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.*;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.impl.combat.Aura;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.DamageUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static net.minecraft.network.play.client.CPlayerDiggingPacket.Action.RELEASE_USE_ITEM;
import static net.minecraft.network.play.client.CPlayerDiggingPacket.Action.SWAP_ITEM_WITH_OFFHAND;

/**
 * @author levin1337
 * @since 04.06.2023
 */
@FunctionAnnotation(name = "NoSlowDown", type = Type.Movement,desc = "Отключение замедления",
        keywords = {"NoSlow"})
public class NoSlowFunction extends Function {
    private final SliderSetting speed = new SliderSetting("Скорость", 85.0F, 0.01F, 100.0F, 0.01F);
    public ModeSetting mode = new ModeSetting("Мод", "Matrix", "Vanilla", "Matrix", "Really World", "GrimAC","NCP","ReallCraft","FunTime");
    private DamageUtil damageUtil = new DamageUtil();
    public TimerUtil timerUtil = new TimerUtil();
    public TimerUtil timerUtil2 = new TimerUtil();
    public NoSlowFunction() {
        addSettings(mode,speed);
    }

    private static long lastExecutionTime = 0;
    Minecraft mc = Minecraft.getInstance();
    KeyBinding useItemKey = mc.gameSettings.keyBindUseItem;
    KeyBinding sprintKey = mc.gameSettings.keyBindSprint;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Override
    public void onEvent(final Event event) {
        if (mc.player.isElytraFlying()) return;

        if (event instanceof EventNoSlow eventNoSlow) {
            handleEventUpdate(eventNoSlow);
        }
        if (event instanceof EventDamage damage) {
            damageUtil.processDamage(damage);
        }
        if (event instanceof EventPacket eventPacket) {
            if (eventPacket.isReceivePacket())
                damageUtil.onPacketEvent(eventPacket);
        }

        if (mode.is("Really World")) {
        }
    }


    /**
     * Обрабатывает событие типа EventUpdate.
     */
    private void handleEventUpdate(EventNoSlow eventNoSlow) {
        if (mc.player.isHandActive()) {
            switch (mode.get()) {
                case "Vanilla" -> eventNoSlow.setCancel(true);
                case "GrimAC" -> handleGrimACMode(eventNoSlow);
                case "Matrix" -> handleMatrixMode(eventNoSlow);
                case "Really World" -> handleGrimNewMode(eventNoSlow);
                case "NCP" -> ncpnoslow(eventNoSlow);
                case "ReallCraft" -> ReallCraft(eventNoSlow);
                case "FunTime" -> funtime(eventNoSlow);
            }
        }
    }

    /**
     * Обрабатывает мод "Matrix".
     */
    private void funtime(EventNoSlow eventNoSlow) {
        if (mc.player == null || mc.player.isElytraFlying()) return;
        if (!isBlockUnderWithMotion() && mc.player.isOnGround() && !mc.player.movementInput.jump && !mc.player.isPotionActive(Effects.SLOWNESS)) {
            float boost = mc.player.moveStrafing == 0 || mc.player.moveForward == 0 ? 0.015F : 0F;
            float speed = mc.player.isPotionActive(Effects.SPEED) ? 0.35F : 0.3f;
            MoveUtil.setSpeed(speed + boost);
            if (timerUtil.hasTimeElapsed(120)) {
                BlockPos.getAllInBox(mc.player.getBoundingBox().offset(0, -1e-1, 0)).filter(pos -> !mc.world.getBlockState(pos).isAir())
                        .forEach(pos -> mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP)));
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
                scheduler.schedule(() -> mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY)), 1, TimeUnit.MILLISECONDS);
                timerUtil.reset();
            }
        }
    }
    public boolean isBlockUnderWithMotion() {
        AxisAlignedBB aab = mc.player.getBoundingBox().offset(mc.player.getMotion().x, -1e-1, mc.player.getMotion().z);
        return mc.world.getCollisionShapes(mc.player, aab).toList().isEmpty();
    }
    private void ReallCraft(EventNoSlow eventNoSlow) {
        if (mc.player.isHandActive()) {
            MoveUtil.setSpeed(0.2f);
        }
    }
    private void ncpnoslow(EventNoSlow eventNoSlow) {
        if (!mc.player.isHandActive() && !mc.player.isRidingHorse()) {
            mc.player.movementInput.moveForward *= (1f * (speed.getValue().floatValue() / 100f));
            mc.player.movementInput.moveStrafe *= (1f * (speed.getValue().floatValue() / 100f));
        }
        if (mc.player.isHandActive() && !mc.player.isRidingHorse() && !mc.player.isSneaking()) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemFrameItem || mc.player.getHeldItemOffhand().getItem() instanceof ItemFrameItem)
                mc.player.connection.sendPacket(new SHeldItemChangePacket(mc.player.inventory.currentItem));
            mc.player.movementInput.moveForward /= 0.2;
            mc.player.movementInput.moveStrafe /= 0.2;
        }
    }

    private void handleMatrixMode(EventNoSlow eventNoSlow) {
        boolean isFalling = (double) mc.player.fallDistance > 0.725;
        float speedMultiplier;
        eventNoSlow.setCancel(true);
        if (mc.player.isOnGround() && !mc.player.movementInput.jump) {
            if (mc.player.ticksExisted % 2 == 0) {
                boolean isNotStrafing = mc.player.moveStrafing == 0.0F;
                speedMultiplier = isNotStrafing ? 0.5F : 0.4F;
                mc.player.motion.x *= speedMultiplier;
                mc.player.motion.z *= speedMultiplier;
            }
        } else if (isFalling) {
            boolean isVeryFastFalling = (double) mc.player.fallDistance > 1.4;
            speedMultiplier = isVeryFastFalling ? 0.95F : 0.97F;
            mc.player.motion.x *= speedMultiplier;
            mc.player.motion.z *= speedMultiplier;
        }
    }
    private long lastClickTime = 0;
    private long lastPressTime = 0;
    private boolean isPressing = false;
    int i;
    private boolean isSwitching = false;
    long currentTime = System.currentTimeMillis();
    private long blockStartTime = 0; // Время начала блокировки

    private void handleGrimNewMode(EventNoSlow noSlow) {
        if (Aura.target != null) {
            return; // Если есть цель, ничего не делаем
        }

        // Проверяем, блокирует ли игрок или ест
        if (mc.player.isBlocking()) {
            if (!isSwitching) {
 // Активируем noSlow
                isSwitching = true; // Устанавливаем флаг переключения
                blockStartTime = System.currentTimeMillis(); // Запоминаем время начала блокировки
            }

            long elapsedTime = System.currentTimeMillis() - blockStartTime; // Вычисляем прошедшее время

            if (elapsedTime < 320) {
                // Переключаем слоты каждые 155 мс
                    int newSlot = (int) ((elapsedTime / 25) % 5) + 2; // Слоты от 2 до 6
                    mc.player.inventory.currentItem = newSlot;
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(newSlot)); // Отправка пакета о смене слота

            } else {
                // Если прошло 1.5 секунды, продолжаем активировать noSlow
                noSlow.setCancel(true); // Активируем noSlow
                // isSwitching остается true, чтобы продолжать активировать noSlow
            }
        } else {
            // Если игрок не блокирует
            if (isSwitching) {
                blockStartTime = 0; // Сбрасываем время
                isSwitching = false; // Останавливаем переключение
            }
        }
    }



    /**
     * Обрабатывает мод "GrimAC".
     */
    private void handleGrimACMode(EventNoSlow noSlow) {
        if (mc.player.getHeldItemOffhand().getUseAction() == UseAction.BLOCK && mc.player.getActiveHand() == Hand.MAIN_HAND || mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT && mc.player.getActiveHand() == Hand.MAIN_HAND) {
            return;
        }

        if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            noSlow.setCancel(true);
            return;
        }

        noSlow.setCancel(true);
        sendItemChangePacket();
    }

    /**
     * Отправляет пакеты смены активного предмета, если игрок движется.
     */
    private void sendItemChangePacket() {
        if (MoveUtil.isMoving()) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket((mc.player.inventory.currentItem % 8 + 1)));
            mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
        }
    }

    private void sendPacketItemRW() {
        // Save the current slot
        if (mc.gameSettings.keyBindUseItem.isKeyDown()) {


            int currentSlot = mc.player.inventory.currentItem;
            // Change to slot 2
            mc.player.connection.sendPacket(new CHeldItemChangePacket(1));

            // Change back to the original slot
            mc.player.connection.sendPacket(new CHeldItemChangePacket(currentSlot));
        }
    }

    @Override
    protected void onEnable() {
        super.onEnable();
    }
}
