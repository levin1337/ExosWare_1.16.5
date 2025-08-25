package ru.levinov.modules.impl.movement;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.*;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.DamageUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.render.RenderUtil;

import java.util.Iterator;

import static ru.levinov.util.movement.MoveUtil.StrafeMovement.*;

/**
 * @author levin1337
 * @since 07.06.2023
 */
@FunctionAnnotation(
        name = "Strafe",
        type = Type.Movement,
        desc = "Быстрое перемещение"
)
public class StrafeFunction extends Function {
    public final ModeSetting modestrafe = new ModeSetting("Мод", "Default", "Default", "Elytra", "HoweLand","Matrix - Vulcan");
    private final SliderSetting speed = new SliderSetting("Скорость", 1.0F, 0.0F, 10.0F, 0.1F);
    private BooleanOption damageBoost = new BooleanOption("Буст с дамагом", false);
    private SliderSetting boostSpeed = new SliderSetting("Скорость буста", 0.7F, 0.1F, 5.0F, 0.1F);


    private DamageUtil damageUtil = new DamageUtil();
    private float waterTicks = 0.0F;
    public static boolean dZw;

    private final TimerUtil elytraDelay = new TimerUtil();

    public StrafeFunction() {
        this.addSettings(modestrafe,this.damageBoost, this.boostSpeed, this.speed);
    }

    public void onEvent(Event event) {
        if (this.modestrafe.is("Default")) {
            if (event instanceof EventAction) {
                EventAction action = (EventAction)event;
                this.handleEventAction(action);
            } else if (event instanceof EventMove) {
                EventMove eventMove = (EventMove)event;
                this.handleEventMove(eventMove);
            } else if (event instanceof EventPostMove) {
                EventPostMove eventPostMove = (EventPostMove)event;
                this.handleEventPostMove(eventPostMove);
            } else if (event instanceof EventPacket) {
                EventPacket packet = (EventPacket)event;
                this.handleEventPacket(packet);
            } else if (event instanceof EventDamage) {
                EventDamage damage = (EventDamage)event;
                this.handleDamageEvent(damage);
            }
        }

        if (event instanceof EventUpdate) {
            if (this.modestrafe.is("Elytra")) {
                if (!mc.player.isElytraFlying()) {
                    if (mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() != Items.ELYTRA && mc.player.inventory.getItemStack().getItem() != Items.ELYTRA && getElytra() == -1) {
                        ClientUtil.sendMesage("Для данного мода нужны элитры.");
                        toggle();
                    }
                    MoveUtil.setSpeed((double) this.speed.getValue().floatValue());
                    int emptySlot = InventoryHelper.getItemIndex(Item.getItemFromBlock(Blocks.AIR));
                    if (emptySlot != -1 && mc.player.inventory.getItemStack().getItem() == Items.ELYTRA && InventoryHelper.getItemIndex(Items.ELYTRA) == -1) {
                        mc.playerController.windowClick(0, emptySlot < 9 ? emptySlot + 36 : emptySlot, 1, ClickType.PICKUP, mc.player);
                    }

                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    if (InventoryHelper.getItemIndex(Items.ELYTRA) == -1) {
                        return;
                    }
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    if (mc.player.inventory.getItemStack().getItem() instanceof ArmorItem && mc.player.inventory.armorItemInSlot(2).isEmpty()) {
                        ArmorItem armor = (ArmorItem) mc.player.inventory.getItemStack().getItem();
                        mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
                    }

                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    if (mc.player.inventory.getItemStack().getItem() instanceof ArmorItem && !mc.player.inventory.armorItemInSlot(2).isEmpty()) {
                        mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
                        int emptySlot2 = InventoryHelper.getItemIndex(Item.getItemFromBlock(Blocks.AIR));
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        if (emptySlot2 != -1 && mc.player.inventory.getItemStack().getItem() == Items.ELYTRA && InventoryHelper.getItemIndex(Items.ELYTRA) == -1) {
                            mc.playerController.windowClick(0, emptySlot2 < 9 ? emptySlot2 + 36 : emptySlot2, 1, ClickType.PICKUP, mc.player);
                        }
                    }

                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                }
            }

            if (event instanceof EventUpdate && this.modestrafe.is("HoweLand")) {
                if (!mc.player.isElytraFlying()) {
                    if (mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() != Items.ELYTRA && mc.player.inventory.getItemStack().getItem() != Items.ELYTRA && getElytra() == -1) {
                        ClientUtil.sendMesage("Для данного мода нужны элитры.");
                        this.toggle();
                    }

                    if ((double) mc.player.fallDistance < 0.1 && mc.player.motion.y < -0.1 && this.elytraDelay.hasTimeElapsed(190L)) {
                        MoveUtil.setSpeed((double) this.speed.getValue().floatValue());
                        this.useElytra();
                        this.elytraDelay.reset();
                    }
                }
            }
        }
        if (event instanceof EventUpdate) {
            if (this.modestrafe.is("Matrix - Vulcan")) {
                if (!mc.gameSettings.keyBindForward.pressed)
                    return;
                if(!MoveUtil.isMoving())
                    return;

                MoveUtil.strafe(MoveUtil.getSpeed() + 0.005f);
            }
        }
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



    private void handleDamageEvent(EventDamage damage) {
        if (this.damageBoost.get()) {
            this.damageUtil.processDamage(damage);
        }
    }
    private void handleEventAction(EventAction action) {
        if (strafes()) {
            this.handleStrafesEventAction(action);
        }
        if (MoveUtil.StrafeMovement.needSwap) {
            this.handleNeedSwapEventAction(action);
        }
    }

    private void handleEventMove(EventMove eventMove) {
        if (strafes()) {
            this.handleStrafesEventMove(eventMove);
        } else {
            MoveUtil.StrafeMovement.oldSpeed = 0.0;
        }
    }

    private void handleEventPostMove(EventPostMove eventPostMove) {
        MoveUtil.StrafeMovement.postMove(eventPostMove.getHorizontalMove());
    }

    private void handleEventPacket(EventPacket packet) {
        if (packet.isReceivePacket()) {
            if (this.damageBoost.get()) {
                this.damageUtil.onPacketEvent(packet);
            }

            this.handleReceivePacketEventPacket(packet);
        }

    }

    private void handleStrafesEventAction(EventAction action) {

    }

    private void handleStrafesEventMove(EventMove eventMove) {
        if (this.damageBoost.get()) {
            this.damageUtil.time(700L);
        }

        float damageSpeed = this.boostSpeed.getValue().floatValue() / 10.0F;
        double speed = MoveUtil.StrafeMovement.calculateSpeed(eventMove, this.damageBoost.get(), this.damageUtil.isNormalDamage(), damageSpeed);
        MoveUtil.MoveEvent.setMoveMotion(eventMove, speed);
    }

    private void handleNeedSwapEventAction(EventAction action) {

    }

    private void handleReceivePacketEventPacket(EventPacket packet) {
        if (packet.getPacket() instanceof SPlayerPositionLookPacket) {
            MoveUtil.StrafeMovement.oldSpeed = 0.0;
        }
    }

    public static boolean strafes() {
        if (mc.player != null && mc.world != null) {
            if (!mc.player.isSneaking() && !mc.player.isElytraFlying()) {
                if ((mc.player.isInWater() || mc.player.isInLava()) && mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isSneaking() && !(mc.world.getBlockState(mc.player.getPosition().add(0, 1, 0)).getBlock() instanceof AirBlock)) {
                    return false;
                } else if (mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ())).getMaterial() != Material.WEB && !(mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 0.01, mc.player.getPosZ())).getBlock() instanceof SoulSandBlock)) {
                    return !mc.player.abilities.isFlying && !mc.player.isPotionActive(Effects.LEVITATION);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void onEnable() {
        MoveUtil.StrafeMovement.oldSpeed = 0.0;
        super.onEnable();
    }

    protected void onDisable() {
        Vector3d var10000 = mc.player.motion;
        var10000.x *= 0.699999988079071;
        var10000 = mc.player.motion;
        var10000.z *= 0.699999988079071;
        super.onDisable();
    }
}
