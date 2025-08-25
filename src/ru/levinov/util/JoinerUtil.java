package ru.levinov.util;

import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.world.InventoryUtil;

/**
 * @author levin1337
 * @since 02.07.2023
 */
public class JoinerUtil implements IMinecraft {

    public static void selectCompass() {
        int slot = InventoryUtil.getHotBarSlot(Items.COMPASS);

        if (slot == -1) {
            return;
        }

        mc.player.inventory.currentItem = slot;
        mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
    }

    private static final TimerUtil timerUtil = new TimerUtil();
}
