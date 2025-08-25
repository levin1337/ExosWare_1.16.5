package ru.levinov.util.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class RotationUtil {
    public static Vector2f getDeltaForCoord(Vector2f rot, Vector3d point) {
        PlayerEntity client = Minecraft.getInstance().player;
        double x = point.x - client.getPosX();
        double y = point.y - client.getEyePosition((float)1.0f).y;
        double z = point.z - client.getPosZ();
        double dst = Math.sqrt(Math.pow(x, 2.0) + Math.pow(z, 2.0));
        float yawToTarget = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90.0);
        float pitchToTarget = (float)(-Math.toDegrees(Math.atan2(y, dst)));
        float yawDelta = MathHelper.wrapDegrees(yawToTarget - rot.x);
        float pitchDelta = pitchToTarget - rot.y;
        return new Vector2f(yawDelta, pitchDelta);
    }
    public static Vector2f getRotationForCoord(Vector3d point) {
        PlayerEntity client = Minecraft.getInstance().player;
        double x = point.x - client.getPosX();
        double y = point.y - client.getEyePosition((float)1.0f).y;
        double z = point.z - client.getPosZ();
        double dst = Math.sqrt(Math.pow(x, 2.0) + Math.pow(z, 2.0));
        float yawToTarget = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90.0);
        float pitchToTarget = (float)(-Math.toDegrees(Math.atan2(y, dst)));
        return new Vector2f(yawToTarget, pitchToTarget);
    }
}
