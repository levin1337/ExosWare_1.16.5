package ru.levinov.modules.impl.movement;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventInput;
import ru.levinov.events.impl.player.EventMove;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.math.MathUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.world.InventoryUtil;

import java.util.Iterator;
import java.util.List;

/**
 * @author levin1337
 * @since 11.06.2023
 */
@FunctionAnnotation(name = "Speed", type = Type.Movement,desc = "Ускорение персонажа")
public class SpeedFunction extends Function {

    private final ModeSetting spdMode = new ModeSetting("Режим", "Matrix", "Matrix", "Timer", "Sunrise DMG", "Really World","ElytraVulcan","Test","FunTime","EntityBoost","Storm","Vulcan","ElytraGrim","ElytraGrim2","HoweLand","TestBedWars");
    private final SliderSetting speed = new SliderSetting("Скорость", 1.0F, 0.01F, 10.0F, 0.01F);

    private BooleanOption fall = new BooleanOption("Проверка на падение", true).setVisible(() -> spdMode.is("ElytraGrim2") || spdMode.is("ElytraGrim"));

    public SpeedFunction() {
        addSettings(spdMode,speed,fall);
    }


    public boolean boosting;

    @Override
    protected void onEnable() {
        super.onEnable();
        timerUtil.reset();
        boosting = false;
    }

    public TimerUtil timerUtil = new TimerUtil();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPacket e) {
        } else if (event instanceof EventMove) {
            handleEventMove((EventMove) event);
        } else if (event instanceof EventUpdate) {
            handleEventUpdate((EventUpdate) event);
            event.setCancel(true);
        }
    }

    private void handleEventMove(EventMove eventMove) {
        if (spdMode.is("Matrix")) {
            if (!mc.player.isOnGround() && mc.player.fallDistance >= 0.5f && eventMove.toGround()) {
                applyMatrixSpeed();
            }
        }
    }

    private void handleEventUpdate(EventUpdate eventUpdate) {
        switch (spdMode.get()) {
            case "Really World" -> handleRWMode();
            case "Timer" -> handleTimerMode();
            case "Sunrise DMG" -> handleSunriseDamageMode();
            case "ElytraVulcan" -> vulcan();
            case "EntityBoost" -> entityboost();
            case "Storm" -> storm();
            case "Vulcan" -> vulcan2();
            case "ElytraGrim" -> elytra();
            case "ElytraGrim2" -> elytra2();
            case "HoweLand" -> howeland();
            case "FunTime" -> funtime();
            case "TestBedWars" -> look();
        }
    }
    private void look() {
        // Активируем элитру

        // Проверяем, надета ли уже элитра
        ItemStack chestItem = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        boolean hasElytraEquipped = chestItem.getItem() == Items.ELYTRA;
        mc.player.startFallFlying();
        if (hasElytraEquipped) {
            // Если элитра уже надета - просто активируем полет
            if (!mc.player.isElytraFlying()) {
                mc.player.connection.sendPacket(
                        new CEntityActionPacket(
                                mc.player,
                                CEntityActionPacket.Action.START_FALL_FLYING
                        )
                );
            }
            return;
        }

        // Поиск элитры в инвентаре
        int elytraSlot = -1;
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (stack.getItem() == Items.ELYTRA) {
                elytraSlot = slot;
                break;
            }
        }

        if (elytraSlot == -1) return; // Элитра не найдена

        // Сохраняем текущий слот игрока
        int prevSelectedSlot = mc.player.inventory.currentItem;

        // Надеваем элитру
        if (elytraSlot < 9) {
            // Если элитра в хотбаре - просто выбираем слот
            mc.player.inventory.currentItem = elytraSlot;
        } else {
            // Если элитра не в хотбаре - перемещаем в хотбар
            int hotbarSlot = 0; // Выбираем первый слот хотбара
            mc.playerController.windowClick(0, elytraSlot, hotbarSlot, ClickType.SWAP, mc.player);
            mc.player.inventory.currentItem = hotbarSlot;
        }

        // Активируем элитру
        mc.player.connection.sendPacket(
                new CEntityActionPacket(
                        mc.player,
                        CEntityActionPacket.Action.START_FALL_FLYING
                )
        );

        // Возвращаем предыдущий слот
        mc.player.inventory.currentItem = prevSelectedSlot;
    }
    private void funtime() {
        AxisAlignedBB aabb = mc.player.getBoundingBox().grow(0.01);
        List<ArmorStandEntity> armorStandEntities = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb);
        List<LivingEntity> livingEntities = mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb);
        int armorStandsCount = armorStandEntities.size();
        int signItemsCount = armorStandEntities.size();
        boolean canBoost = signItemsCount > 1 ||  armorStandsCount > 1 || livingEntities.size() > 1;
        if (canBoost && !mc.player.isOnGround()) {
            mc.player.jumpMovementFactor = armorStandsCount > 1 ? 1.0F / (float) armorStandsCount : 0.15F;
        }
    }
    private void howeland() {
        AxisAlignedBB aabb = mc.player.getBoundingBox().grow(0.4);
        List<ArmorStandEntity> armorStandEntities = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb);
        List<LivingEntity> livingEntities = mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb);
        int armorStandsCount = armorStandEntities.size();
        int signItemsCount = armorStandEntities.size();
        boolean canBoost = signItemsCount > 1 || armorStandsCount > 1 || livingEntities.size() > 1;
        if (canBoost && !mc.player.isOnGround()) {
            mc.player.jumpMovementFactor = armorStandsCount > 0.4 ? 0.1F / (float) armorStandsCount : 0.1F;
        }
    }
    private void elytra2() {
        if (mc.gameSettings.keyBindJump.isKeyDown() && !fall.get() || mc.player.fallDistance > 0.3f) {
            int elytra = getElytra();
            if (mc.player.isElytraFlying()) {
                mc.player.stopFallFlying();
            } else {
                for (int i = 0; i < 9; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                        mc.player.stopFallFlying();
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        mc.player.startFallFlying();
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                        mc.player.stopFallFlying();
                        mc.player.stopFallFlying();
                    }
                }
            }
        }
    }

    private void elytra() {
        if (fall.get()) {
            if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.fallDistance > 0.2f) {
                if (timerUtil.hasTimeElapsed(1)) {
// Проверяем, находится ли игрок в воздухе
                    if (mc.player.isElytraFlying()) {
                        mc.player.startFallFlying();
                    } else {
                        for (int i = 0; i < 9; ++i) {
                            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA && !mc.player.isInLava()) {
                                mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                                mc.player.startFallFlying();
                                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                                mc.player.startFallFlying();
                                mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                            }
                        }
                    }
                    timerUtil.reset(); // Сбрасываем таймер
                }
            }
        } else {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                if (timerUtil.hasTimeElapsed(1)) {
                    if (mc.player.isElytraFlying()) {
                        mc.player.startFallFlying();
                    } else {
                        for (int i = 0; i < 9; ++i) {
                            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA && !mc.player.isInLava()) {
                                mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                                mc.player.startFallFlying(); // Начинаем летать на элитрах

                                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                                mc.player.startFallFlying(); // Дублируем вызов, чтобы убедиться, что действие выполнено


                                mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                            }
                        }
                    }
                    timerUtil.reset(); // Сбрасываем таймер
                }
            }
        }

        // mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        // mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
    }
    public static long lastStartFalling;
    private void vulcan2() {
        mc.player.setSprinting(false);
        double radians = (double) MoveUtil.getDirection();
        int elytra = InventoryUtil.findInventoryElytra();
        if (MoveUtil.isMoving()) {
            if (mc.player.ticksExisted % 4 == 0) {
                disabler(elytra);
            }
            if (mc.player.isOnGround()) {
                mc.player.addVelocity(-Math.sin(radians) * (double) speed.getValue().floatValue() / (double) 24.5F, (double) 0.0F, Math.cos(radians) * (double) speed.getValue().floatValue() / (double) 24.5F);
                MoveUtil.setSpeed((double) MoveUtil.getMotion());
            } else {
                if (mc.player.isInWater()) {
                    mc.player.addVelocity(-Math.sin(radians) * (double) speed.getValue().floatValue() / (double) 24.5F, (double) 0.0F, Math.cos(radians) * (double) speed.getValue().floatValue() / (double) 24.5F);
                    MoveUtil.setSpeed((double) MoveUtil.getMotion());
                } else {
                    if (!mc.player.isOnGround()) {
                        mc.player.addVelocity(-Math.sin(radians) * (double) 0.5F / (double) 24.5F, (double) 0.0F, Math.cos(radians) * (double) 0.5F / (double) 24.5F);
                        MoveUtil.setSpeed((double) MoveUtil.getMotion());
                    }
                }
            }
        }
    }
    public static void disabler(int elytra) {
        if (elytra != -2) {
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        }
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));

        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        if (elytra != -2) {
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
        }

    }
    public static void matrixpuzo(int elytra) {
        elytra = elytra >= 0 && elytra < 9 ? elytra + 36 : elytra;
        if (elytra != -2) {
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        }
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));

        if (elytra != -2) {
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
        }

        lastStartFalling = System.currentTimeMillis();
    }
    private void storm() {
        if (!mc.player.isOnGround() && mc.player.fallDistance >= 0.5f) {
            mc.timer.timerSpeed = 1.1f;
            MoveUtil.setSpeed(0.5f);
        }
    }
    private void entityboost() {
        Iterator var2 = mc.world.getPlayers().iterator();
        while(true) {
            Vector3d var10000;
            PlayerEntity entity;
            do {
                do {
                    do {
                        if (!var2.hasNext()) {
                            return;
                        }
                        entity = (PlayerEntity) var2.next();
                    } while (mc.player == entity);
                } while(!(mc.player.getDistance(entity) <= 2.0F));
            } while(!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown());

            mc.player.getMotion().x *= speed.getValue().floatValue();
            mc.player.getMotion().z *= speed.getValue().floatValue();
        }
    }
    private void vulcan() {
        if (mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() != Items.ELYTRA && mc.player.inventory.getItemStack().getItem() != Items.ELYTRA && getElytra() == -1) {
            ClientUtil.sendMesage("Для данного модуля нужны элитры.");
            this.toggle();
        }
        MoveUtil.setSpeed(speed.getValue().floatValue());
        if (mc.player.fallDistance != 0.0F && (double)mc.player.fallDistance < 0.1 && this.timerUtil.hasTimeElapsed(190L) && MoveUtil.isMoving()) {
            this.useElytra();
            this.timerUtil.reset();
        }

    }
    private float waterTicks = 0.0F;
    private void handleRWMode() {
        mc.timer.timerSpeed = 0.2f;
        MoveUtil.setSpeed(0.35f);
    }
    private boolean isOnEdge(BlockPos pos) {
        double offsetX = Math.abs(mc.player.getPosX() - pos.getX() - 0.5);
        double offsetZ = Math.abs(mc.player.getPosZ() - pos.getZ() - 0.5);

        return (offsetX > 0.3 && offsetX < 0.5) || (offsetZ > 0.3 && offsetZ < 0.5);
    }

    private void applyMatrixSpeed() {
        double speed = 2;
        mc.player.motion.x *= speed;
        mc.player.motion.z *= speed;
        MoveUtil.StrafeMovement.oldSpeed *= speed;
    }

    private void handleTimerMode() {
        if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder()) {
            return;
        }

        float timerValue = 1;
        if (mc.player.fallDistance <= 0.1f) {
            timerValue = 1.34f;
        }
        if (mc.player.fallDistance > 1.0f) {
            timerValue = 0.6f;
        }

        if (MoveUtil.isMoving()) {
            mc.timer.timerSpeed = 1;
            if (mc.player.isOnGround()) {
                if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.jump();
                }
            } else {
                mc.timer.timerSpeed = timerValue;
            }
        } else {
            mc.timer.timerSpeed = 1.0f;
        }
    }

    private void handleSunriseDamageMode() {
        double radians = MoveUtil.getDirection();

        if (MoveUtil.isMoving()) {
            if (mc.player.isOnGround()) {
                applySunriseGroundMotion(radians);
            } else if (mc.player.isInWater()) {
                applySunriseWaterMotion(radians);
            } else if (!mc.player.isOnGround()) {
                applySunriseAirMotion(radians);
            } else {
                applySunriseDefaultMotion(radians);
            }
        }
    }

    private void applySunriseGroundMotion(double radians) {
        mc.player.addVelocity(-MathHelper.sin(radians) * 9.5 / 24.5, 0, MathHelper.cos(radians) * 9.5 / 24.5);
        MoveUtil.setMotion(MoveUtil.getMotion());
    }
    public void useElytra() {
        int elytra = getElytra();
        if (!mc.player.isInWater() && !mc.player.isInLava() && !(this.waterTicks > 0.0F) && elytra != -1 && mc.player.fallDistance != 0.0F && (double)mc.player.fallDistance < 0.1 && mc.player.motion.y < -0.1) {
            if (elytra != -2) {
                mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            }

            mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            if (elytra != -2) {
                mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            }
        }

    }

    public static int getElytra() {
        Iterator var0 = mc.player.getArmorInventoryList().iterator();

        while(var0.hasNext()) {
            ItemStack stack = (ItemStack)var0.next();
            if (stack.getItem() == Items.ELYTRA) {
                return -2;
            }
        }

        int slot = -1;

        for(int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == Items.ELYTRA) {
                slot = i;
                break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }

        return slot;
    }

    private void applySunriseWaterMotion(double radians) {
        mc.player.addVelocity(-MathHelper.sin(radians) * 9.5 / 24.5, 0, MathHelper.cos(radians) * 9.5 / 24.5);
        MoveUtil.setMotion(MoveUtil.getMotion());
    }

    private void applySunriseAirMotion(double radians) {
        mc.player.addVelocity(-MathHelper.sin(radians) * 0.11 / 24.5, 0, MathHelper.cos(radians) * 0.11 / 24.5);
        MoveUtil.setMotion(MoveUtil.getMotion());
    }

    private void applySunriseDefaultMotion(double radians) {
        mc.player.addVelocity(-MathHelper.sin(radians) * 0.005 * MoveUtil.getMotion(), 0,
                MathHelper.cos(radians) * 0.005 * MoveUtil.getMotion());
        MoveUtil.setMotion(MoveUtil.getMotion());
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }
}
