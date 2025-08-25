package ru.levinov.modules.impl.combat;


import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.world.InventoryUtil;

@FunctionAnnotation(
        name = "Criticals",
        type = Type.Combat,
        desc = "Критический удар без прыжка"
)
public class Criticals extends Function {
    private final TimerUtil timerUtil = new TimerUtil();
    public final ModeSetting mode = new ModeSetting("Мод", "OldNCP", "OldNCP", "Ncp", "UpdatedNCP", "Strict","Default","Elytra");
    public Criticals() {
        addSettings(mode);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onEvent(Event event) {

    }


    static void packet(double yDelta) {
        mc.getConnection().sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + yDelta, mc.player.getPosZ(), true));
        mc.getConnection().sendPacket(new CPlayerPacket.PositionRotationPacket(mc.player.getPosX(), mc.player.getPosY() + yDelta, mc.player.getPosZ(), mc.player.rotationYaw, mc.player.rotationPitch, false));
    }


    public static void doCrit(double y){
        if (mc.player == null || mc.world == null)
        return;
        if ((mc.player.isOnGround() || mc.player.abilities.isFlying ||  mc.player.isPushedByWater() || !mc.player.isInLava() && !mc.player.isInWater()))
        {
            mc.getConnection().sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + y, mc.player.getPosZ(), false));
            mc.getConnection().sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
        }
    }
    public void onDisable() {
        super.onDisable();
    }
}