package ru.levinov.modules.impl.Misc;


import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CEntityActionPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.movement.MoveUtil;

@FunctionAnnotation(
        name = "NCPExploit",
        type = Type.Misc,
        desc = "Ёксплоит на флай старый"
)
public class NPCEploit extends Function {
    private final BooleanOption sendpacket = new BooleanOption("ѕакеты", true);
    private final SliderSetting motionX = new SliderSetting("—корость", 1.5F, 0.1F, 10.0F, 0.1F);
    private final SliderSetting motionY = new SliderSetting("motionY", 1.5F, 0.1F, 10.0F, 0.1F);

    public NPCEploit() {
        this.addSettings(new Setting[]{this.sendpacket, this.motionX, this.motionY});
    }

    public void onEvent(Event event) {
        if (event instanceof EventMotion) {
            Vector3d var10000;
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                var10000 = mc.player.motion;
                var10000.y *= (double)this.motionY.getValue().floatValue();
            }

            var10000 = mc.player.motion;
            var10000.x *= (double)this.motionX.getValue().floatValue();
            var10000 = mc.player.motion;
            var10000.z *= (double)this.motionX.getValue().floatValue();
            if (this.sendpacket.get()) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.OPEN_INVENTORY));
            }

            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.START_FALL_FLYING));
            if (MoveUtil.isMoving()) {
                MoveUtil.setSpeed((double)this.motionX.getValue().floatValue());
            }
        }

    }
}