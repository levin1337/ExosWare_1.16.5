package ru.levinov.modules.impl.combat;

import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ModeSetting;

import static net.minecraft.command.arguments.EntityArgument.getPlayer;

@FunctionAnnotation(name = "Velocity", type = Type.Combat,desc = "Сопротивление к откидывания",
        keywords = {"Акб"})
public class VelocityFunction extends Function {

    private final ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Grim", "Grim Updated", "Custom","Intave","ReallyWorld");
    private boolean wasOnGround;

    public VelocityFunction() {
        addSettings(mode);
    }

    private int toSkip;
    private int await;

    BlockPos blockPos;

    boolean damaged;

    @Override
    public void onEvent(final Event event) {
        if (mc.player == null || mc.world == null) return;

        if (event instanceof EventPacket e && e.isReceivePacket()) {
            switch (mode.get()) {
                case "Cancel" -> {
                    if (e.getPacket() instanceof SEntityVelocityPacket p) {
                        if (p.getEntityID() != mc.player.getEntityId()) return;

                        e.setCancel(true);
                    }
                }
                case "ReallyWorld" -> {

                }


                case "Grim" -> {
                    if (e.getPacket() instanceof SEntityVelocityPacket p) {
                        if (p.getEntityID() != mc.player.getEntityId() || toSkip < 0) return;

                        toSkip = 2;
                        event.setCancel(true);
                    }

                    if (e.getPacket() instanceof SConfirmTransactionPacket) {
                        if (toSkip < 0) toSkip++;

                        else if (toSkip > 1) {
                            toSkip--;
                            event.setCancel(true);
                        }
                    }

                    if (e.getPacket() instanceof SPlayerPositionLookPacket) toSkip = -8;
                }

                case "Grim Updated" -> {
                    if (e.getPacket() instanceof SEntityVelocityPacket p) {
                        if (p.getEntityID() != mc.player.getEntityId() || await > -5) {
                            return;
                        }

                        await = 2;
                        damaged = true;
                        event.setCancel(true);
                    }
                }
            }
        }

        if (event instanceof EventUpdate ) {
            if (mode.is("Grim Updated")) {
                await--;

                if (damaged) {
                    blockPos = new BlockPos(mc.player.getPositionVec());
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.UP));
                    damaged = false;
                }
            }
            if (mode.is("Custom")) {
                toSkip = 8;
                event.setCancel(true);
                if (mc.player.hurtTime > 0.2f) {
                    blockPos = new BlockPos(mc.player.getPositionVec());
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.UP));
                    damaged = false;
                }
            }
            if (mode.is("Intave")) {
                if (mc.player.hurtTime != 0 && mc.gameSettings.keyBindSneak.isKeyDown()) {
                    int i;
                }

                if (mc.player.hurtTime == 10 && mc.player.isOnGround()) {
                    this.wasOnGround = true;
                }

                if (mc.player.hurtTime == 0) {
                    this.wasOnGround = false;
                }

                if (mc.player.hurtTime == 9 && mc.player.isOnGround()) {
                    mc.player.getMotion().y = 0.0;
                }
            }
        }
    }

    private void reset() {
        toSkip = 0;
        await = 0;
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        reset();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        reset();
    }
}