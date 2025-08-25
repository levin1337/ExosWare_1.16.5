package ru.levinov.modules.impl.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CChatMessagePacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;

import java.awt.*;

@FunctionAnnotation(name = "AutoLeave", type = Type.Util,desc = "������ ��� ���������")
public class AutoLeave extends Function {

    private final ModeSetting leavemod = new ModeSetting("���", "�������", "�������", "HoweLand");



    public SliderSetting range = new SliderSetting("���������", 15, 5, 40, 1).setVisible(() -> leavemod.is("�������"));
    public ModeSetting mode = new ModeSetting("��� ������?", "/spawn", "/spawn", "/hub", "kick").setVisible(() -> leavemod.is("�������"));
    public BooleanOption health = new BooleanOption("�� ��������", false).setVisible(() -> leavemod.is("�������"));
    public SliderSetting healthSlider = new SliderSetting("��������", 10, 5, 20, 1).setVisible(() -> health.get());

    public AutoLeave() {
        addSettings(leavemod,range, mode, health, healthSlider);
    }

    @Override
    public void onEvent(Event event) {
        if (leavemod.is("�������")) {
            if (event instanceof EventMotion e) {
                if (health.get()) {
                    if (mc.player.getHealth() <= healthSlider.getValue().floatValue()) {
                        if (mode.is("kick")) {
                            mc.player.connection.getNetworkManager().closeChannel(ClientUtil.gradient("�� ����� � �������! \n" + " ���� ��!", new Color(121, 208, 255).getRGB(), new Color(96, 133, 255).getRGB()));
                        } else {
                            mc.player.connection.sendPacket(new CChatMessagePacket(mode.get()));
                        }
                    }
                    setState(false);
                    return;
                }

                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (player == mc.player) continue;
                    if (player.isBot) continue;
                    if (Managment.FRIEND_MANAGER.isFriend(player.getGameProfile().getName())) {
                        continue;
                    }

                    if (mc.player.getDistance(player) <= range.getValue().floatValue()) {
                        if (mode.is("kick")) {
                            mc.player.connection.getNetworkManager().closeChannel(ClientUtil.gradient("�� ����� � �������! \n" + player.getGameProfile().getName(), new Color(121, 208, 255).getRGB(), new Color(96, 133, 255).getRGB()));
                        } else {
                            mc.player.connection.sendPacket(new CChatMessagePacket(mode.get()));
                        }
                        setState(false);
                        break;
                    }
                }
            }
        }
    }
}
