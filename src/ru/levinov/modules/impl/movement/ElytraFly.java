package ru.levinov.modules.impl.movement;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.system.CallbackI;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventMove;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.events.impl.player.StartFallFlyingEvent;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.world.InventoryUtil;

/**
 * @author levin1337
 * @since 11.06.2023
 */
@FunctionAnnotation(name = "ElytraFly", type = Type.Movement,desc = "Быстрое летание на элитрах")
public class ElytraFly extends Function {
    private final ModeSetting mode = new ModeSetting("Мод", "Новый", "Новый", "Старый");

    private final TimerUtil timerUtil = new TimerUtil();
    private final BooleanOption boosting = new BooleanOption("Ускорение", false);
    private final SliderSetting speedXZ = new SliderSetting("Скорость по XZ", 1.2f, 0.5f, 2.2f, 0.01f).setVisible(() -> boosting.get());
    private final SliderSetting speedY = new SliderSetting("Скорость по Y", 0.1f, 0.1f, 1.0f, 0.01f).setVisible(() -> boosting.get());
    private final SliderSetting fireworkDelay = new SliderSetting("Задержка фейрверка", 1.5f, 0.5f, 5f, 0.1f);
    private ItemStack getStackInSlotCopy;
    private Item prevItemInHand = Items.AIR;
    private int slotWithFireWorks = -1;
    private long lastFireworkTime;
    private boolean flying;
    private int currentSpeed;
    private int ticksInAir;
    private boolean startFallFlying;
    private boolean starting;
    private Integer oldArmor = -1;

    public ElytraFly() {
        addSettings(mode, speedXZ, speedY,boosting, fireworkDelay);
    }

    public boolean isActive() {
        return state && starting;
    }


    private void noFireworks() {
        ClientUtil.sendMesage("Нету феерверков в инвентаре!");
        setState(false);
        onDisable();
        flying = false;
        ticksInAir = 0;
    }

    private void noElytra() {
        ClientUtil.sendMesage("Нету элитр в инвентаре!");
        setState(false);
        onDisable();
        flying = false;
        ticksInAir = 0;
    }

    private boolean canFly() {
        if (shouldSwapToElytra()) {
            return false;
        }
        return InventoryUtil.getFireWorks() != -1;
    }

    private boolean shouldSwapToElytra() {
        ItemStack is = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (is.getItem() != Items.ELYTRA) {
            return true;
        }
        return !ElytraItem.isUsable(is);
    }

    private void fly(boolean started) {
        if (started && (float) (System.currentTimeMillis() - lastFireworkTime) < fireworkDelay.getValue().floatValue() * 1000.0f) {
            return;
        }
        if (started && !mc.player.isElytraFlying()) {
            return;
        }
        if (!started && ticksInAir > 1) {
            return;
        }
        int n2 = InventoryUtil.getFireWorks();

        if (n2 == -1) {
            slotWithFireWorks = -1;
            return;
        }
        slotWithFireWorks = n2;
        boolean bl3 = mc.player.getHeldItemOffhand().getItem() == Items.FIREWORK_ROCKET;
        if (!bl3) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(n2));
        }

        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(bl3 ? Hand.OFF_HAND : Hand.MAIN_HAND));
        if (!bl3) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
        }
        ++ticksInAir;
        flying = true;
        lastFireworkTime = System.currentTimeMillis();
    }


    public static int getElytraSlot() {
        if (mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA) {
            return -2;
        }
        if (mc.currentScreen instanceof ContainerScreen<?> && !(mc.currentScreen instanceof InventoryScreen) && !(mc.currentScreen instanceof CreativeScreen)) {
            return -1;
        }
        for (int i = 0; i < 45; ++i) {
            ItemStack is = mc.player.inventory.getStackInSlot(i);
            if (is.getItem() != Items.ELYTRA || !ElytraItem.isUsable(is)) continue;
            return i < 9 ? i + 36 : i;
        }
        return -1;
    }


    @Override
    public void onEvent(Event event) {
        if (mode.is("Старый")) {
            if (event instanceof StartFallFlyingEvent) {
                //fly(false);
            }
            if (event instanceof EventUpdate) {
                if (boosting.get()) {
                    if (mc.player.isOnGround()) {
                        startFallFlying = false;
                        ticksInAir = 0;
                        mc.gameSettings.keyBindJump.setPressed(false);
                        mc.player.jump();
                        return;
                    }
                    if (mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) {
                        currentSpeed = 0;
                    }

                    if (!canFly()) return;

                    if (!mc.player.isElytraFlying() && !startFallFlying && mc.player.motion.y < 0.0) {
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        startFallFlying = true;
                    }
                    if (mc.player.getTicksElytraFlying() < 4) {
                        mc.gameSettings.keyBindJump.setPressed(false);
                    }
                    fly(true);
                } else {
                    if (mc.player.isOnGround()) {
                        mc.player.jump();
                        startFallFlying = false;
                    }
                    if (mc.player.fallDistance > 0 && !mc.player.isElytraFlying() && !startFallFlying) {
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        startFallFlying = true;
                        fly(false);
                    }

                    if (mc.player.isElytraFlying()) {
                        fly(true);
                    }
                }
            }

            if (event instanceof EventMotion motionEvent) {
                if (boosting.get()) {
                    if (!(mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f) && mc.player.movementInput.jump && mc.player.isElytraFlying() && flying) {
                        motionEvent.setPitch(-90.0f);
                    }
                    if (mc.player.getTicksElytraFlying() < 5) {
                        motionEvent.setPitch(-90.0f);
                        starting = true;
                    } else {
                        starting = false;
                    }
                }
            }

            if (event instanceof EventMove move) {
                if (mc.player.isElytraFlying() && flying && boosting.get()) {
                    if (mc.player.getTicksElytraFlying() < 4) {
                        move.motion().y = 1.0;
                    }
                    float speed = speedXZ.getValue().floatValue() - 0.017f;
                    float speedY = this.speedY.getValue().floatValue();
                    mc.player.motion.y = 0;
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        move.motion().y += speedY;
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        move.motion().y -= speedY;
                    } else {
                        move.motion().y = mc.player.ticksExisted % 2 == 0 ? (double) 0.08f : (double) -0.08f;
                    }

                    mc.player.motion.y = move.motion().y * (double) speedY;
                    MoveUtil.MoveEvent.setMoveMotion(move, speed * Math.min((float) (currentSpeed += 9) / 100.0f, 1.0f));
                }
            }
        }
        if (mode.is("Новый")) {
            if (event instanceof EventUpdate) {
                if (boosting.get()) {
                    if (mc.player.isOnGround()) {
                        startFallFlying = false;
                        ticksInAir = 0;
                        mc.gameSettings.keyBindJump.setPressed(false);
                        mc.player.jump();
                        return;
                    }
                    if (mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) {
                        currentSpeed = 0;
                    }

                    if (!canFly()) return;

                    if (!mc.player.isElytraFlying() && !startFallFlying && mc.player.motion.y < 0.0) {
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        startFallFlying = true;
                    }
                    fly(true);
                } else {
                    if (mc.player.isOnGround()) {
                        mc.player.jump();
                        startFallFlying = false;
                    }
                    if (mc.player.fallDistance > 0 && !mc.player.isElytraFlying() && !startFallFlying) {
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        startFallFlying = true;
                        fly(false);
                    }

                    if (mc.player.isElytraFlying()) {
                        fly(true);
                    }
                }
            }


            if (event instanceof EventMove move) {
                final float motionY = this.speedY.getValue().floatValue();
                final float speed = this.speedXZ.getValue().floatValue();
                if (mc.player.isElytraFlying() && flying && boosting.get()) {
                    if (mc.gameSettings.keyBindSprint.isKeyDown()) {
                        MoveUtil.setMotion(speed);
                    } else {
                        MoveUtil.setMotion(speed);
                    }
                    mc.player.motion.y = 0;
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.player.motion.y = motionY;
                    }
                    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.motion.y = -motionY;
                    }

                }
            }
        }
    }

    private void useFirework() {
        if (InventoryUtil.getItemSlot(Items.FIREWORK_ROCKET) == -1) {
            ClientUtil.sendMesage(TextFormatting.RED + "У вас нету фейерверов!");
            toggle();
        } else {
            InventoryUtil.inventorySwapClick1337(Items.FIREWORK_ROCKET, false);
            timerUtil.reset();
        }
    }

    @Override
    public void onEnable() {
        if (mode.is("Старый")) {
            int elytraItem = getElytraSlot();
            if (mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() != Items.ELYTRA && mc.player.inventory.getItemStack().getItem() != Items.ELYTRA && getElytraSlot() == -1) {
                noElytra();
                return;
            }
            if (InventoryUtil.getFireWorks() == -1) {
                noFireworks();
                return;
            }

            if (!boosting.get()) {
                starting = true;
            }

            if (elytraItem != -2) {
                mc.playerController.windowClick(0, elytraItem, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, elytraItem, 0, ClickType.PICKUP, mc.player);
                oldArmor = elytraItem;
            }
        }
    }

    @Override
    public void onDisable() {
        if (mode.is("Старый")) {
            currentSpeed = 0;
            startFallFlying = false;
            new Thread(() -> {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (oldArmor != -1) {
                    mc.playerController.windowClick(0, oldArmor, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, oldArmor, 0, ClickType.PICKUP, mc.player);
                    oldArmor = -1;
                }
            }).start();
        }
    }
}
