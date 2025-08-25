package ru.levinov.modules.impl.Misc;

import net.minecraft.client.gui.screen.HopperScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.JoinerUtil;
import ru.levinov.util.misc.TimerUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FunctionAnnotation(
        name = "RCT",
        type = Type.Util,
        desc = "Перезаход на сервер"
)
public class RCT extends Function {
    private final TimerUtil timerUtil = new TimerUtil();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RCT() {
        this.addSettings();
    }

    protected void onEnable() {
        this.scheduler.schedule(() -> {
            mc.player.sendChatMessage("/hub");
        }, 1L, TimeUnit.MILLISECONDS);
        JoinerUtil.selectCompass();
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        super.onEnable();
    }

    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            this.handleEventUpdate();
        }

    }

    private void handleEventUpdate() {
        JoinerUtil.selectCompass();
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        if (mc.currentScreen == null) {
            if (mc.player.ticksExisted < 5) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            }
        } else if (mc.currentScreen instanceof HopperScreen) {
            try {
                ContainerScreen container = (ContainerScreen)mc.currentScreen;

                for(int i = 0; i < container.getContainer().inventorySlots.size(); ++i) {
                    ItemStack stack = ((Slot)container.getContainer().inventorySlots.get(i)).getStack();
                    if (stack.getItem() == Items.PLAYER_HEAD || stack.getItem() == Items.WITHER_SKELETON_SKULL || stack.getItem() == Items.ZOMBIE_HEAD) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                    }

                    this.timerUtil.reset();
                }

                this.toggle();
            } catch (Exception var4) {
            }
        }

    }
}