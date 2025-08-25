package ru.levinov.modules.impl.util;

import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.JoinerUtil;
import ru.levinov.util.SoundUtil;
import ru.levinov.util.misc.TimerUtil;

/**
 * @author levin1337
 * @since 02.07.2023
 */
@FunctionAnnotation(name = "ServerJoiner", type = Type.Util,desc = "Çàõîä íà ñåðâåð")
public class GreifJoinerFunction extends Function {

    public final ModeSetting mode = new ModeSetting("Ìîä", "ReallyWorld", "ReallyWorld", "MoonRise","SunRise");

    private final SliderSetting griefSelection = new SliderSetting("Íîìåð ñëîòà", 1, -1, 100, 1);
    private final TimerUtil timerUtil = new TimerUtil();

    public GreifJoinerFunction() {
        addSettings(griefSelection,mode);
    }

    @Override
    protected void onEnable() {
        if (mode.is("ReallyWorld")) {
       //     JoinerUtil.selectCompass();
         //   mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            super.onEnable();
        }
        if (mode.is("MoonRise")) {
         //   JoinerUtil.selectCompass();
         //   mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            super.onEnable();
        }
        if (mode.is("SunRise")) {
        //    JoinerUtil.selectCompass();
         //   mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            super.onEnable();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (mode.is("ReallyWorld")) {
            if (event instanceof EventUpdate) {
          //      handleEventUpdate();
            }
            if (event instanceof EventPacket eventPacket) {
                if (eventPacket.getPacket() instanceof SJoinGamePacket) {
                    try {
                        if (mc.ingameGUI.getTabList().header == null) {
                            return;
                        }

                        String string = TextFormatting.getTextWithoutFormattingCodes(mc.ingameGUI.getTabList().header.getString());
                        if (!string.contains("Lobby")) {
                            return;
                        }

                        JoinerUtil.selectCompass();
                        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    } catch (Exception var7) {
                    }
                }
            }
        }
        if (mode.is("MoonRise")) {
            if (event instanceof EventUpdate) {
                if (timerUtil.hasTimeElapsed(800)) {
                    mc.playerController.windowClick(mc.player.openContainer.windowId, (int) griefSelection.getValue().floatValue(), 0, ClickType.PICKUP, mc.player);
                    timerUtil.reset();
                    if (mc.ingameGUI.getTabList().header == null) {
                        return;
                    }
                    String string = TextFormatting.getTextWithoutFormattingCodes(mc.ingameGUI.getTabList().header.getString());
                    if (!string.contains("hub")) {
                        toggle();
                    }
                    JoinerUtil.selectCompass();
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                }
            }
        }
        if (mode.is("SunRise")) {
            if (event instanceof EventUpdate) {
                if (timerUtil.hasTimeElapsed(1000)) {
                    mc.playerController.windowClick(mc.player.openContainer.windowId, (int) griefSelection.getValue().floatValue(), 0, ClickType.PICKUP, mc.player);
                    timerUtil.reset();
                    if (mc.ingameGUI.getTabList().header == null) {
                        return;
                    }
                    String string = TextFormatting.getTextWithoutFormattingCodes(mc.ingameGUI.getTabList().header.getString());
                    if (!string.contains("hub")) {
                        toggle();
                    }
                    JoinerUtil.selectCompass();
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                }
            }
        }
    }

    private void handleEventUpdate() {
        if (mc.currentScreen == null) {
            if (mc.player.ticksExisted < 5) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            }
        } else if (mc.currentScreen instanceof ChestScreen) {
            try {
                int numberGrief = this.griefSelection.getValue().intValue();
                ContainerScreen container = (ContainerScreen)mc.currentScreen;

                for(int i = 0; i < container.getContainer().inventorySlots.size(); ++i) {
                    String s = ((Slot) container.getContainer().inventorySlots.get(i)).getStack().getDisplayName().getString();
                    if (ClientUtil.isConnectedToServer("reallyworld") && s.contains("ÃÐÈÔÅÐÑÊÎÅ ÂÛÆÈÂÀÍÈÅ") && this.timerUtil.hasTimeElapsed(50L)) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                        this.timerUtil.reset();
                    }

                    if (s.contains("ÃÐÈÔ #" + numberGrief + " (1.16.5-1.20.4)") && this.timerUtil.hasTimeElapsed(50L)) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                        this.timerUtil.reset();
                    }
                }
            } catch (Exception var5) {
            }
        }
    }
}
