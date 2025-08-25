package ru.levinov.command.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.joml.Vector4i;
import ru.levinov.command.Cmd;
import ru.levinov.command.Command;
import ru.levinov.managment.Managment;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.awt.*;

import static ru.levinov.util.IMinecraft.mc;
import static ru.levinov.util.render.RenderUtil.Render2D.drawShadow;
import static ru.levinov.util.render.RenderUtil.Render2D.drawTriangle;

@Cmd(name = "gps", description = "Прокладывает путь до координат")
public class GPSCommand extends Command {

    public static boolean enabled;

    public static Vector3d vector3d;

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("off")) {
                ClientUtil.sendMesage(TextFormatting.GRAY + "Навигатор выключен!");

                enabled = false;
                vector3d = null;
                return;
            }
            if (args.length == 3) {
                int x = Integer.parseInt(args[1]), y = Integer.parseInt(args[2]);
                enabled = true;
                vector3d = new Vector3d(x, 0, y);
                ClientUtil.sendMesage(TextFormatting.GRAY + "Навигатор включен! Координаты " + x + ";" + y);
            }
        } else {
            error();
        }
    }

    public static void drawArrow(MatrixStack stack) {
        if (!enabled)
            return;

        double x = vector3d.x - mc.getRenderManager().info.getProjectedView().getX();
        double z = vector3d.z - mc.getRenderManager().info.getProjectedView().getZ();

        double cos = MathHelper.cos(mc.player.rotationYaw * (Math.PI * 2 / 360));
        double sin = MathHelper.sin(mc.player.rotationYaw * (Math.PI * 2 / 360));
        double rotY = -(z * cos - x * sin);
        double rotX = -(x * cos + z * sin);
        double dst = Math.sqrt(Math.pow(vector3d.x - mc.player.getPosX(), 2) + Math.pow(vector3d.z - mc.player.getPosZ(), 2));

        float angle = (float) (Math.atan2(rotY, rotX) * 180 / Math.PI);
        double x2 = 75 * MathHelper.cos(Math.toRadians(angle)) + mc.getMainWindow().getScaledWidth() / 2f;
        double y2 = 75 * (mc.player.rotationPitch / 90) * MathHelper.sin(Math.toRadians(angle)) + mc.getMainWindow().getScaledHeight() / 2f;

        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.translated(x2, y2, 0);
        GlStateManager.rotatef(angle, 0, 0, 1);
        final int hud_color = new Color(0, 0, 0, 90).getRGB();


        int clr = Managment.STYLE_MANAGER.getCurrentStyle().getColor(100);

        drawShadow(-3F, -3F, 8, 6F, 8, clr);
       // drawTriangle(-4, -5F, 4F, 7F, new Color(0, 0, 0, 32));
        drawTriangle(-3F, - 5F, 3F, 5F, new Color(clr));
        GlStateManager.rotatef(90, 0, 0, 1);

        float epsilon = 0.0001f; // Допустимая погрешность

        if (Math.abs(dst - 0.1f) < epsilon) {
            RenderUtil.Render2D.drawRoundedCorner(-22,-5,45,25,4,hud_color);
            Fonts.durman[12].drawCenteredStringWithOutline(stack, "Вы на месте", 0, 15, -1);

        } else if (dst > 10) {
            RenderUtil.Render2D.drawRoundedCorner(-15,-5,30,25,6,hud_color);
            Fonts.durman[12].drawCenteredStringWithOutline(stack, (int) dst + " M", 0, 15, -1);
        } else {
            RenderUtil.Render2D.drawRoundedCorner(-22,-5,45,25,4,hud_color);
            Fonts.durman[12].drawCenteredStringWithOutline(stack, "Вы на месте", 0, 15, -1);
        }

        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "Ошибка в использовании" + TextFormatting.WHITE + ":");
        sendMessage(TextFormatting.WHITE + ".gps " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "x, z" + TextFormatting.GRAY + ">");
        sendMessage(TextFormatting.WHITE + ".gps " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "off" + TextFormatting.GRAY + ">");
    }
}
