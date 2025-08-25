package ru.levinov.modules.impl.movement;

import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.game.EventMouseTick;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

@FunctionAnnotation(name = "Click TP", type = Type.Movement,desc = "Телепорт по клику")
public class ClickTP extends Function {

    private BlockRayTraceResult result;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMouseTick e) {
            if (e.getButton() == 2 && result != null) {
                Vector3d vec = Vector3d.copyCenteredHorizontally(result.getPos().up());
                double x = vec.x;
                double y = vec.y;
                double z = vec.z;
                int i;
                for(i = 0; i < 10; ++i) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
                }

                for(i = 0; i < 10; ++i) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
                }

                mc.player.setPosition(x, y, z);
            }
        }

        if (event instanceof EventRender) {
            result = (BlockRayTraceResult) mc.player.pick(100, 1, false);
            if (result.getType() == RayTraceResult.Type.MISS) result = null;
            if (result != null)
                RenderUtil.Render3D.drawBlockBox(result.getPos(), ColorUtil.rgba(128,255,128,255));
        }
    }
}
