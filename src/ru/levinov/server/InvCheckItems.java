package ru.levinov.server;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.optifine.Config;
import ru.levinov.util.misc.HudUtil;

import static ru.levinov.util.render.RenderUtil.Render2D.drawRoundedCorner;

public class InvCheckItems {

    static ItemStack slot = Minecraft.getInstance().player.inventory.getStackInSlot(1);
    public static void items() {
        for(int i = 9; i < 36; ++i) {
            ItemStack slot = Minecraft.getInstance().player.inventory.getStackInSlot(i);
           // System.out.println(slot.getDisplayName().getString() + ", x" + slot.getCount();
            return;
        }
    }
}
