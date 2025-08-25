package ru.levinov.modules.impl.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;

import java.lang.reflect.Field;
import java.util.Map;

@FunctionAnnotation(name = "GodMode", type = Type.Util)
public class GodMode extends Function implements Runnable {

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

    @Override
    public void onEvent(Event event) {

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
        mc.player.sendChatMessage("/warp");
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


        ClientUtil.sendMesage(TextFormatting.WHITE + "GodMod активирован!");
    }



    private void startClickingSlot13() {
        clickingSlot13 = true;

    }

    private void stopClickingSlot13() {
        clickingSlot13 = false;

    }

    private void resetState() {
        clickingSlot13 = false;
        slot21Clicked = false;
        menuClosed = false;
        stopWatch.reset();
        warpDelay.reset();

    }

    private BossOverlayGui getBossOverlayGui() {
        try {
            Minecraft mc = Minecraft.getInstance();
            return mc.ingameGUI.getBossOverlay();
        } catch (Exception e) {

            return null;
        }
    }

    private boolean isPvpBossBarActive() {
        BossOverlayGui bossOverlayGui = getBossOverlayGui();
        if (bossOverlayGui == null) {

            return false;
        }

        Map<?, ClientBossInfo> bossBars;

        try {

            Field bossInfosField = BossOverlayGui.class.getDeclaredField("mapBossInfos");
            bossInfosField.setAccessible(true);
            bossBars = (Map<?, ClientBossInfo>) bossInfosField.get(bossOverlayGui);

            for (ClientBossInfo bossInfo : bossBars.values()) {
                String bossName = bossInfo.getName().getString();

                if (bossName.contains("Режим ПВП") || bossName.contains("PVP")) {

                    return true;
                }
            }
        } catch (Exception e) {

        }


        return false;
    }



    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(50);

                if (!menuClosed && warpDelay.hasTimeElapsed(1000)) {
                    forceCloseMenu();
                }

                if (warpDelay.hasTimeElapsed(500) && !slot21Clicked) {
                    clickSlot(21);
                    slot21Clicked = true;
                }

                if (isPvpBossBarActive()) {
                    if (!clickingSlot13) {
                        startClickingSlot13();
                    }
                } else {
                    if (clickingSlot13) {
                        stopClickingSlot13();
                    }
                }

                if (clickingSlot13 && stopWatch.hasTimeElapsed(5)) {
                    clickSlot(13);
                    stopWatch.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
