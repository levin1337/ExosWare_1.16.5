package ru.levinov.modules.impl.movement;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.world.InventoryUtil;

@FunctionAnnotation(name = "ElytraLeave", type = Type.Movement,desc = "Быстрый лив в верх с элитрами")
public class ElytraLeave extends Function {
    private final SliderSetting air = new SliderSetting("Высота", 300f, 150f, 1200f, 50F);

    private final TimerUtil timerUtil = new TimerUtil();
    public ElytraLeave() {
        addSettings(air);
    }
    boolean startFallFlying;
    private float previousPitch;
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            float[] angles = new float[]{mc.player.rotationYaw, -85.0F};
            this.previousPitch = -85.0F;
            e.setYaw(angles[0]);
            e.setPitch(this.previousPitch);
            mc.player.rotationPitch = this.previousPitch;
            mc.player.rotationYaw = angles[0];
            mc.player.renderYawOffset = angles[0];

            int blockHeight = (int) mc.player.getPosY();
            if (blockHeight == air.getValue().intValue()) {
                toggle();
            }
        }
        if (event instanceof EventUpdate eventMotion) {
            if (timerUtil.getTime() > 450) {
                useFirework();
                timerUtil.reset();
            }
        }
        if (event instanceof EventUpdate updated) {
            if (Managment.FUNCTION_MANAGER.elytraBoost.state) {
                Managment.FUNCTION_MANAGER.elytraBoost.toggle();
            }
        }
    }
    private void useFirework() {
        if (InventoryUtil.getItemSlot(Items.FIREWORK_ROCKET) == -1) {
            ClientUtil.sendMesage(TextFormatting.RED + "У вас нету фейерверов!");
        } else {
            InventoryUtil.inventorySwapClick1337(Items.FIREWORK_ROCKET, false);
            timerUtil.reset();
        }
    }

    public void onDisable() {
        super.onDisable();
    }
}