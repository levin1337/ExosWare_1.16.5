package ru.levinov.modules.impl.util;

import com.google.common.eventbus.Subscribe;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.events.impl.player.EventWorldChange;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@FunctionAnnotation(name = "Disabler", type = Type.Movement)
public class Disabler extends Function {

    private final ModeSetting settings = new ModeSetting("Настройки", "FallFlying", new String[]{
            "FallFlying", "C03PacketPlayer", "GrimSpectate", "Sloth", "Verus", "Verus2", "Timer", "Ladder", "KeepAlive", "Transaction", "MatrixTimer", "VulcanCombat", "TestDisabler", "VulcanMovement", "MatrixFlyV6.0.1", "NCPFlyV3.13", "IntaveBypassV14.1", "GrimMovementBypass", "GrimCombatBypassV1.0", "VulcanMovementV3.4", "VulcanCombatV3.4", "MatrixFlyBypass", "NCPFlyBypass", "IntaveFlyBypass", "SwatHVH"});


    private final Queue<IPacket> packetQueue = new ConcurrentLinkedQueue<>();
    private final TimerUtil timer = new TimerUtil();
    private boolean shouldDelay = false;
    private int vulTickCounterUID = 0;
    private final LinkedBlockingQueue<IPacket<?>> packets = new LinkedBlockingQueue<>();

    public Disabler() {
        addSettings(settings);
    }

    @Override
    public void onEvent(Event var1) {
        if (var1 instanceof EventUpdate e) {
            if (settings.is("VulcanMovementV3.4")) {
                if (mc.player.ticksExisted % 10 == 0) {
                    mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), Direction.UP));
                    mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ()), Direction.UP));
                }
            }
            if (settings.is("GrimMovementBypass")) {
                mc.player.setSprinting(false);
                if (mc.player.ticksExisted % 5 == 0) {
                    mc.getConnection().sendPacket(new CPlayerPacket(true));
                }
            }
            if (settings.is("MatrixFlyBypass")) {
                if (mc.player.ticksExisted % 15 == 0) {
                    mc.player.connection.sendPacket(new CPlayerPacket(true));
                    mc.player.setVelocity(0, 0.42, 0);
                }
            }
            if (settings.is("NCPFlyBypass")) {
                if (mc.player.isOnGround()) {
                    mc.player.jump();
                }
                mc.player.setVelocity(0, 0.42, 0);
            }
            if (settings.is("IntaveFlyBypass")) {
                if (mc.player.ticksExisted % 10 == 0) {
                    mc.getConnection().sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.1, mc.player.getPosZ(), true));
                }
            }
            if (settings.is("SwatHVH")) {
                mc.player.sendChatMessage("/Crashsystemsosigrib");
                ClientUtil.sendMesage("Анти чит был отключён приятной игры.");
                toggle();
            }
        }
    }

    @Subscribe
    public void onWorldChange(EventWorldChange e) {
        if (settings.is("VulcanCombatV3.4")) {
            packetQueue.clear();
            timer.reset();
            vulTickCounterUID = -25767;
        }
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (settings.is("VulcanCombatV3.4")) {
            if (timer.hasTimeElapsed(5000L) && packetQueue.size() > 4) {
                timer.reset();
                while (packetQueue.size() > 4) {
                    mc.player.connection.sendPacket(packetQueue.poll());
                }
            }
        }
        if (settings.is("GrimCombatBypassV1.0")) {
            if (mc.player.ticksExisted % 20 == 0) {
                mc.player.connection.sendPacket(new CKeepAlivePacket(12345L));
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        vulTickCounterUID = -25767;
        shouldDelay = false;
        timer.reset();
    }

    @Override
    public void onDisable() {
        while (!packets.isEmpty()) {
            Iterator<IPacket<?>> packetIterator = this.packets.iterator();
            while (packetIterator.hasNext()) {
                mc.player.connection.sendPacket(packetIterator.next());
                packetIterator.remove();
            }
        }
        packetQueue.clear();
        super.onDisable();
    }

    @Subscribe
    public void onPacket(EventPacket event) {
        if (settings.is("GrimMovementBypass")) {
            if (event.getPacket() instanceof SPlayerPositionLookPacket) {
                SPlayerPositionLookPacket playerPosLook = (SPlayerPositionLookPacket) event.getPacket();
                playerPosLook.yaw += 1.0E-4;
            }
        }
        if (settings.is("GrimCombatBypassV1.0")) {
            IPacket packet = event.getPacket();
            if (packet instanceof CConfirmTransactionPacket && shouldDelay) {
                event.setCancel(true);
                packets.add(packet);
            }
        }
    }
}