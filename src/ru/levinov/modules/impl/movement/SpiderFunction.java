package ru.levinov.modules.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.impl.util.Scaffold;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.math.RayTraceUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.world.InventoryUtil;

import java.util.Arrays;
import java.util.Random;

import static ru.levinov.util.IMinecraft.mc;

/**
 * @author levin1337
 * @since 19.06.2023
 */
@FunctionAnnotation(name = "Spider", type = Type.Movement,desc = "Ползанье по стенам")
public class SpiderFunction extends Function {
    TimerUtil timerUtil = new TimerUtil();
    public ModeSetting mode = new ModeSetting("Mode", "Grim", "Grim", "Matrix", "FunTime", "ElytraGrim","ElytraGrim2");


    private final SliderSetting spiderSpeed = new SliderSetting(
            "Скорость",
            2.0f,
            1.0f,
            1000.0f,
            0.05f
    ).setVisible(() -> !mode.is("Grim"));
    int i;
    int oldItem = -1;
    public SpiderFunction() {
        addSettings(mode,spiderSpeed);
    }

    @Override
    public void onEvent(Event event) {
        if (mode.is("ElytraGrim")) {
            if (event instanceof EventMotion eventMotion) {
                eventMotion = (EventMotion) event;
                eventMotion.setPitch(0.0F);
                mc.player.rotationPitchHead = 0.0F;
            }

            if (event instanceof EventUpdate) {
                for (i = 0; i < 9; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA && !mc.player.isOnGround() && mc.player.collidedHorizontally && mc.player.fallDistance == 0.0F) {
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        MoveUtil.setMotion(0.06);
                        mc.player.motion.y = 0.366;
                        mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                        oldItem = i;
                    }
                }
            }

            if (event instanceof EventPacket e) {
                e = (EventPacket) event;
                IPacket var4 = e.getPacket();
                if (var4 instanceof SPlayerPositionLookPacket) {
                    SPlayerPositionLookPacket p = (SPlayerPositionLookPacket) var4;
                    mc.player.func_242277_a(new Vector3d(p.getX(), p.getY(), p.getZ()));
                    mc.player.setRawPosition(p.getX(), p.getY(), p.getZ());

                }
            }
            if (event instanceof EventPacket e) {
                e = (EventPacket) event;
                if (e.getPacket() instanceof SEntityMetadataPacket && ((SEntityMetadataPacket) e.getPacket()).getEntityId() == mc.player.getEntityId()) {
                    e.setCancel(true);
                }
            }
        }
        if (mode.is("ElytraGrim2")) {
            if (event instanceof EventMotion eventMotion) {
                eventMotion = (EventMotion) event;
                float rotation = 0.0F;
                eventMotion.setPitch(rotation);
                mc.player.rotationPitchHead = rotation;
            }

            if (event instanceof EventUpdate) {
                if (((ItemStack) mc.player.inventory.armorInventory.get(2)).getItem() != Items.ELYTRA && mc.player.collidedHorizontally) {
                    for (i = 0; i < 9; ++i) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                            mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                            this.oldItem = i;
                            this.timerUtil.reset();
                        }
                    }
                }

                if (mc.player.collidedHorizontally) {
                    mc.gameSettings.keyBindJump.setPressed(false);
                }

                if (((ItemStack) mc.player.inventory.armorInventory.get(2)).getItem() == Items.ELYTRA && !mc.player.collidedHorizontally && this.oldItem != -1) {
                    mc.playerController.windowClick(0, 6, this.oldItem, ClickType.SWAP, mc.player);
                    oldItem = -1;
                }

                if (((ItemStack) mc.player.inventory.armorInventory.get(2)).getItem() == Items.ELYTRA && !mc.player.isOnGround() && mc.player.collidedHorizontally) {
                    if (mc.player.fallDistance != 0.0F) {
                    }
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    MoveUtil.setMotion(0.02);
                    mc.player.motion.y = 0.36;
                }
            }

            if (event instanceof EventPacket e) {
                e = (EventPacket) event;
                if (e.getPacket() instanceof SEntityMetadataPacket && ((SEntityMetadataPacket) e.getPacket()).getEntityId() == mc.player.getEntityId() && !mc.player.isElytraFlying()) {
                    e.setCancel(true);
                }
            }
        }
        if (event instanceof EventMotion e) {
            if (!mc.player.collidedHorizontally) {
                return;
            }
            if (mode.is("Matrix")) {
                if (timerUtil.hasTimeElapsed((long) spiderSpeed.getValue().floatValue())) {
                    e.setOnGround(true);
                    mc.player.setOnGround(true);
                    mc.player.collidedVertically = true;
                    mc.player.collidedHorizontally = true;
                    mc.player.isAirBorne = true;
                    mc.player.jump();
                    timerUtil.reset();
                }
            }
            if (mode.is("FunTime")) {
                if (timerUtil.hasTimeElapsed((long) spiderSpeed.getValue().floatValue())) {
                    e.setOnGround(true);
                    mc.player.setOnGround(true);
                    mc.player.collidedVertically = true;
                    mc.player.collidedHorizontally = true;
                    mc.player.isAirBorne = true;
                    mc.player.jump();
                    timerUtil.reset();
                }
                placeFencesInFront();
            }
            if (mode.is("Grim")) {
                if (mc.player.isOnGround()) {
                    e.setOnGround(true);
                    mc.player.setOnGround(true);
                    mc.player.jump();
                }
                if (mc.player.fallDistance > 0 && mc.player.fallDistance < 2) {
                    int block = -1;
                    for (int i = 0; i < 9; i++) {
                        ItemStack s = mc.player.inventory.getStackInSlot(i);
                        if (s.getItem() instanceof BlockItem) {
                            block = i;
                            break;
                        }
                    }

                    if (block == -1) {
                        ClientUtil.sendMesage("Для использования этого спайдера у вас должны блоки в хотбаре!");
                        toggle();
                        return;
                    }

                    int last = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = block;
                    e.setPitch(80);
                    e.setYaw(mc.player.getHorizontalFacing().getHorizontalAngle());
                    BlockRayTraceResult r = (BlockRayTraceResult) RayTraceUtil.rayTrace(4, e.getYaw(), e.getPitch(), mc.player);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, r);
                    mc.player.inventory.currentItem = last;
                    mc.player.fallDistance = 0;
                }
            }
        }
    }

    public void placeFencesInFront() {
        // Получаем позицию игрока
        Vector3d playerPos = mc.player.getPositionVec();
        // Измените значение по своему усмотрению

        double offsetZ = Math.cos(Math.toRadians(mc.player.rotationPitch));

        BlockPos fencePos = new BlockPos(playerPos.x, playerPos.y + offsetZ, playerPos.z);

        // Проверяем, что блок под позицией установки - это забор
        // Случайный выбор типа забора

        // Устанавливаем забор
        BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(
                new Vector3d(fencePos.getX() + 0.5f, fencePos.getY() + 0.5f, fencePos.getZ() + 0.5f),
                Direction.UP, // Устанавливаем забор вверх
                fencePos,
                false
        );

        // Устанавливаем поворот игрока (если нужно)
        // Положение по оси X
        float[] rotation = rots(new Vector3d((double) fencePos.getX() + 0.5, (double) fencePos.getY() + 1f, (double) fencePos.getZ() + 0.5));
        //Бошка
        mc.player.rotationYawHead = rotation[0];
        mc.player.renderYawOffset = rotation[1];
        mc.player.rotationPitchHead = rotation[1];

        if (mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, rayTraceResult) == ActionResultType.SUCCESS) {
            mc.player.swingArm(Hand.MAIN_HAND);
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
