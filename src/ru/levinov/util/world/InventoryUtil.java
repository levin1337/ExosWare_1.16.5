//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.levinov.util.world;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import net.java.games.input.Component;
import net.minecraft.block.FenceBlock;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CEntityActionPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;
import ru.levinov.events.impl.player.EventWindowClick;
import ru.levinov.events.impl.player.EventWindowClick.ClickStage;
import ru.levinov.managment.Managment;
import ru.levinov.modules.impl.combat.Aura;
import ru.levinov.util.IMinecraft;

public class InventoryUtil implements IMinecraft {
    public InventoryUtil() {
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
    public static int getHotBarSlot(Item input) {
        for(int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == input) {
                return i;
            }
        }

        return -1;
    }


    public static int getFireWorks() {
        for(int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof FireworkRocketItem) {
                return i;
            }
        }

        return -1;
    }

    public static int getTrident() {
        for(int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof TridentItem) {
                return i;
            }
        }

        return -1;
    }

    public static int findInventoryElytra() {
        for(int i = 9; i < 36; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA) {
                return i;
            }
        }

        return -1;
    }

    public static int findInventoryChestplate() {
        for(int i = 9; i < 36; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == Items.DIAMOND_CHESTPLATE) {
                return i;
            }
        }

        return -1;
    }

    public static int getItem(Item item, boolean hotbar) {
        for(int i = 0; i < (hotbar ? 9 : 45); ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    public static int getSlotInHotBar(Item item) {
        for(int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    public static int getItemSlot(Item input) {
        Iterator var1 = mc.player.getArmorInventoryList().iterator();

        while(var1.hasNext()) {
            ItemStack stack = (ItemStack)var1.next();
            if (stack.getItem() == input) {
                return -2;
            }
        }

        int slot = -1;

        for(int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == input) {
                slot = i;
                break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }

        return slot;
    }

    public static int getItemSlot(ItemStack input) {
        Iterator var1 = mc.player.getArmorInventoryList().iterator();

        while(var1.hasNext()) {
            ItemStack stack = (ItemStack)var1.next();
            if (stack == input) {
                return -2;
            }
        }

        int slot = -1;

        for(int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s == input) {
                slot = i;
                break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }

        return slot;
    }

    public static void handleItemTransfer() {
        int emptySlot;
        if (mc.player.inventory.getItemStack().getItem() != Items.AIR && (emptySlot = findEmptySlot(false)) != -1) {
            mc.playerController.windowClick(0, emptySlot, 0, ClickType.PICKUP, mc.player);
        }

        mc.player.closeScreen();
    }

    public static void inventorySwapClick1337(Item item, boolean rotation) {
        if (InventoryHelper.getItemIndex(item) != -1) {
            Aura aura;
            int i;
            if (doesHotbarHaveItem(item)) {
                for(i = 0; i < 9; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                        if (i != mc.player.inventory.currentItem) {
                            mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                        }

                        if (rotation) {
                            aura = Managment.FUNCTION_MANAGER.auraFunction;
                            if (Aura.target != null) {
                                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(mc.player.rotationYaw, mc.player.rotationPitch, false));
                            }
                        }

                        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        if (i != mc.player.inventory.currentItem) {
                            mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                        }
                        break;
                    }
                }
            }

            if (!doesHotbarHaveItem(item)) {
                for(i = 0; i < 36; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                        mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem % 8 + 1));
                        if (rotation) {
                            if (Aura.target != null) {
                                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(mc.player.rotationYaw, mc.player.rotationPitch, false));
                            }
                        }

                        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                        mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        break;
                    }
                }
            }
        }

    }


    public static void inventorySwapClick(Item item) {
        if (doesHotbarHaveItem(item)) {
            for(int i = 0; i < 9; ++i) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                    boolean levin1337 = false;
                    if (i != mc.player.inventory.currentItem) {
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                        levin1337 = true;
                    }

                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    if (levin1337) {
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    }
                    break;
                }
            }
        }

    }

    public static boolean doesHotbarHaveItem(Item item) {
        for(int i = 0; i < 9; ++i) {
            mc.player.inventory.getStackInSlot(i);
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static void handleClick(EventWindowClick windowClick) {
        boolean isSneaking = mc.player.isSneaking();
        if (windowClick.getClickStage() == ClickStage.PRE) {
            mc.player.setSprinting(false);
            if (!isSneaking) {
                return;
            }

            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.RELEASE_SHIFT_KEY));
        }

        if (windowClick.getClickStage() == ClickStage.POST) {
            if (!isSneaking) {
                return;
            }

            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.PRESS_SHIFT_KEY));
        }

    }

    public static int findEmptySlot(boolean isStartingFromZero) {
        int start = isStartingFromZero ? 0 : 9;
        int end = isStartingFromZero ? 9 : 45;

        for(int i = start; i < end; ++i) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public static void moveItem(int from, int to, boolean air) {
        if (from != to) {
            pickupItem(from, 0);
            pickupItem(to, 0);
            if (air) {
                pickupItem(from, 0);
            }

        }
    }

    public static void pickupItem(int slot, int button) {
        mc.playerController.windowClick(0, slot, button, ClickType.PICKUP, mc.player);
    }

    public static void dropItem(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.THROW, mc.player);
    }

    public static int getAxe(boolean hotBar) {
        int startSlot = hotBar ? 0 : 9;
        int endSlot = hotBar ? 9 : 36;

        for(int i = startSlot; i < endSlot; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof AxeItem) {
                return i;
            }
        }

        return -1;
    }

    public static int getFireworkHotbar(boolean hotBar) {
        int startSlot = hotBar ? 0 : 9;
        int endSlot = hotBar ? 9 : 36;

        for(int i = startSlot; i < endSlot; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof FireworkRocketItem) {
                return i;
            }
        }

        return -1;
    }

    public static int getperls(boolean hotBar) {
        int startSlot = hotBar ? 0 : 9;
        int endSlot = hotBar ? 9 : 36;

        for(int i = startSlot; i < endSlot; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof EnderPearlItem) {
                return i;
            }
        }

        return -1;
    }

    public static int getPearls() {
        for(int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof EnderPearlItem) {
                return i;
            }
        }

        return -1;
    }


    public static Slot getInventorySlot(Item item) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().equals(item) && s.slotNumber >= mc.player.openContainer.inventorySlots.size() - 36).findFirst().orElse(null);
    }

    public static Slot getInventorySlot(List<Item> item) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> item.contains(s.getStack().getItem()) && s.slotNumber >= mc.player.openContainer.inventorySlots.size() - 36).findFirst().orElse(null);
    }


    public static int getInventoryCount(Item item) {
        return IntStream.range(0, 45).filter(i -> mc.player.inventory.getStackInSlot(i).getItem().equals(item)).map(i -> mc.player.inventory.getStackInSlot(i).getCount()).sum();
    }

    public static void clickSlot(Slot slot, int button, ClickType clickType, boolean packet) {
        if (slot != null) clickSlotId(slot.slotNumber, button, clickType, packet);
    }

    public static void clickSlotId(int slot, int button, ClickType clickType, boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(new CClickWindowPacket(mc.player.openContainer.windowId, slot, button, clickType, ItemStack.EMPTY, mc.player.openContainer.getNextTransactionID(mc.player.inventory)));
        } else {
            mc.playerController.windowClick(mc.player.openContainer.windowId, slot, button, clickType, mc.player);
        }
    }

    public static int getPrice(ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        if (tag == null) return -1;
        String price = StringUtils.substringBetween(tag.toString(), "\"text\":\" $", "\"}]");
        if (price == null || price.isEmpty()) return -1;
        price = price.replaceAll(" ", "").replaceAll(",", "");
        return Integer.parseInt(price);
    }

    public static Slot getSlotFoodMaxSaturation() {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().getFood() != null && !s.getStack().getItem().getFood().canEatWhenFull()).max(Comparator.comparingDouble(s -> s.getStack().getItem().getFood().getSaturation())).orElse(null);
    }
}
