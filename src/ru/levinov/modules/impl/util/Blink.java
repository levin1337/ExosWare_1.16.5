package ru.levinov.modules.impl.util;


import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

@FunctionAnnotation(name = "Blink", type = Type.Util,desc = "�������� ���������� ���������")
public final class Blink extends Function {

    private final CopyOnWriteArrayList<IPacket> packets = new CopyOnWriteArrayList<>();
    private BooleanOption delay = new BooleanOption("���������", false);
    private SliderSetting delayS = new SliderSetting("��������", 100, 50, 1000, 50).setVisible(() -> delay.get());


    public Blink() {
        super();
        addSettings(delay, delayS);
    }

    private long started;

    @Override
    protected void onEnable() {
        super.onEnable();
        started = System.currentTimeMillis();
        lastPos = mc.player.getPositionVec();
    }

    float animation;

    public TimerUtil timerUtil = new TimerUtil();

    Vector3d lastPos = new Vector3d(0, 0, 0);

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {
            if (e.isRender3D()) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                {
                    RenderUtil.Render3D.drawBox(AxisAlignedBB.fromVector(lastPos).expand(0, 1, 0).offset(-mc.getRenderManager().info.getProjectedView().x, -mc.getRenderManager().info.getProjectedView().y, -mc.getRenderManager().info.getProjectedView().z).offset(-0.5f, 0, -0.5f).grow(-0.2, 0, -0.2), -1);
                }
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            if (e.isRender2D()) {
                float width = 120;
                float height = 10;
                float state = 1 - (System.currentTimeMillis() - started) / 30000F;

                animation = AnimationMath.lerp(animation, width * state, 10);

                RenderUtil.Render2D.drawShadow((e.scaledResolution.getScaledWidth() / 2f) - (width / 2f), 25, width, height, 10, new Color(23, 23, 23, 200).getRGB());
                RenderUtil.Render2D.drawRect((e.scaledResolution.getScaledWidth() / 2f) - (width / 2f), 25, width, height, new Color(23, 23, 23, 200).getRGB());
                RenderUtil.Render2D.drawShadow((e.scaledResolution.getScaledWidth() / 2f) - (width / 2f), 25, animation, height, 10, ColorUtil.getColorStyle(10));
                RenderUtil.Render2D.drawRect((e.scaledResolution.getScaledWidth() / 2f) - (width / 2f), 25, animation, height, ColorUtil.getColorStyle(10));

                Fonts.gilroyBold[14].drawCenteredString(e.matrixStack, "Blink", (e.scaledResolution.getScaledWidth() / 2f), 5 + height, -1);

            }
        }

        if (event instanceof EventPacket e) {
            if (mc.player != null && mc.world != null && !mc.isSingleplayer() && !mc.player.getShouldBeDead()) {
                if (e.isSendPacket()) {
                    packets.add(e.getPacket());
                    e.setCancel(true);
                }
            } else toggle();
        }

        if (event instanceof EventMotion e) {
            if ((System.currentTimeMillis() - started) >= 29900) {
                toggle();
            }
            if (delay.get() && timerUtil.hasTimeElapsed(delayS.getValue().longValue())) {
                for (IPacket packet : packets) {
                    
                    mc.player.connection.getNetworkManager().sendPacketWithoutEvent(packet);
                }
                packets.clear();
                started = System.currentTimeMillis();
                timerUtil.reset();
            }
        }


    }


    @Override
    public void onDisable() {
        super.onDisable();
        for (IPacket packet : packets) {
            mc.player.connection.sendPacket(packet);
        }

        packets.clear();
    }
}