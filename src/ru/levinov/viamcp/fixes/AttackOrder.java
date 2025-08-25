package ru.levinov.viamcp.fixes;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import ru.levinov.viamcp.ViaLoadingBase;

public class AttackOrder implements ru.levinov.util.IMinecraft {

    public static void sendConditionalSwing(RayTraceResult rayTraceResult, Hand hand) {
        if (rayTraceResult != null && rayTraceResult.getType() != RayTraceResult.Type.ENTITY) mc.player.swingArm(hand);
    }

    public static void sendFixedAttack(PlayerEntity entityIn, Entity target, Hand hand) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            mc.player.swingArm(hand);
            mc.playerController.attackEntity(entityIn, target);
        } else {
            mc.playerController.attackEntity(entityIn, target);
            mc.player.swingArm(hand);
        }
    }
}