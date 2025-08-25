package ru.levinov.modules.impl.Misc;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(
        name = "ElytraFix",
        type = Type.Misc,
        desc = "Не даёт надеть элитру"
)
public class ElytraFix extends Function {
    public static long delay;

    public ElytraFix() {
    }

    public void onEvent(Event event) {
        ItemStack stack = mc.player.inventory.getItemStack();
        if (stack.getItem() instanceof ArmorItem && System.currentTimeMillis() > -1000) {
            ArmorItem ia = (ArmorItem)stack.getItem();
            if (ia.getEquipmentSlot() == EquipmentSlotType.CHEST) {
                ItemStack chestSlot = mc.player.inventory.armorItemInSlot(2);
                if (chestSlot.getItem() == Items.ELYTRA || chestSlot.isEmpty()) {
                    this.handleItemSwap(stack, chestSlot);
                }
            }
        }

    }

    private void handleItemSwap(ItemStack stack, ItemStack chestSlot) {
        mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        int nullSlot = findNullSlot();
        boolean needDrop = nullSlot == 999;
        if (needDrop) {
            nullSlot = this.findEmptyInventorySlot();
        }

        mc.playerController.windowClick(0, nullSlot, 1, ClickType.PICKUP, mc.player);
        delay = 0;
    }

    public static int findNullSlot() {
        for(int i = 0; i < 36; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return 999;
    }

    private int findEmptyInventorySlot() {
        for(int i = 9; i < 36; ++i) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }

        return 9;
    }
}
