package ru.levinov.modules.impl.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.events.Event;
import ru.levinov.events.impl.game.EventKey;
import ru.levinov.events.impl.player.EventInput;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BindSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;

import java.lang.reflect.Field;
import java.util.Map;

@FunctionAnnotation(name = "KTLeaveFT", type = Type.Util)
public class KTLeaveFT extends Function implements Runnable {

    private final TimerUtil stopWatch = new TimerUtil();
    private final TimerUtil warpDelay = new TimerUtil();
    private boolean clickingSlot13 = false;
    private boolean slot21Clicked = false;
    private boolean menuClosed = false;
    private Thread updateThread;

    @Override
    public void onEnable() {
        resetState();
        sendWarpCommand();
        warpDelay.reset();

        // Запус
        updateThread = new Thread(this);
        updateThread.start();
        super.onEnable();
    }

    private BindSetting key = new BindSetting("Кнопка лива", 0);

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventKey e) {
            if (e.key == key.getKey()) {
                clickSlot(24);
            }
        }
        if (event instanceof EventInput e) {
            if (mc.player.fallDistance > 0.2) {

            } else {
                if (mc.player.isOnGround()) {
                    float slowdownFactor = 0.2f; // Измените это значение для настройки уровня замедления
                    e.setForward(e.getForward() * slowdownFactor);
                    e.setStrafe(e.getStrafe() * slowdownFactor);
                } else {
                }
            }
        }

    }
    public KTLeaveFT() {
        addSettings(key);
    }

    @Override
    public void onDisable() {
        resetState();
        if (updateThread != null && updateThread.isAlive()) {
            updateThread.interrupt();
        }
        super.onDisable();
    }

    private void sendWarpCommand() {
        mc.player.sendChatMessage("/darena");
        mc.mouseHelper.grabMouse();
        menuClosed = false;
    }

    private void clickSlot(int slotIndex) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.openContainer != null && mc.player.openContainer.getSlot(slotIndex) != null) {
            mc.playerController.windowClick(mc.player.openContainer.windowId, slotIndex, 0, ClickType.QUICK_MOVE, mc.player);
        } else {
        }
    }

    private void forceCloseMenu() {
        Minecraft mc = Minecraft.getInstance();
        mc.displayGuiScreen(null);
        mc.mouseHelper.ungrabMouse();
        menuClosed = true;
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        ClientUtil.sendMesage(TextFormatting.WHITE + "KTLeave activated!");
    }

    private void startClickingSlot13() {
        clickingSlot13 = true;
    }

    private void resetState() {
        clickingSlot13 = false;
        slot21Clicked = false;
        menuClosed = false;
        stopWatch.reset();
        warpDelay.reset();

    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(10);

                if (!menuClosed && warpDelay.hasTimeElapsed(500)) {
                    forceCloseMenu();
                }

                if (!clickingSlot13) {
                    startClickingSlot13();
                }
                if (clickingSlot13 && stopWatch.hasTimeElapsed(10)) {
                    clickSlot(2);
                    stopWatch.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
