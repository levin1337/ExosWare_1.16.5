package ru.levinov.modules.impl.util;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import ru.levinov.events.Event;
import ru.levinov.events.impl.game.EventKey;
import ru.levinov.events.impl.player.EventInput;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BindSetting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.world.InventoryUtil;

@FunctionAnnotation(name = "ElytraSwap", type = Type.Util,desc = "Управление элитрой",
        keywords = {"ElytraHelper"})
public class ElytraSwap extends Function {

    private BindSetting swapKey = new BindSetting("Кнопка свапа", 0);
    private BindSetting fireworkKey = new BindSetting("Кнопка феерверков", 0);
    private ItemStack oldStack = null;
    boolean startFallFlying;
    private BooleanOption inventory = new BooleanOption("брать из инвентаря", true);
    private BooleanOption autoFly = new BooleanOption("Автоматический взлёт", true);

    private BooleanOption notif = new BooleanOption("Оповещение", true);

    private BooleanOption bypass = new BooleanOption("Обход", false);
    public ElytraSwap() {
        addSettings(swapKey, fireworkKey, notif, autoFly,inventory,bypass);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (autoFly.get() && mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA) {
                if (mc.player.isOnGround()) {
                    startFallFlying = false;
                    mc.gameSettings.keyBindJump.setPressed(false);
                    mc.player.jump();
                    return;
                }

                if (!mc.player.isElytraFlying() && !startFallFlying && mc.player.motion.y < 0.0) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    startFallFlying = true;
                }
            }
        }
        if (event instanceof EventKey e) {
            ItemStack itemStack = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if (e.key == swapKey.getKey()) {
                int elytraSlot = InventoryUtil.getItemSlot(Items.ELYTRA);
                if (elytraSlot == -1) {
                    ClientUtil.sendMesage(TextFormatting.RED + "Не найдена элитры в инвентаре!");
                    return;
                }
                if (reasonToEquipElytra(itemStack)) {
                    ItemStack n = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                    oldStack = n.copy();
                    InventoryUtil.moveItem(elytraSlot, 6, true);
                    if (notif.get()) ClientUtil.sendMesage(TextFormatting.GREEN + "Элитра!");

                } else if (oldStack != null) {
                    int oldStackSlot = InventoryUtil.getItemSlot(oldStack.getItem());
                    InventoryUtil.moveItem(oldStackSlot, 6, true);
                    if (notif.get())
                        ClientUtil.sendMesage(TextFormatting.GREEN + "Нагрудник!");
                    oldStack = null;

                }
            }
            if (e.key == fireworkKey.getKey() && itemStack.getItem() == Items.ELYTRA) useFirework();

        }
    }

    private void useFirework() {
        int fireWorksSlot = InventoryUtil.getFireWorks();
        boolean offHand = mc.player.getHeldItemOffhand().getItem() == Items.FIREWORK_ROCKET;

        if (!offHand && fireWorksSlot == -1) {
          //  ClientUtil.sendMesage(TextFormatting.RED + "Нет феерверков!");
            if (inventory.get()) {
                //     ClientUtil.sendMesage(TextFormatting.RED + "У вас нету эндер-жемчюгов беру из инвентаря");
                if (InventoryUtil.getItemSlot(Items.FIREWORK_ROCKET) == -1) {
                    //  ClientUtil.sendMesage(TextFormatting.RED + "У вас нету эндер-жемчюгов");
                } else {
                    InventoryUtil.inventorySwapClick1337(Items.FIREWORK_ROCKET, false);
                }
            }
            return;
        }

        if (!offHand) mc.player.connection.sendPacket(new CHeldItemChangePacket(fireWorksSlot));
        if (bypass.get()) {
            useItem(offHand ? Hand.OFF_HAND : Hand.MAIN_HAND);
        } else {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(offHand ? Hand.OFF_HAND : Hand.MAIN_HAND));
        }
        if (!offHand) mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));

    }
    private void useItem(Hand hand) {
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(hand));
        mc.player.swingArm(hand);
    }

    private boolean reasonToEquipElytra(ItemStack stack) {
        return stack.getItem() != Items.ELYTRA;
    }
}
