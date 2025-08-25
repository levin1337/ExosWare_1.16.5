package ru.levinov.modules.impl.movement;

import net.minecraft.inventory.container.ClickType;
import net.minecraft.network.play.client.CEntityActionPacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.world.InventoryUtil;

@FunctionAnnotation(name = "HighJump", type = Type.Movement,desc = "Прыжок в небо")
public class HighJump extends Function {
    private final SliderSetting y = new SliderSetting("Высота", 0.61F, 0.35F, 1.0F, 0.1F);

    public HighJump() {
        addSettings(y);
    }

    private static long lastStartFalling;
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion) {
            int elytra = InventoryUtil.findInventoryElytra();
            if (elytra == -1) {
                toggle();
            }

            if (mc.gameSettings.keyBindJump.pressed && mc.player.fallDistance == 0.0F && mc.player.ticksExisted % 2 == 0) {
                matrixpuzo(elytra);
                mc.player.addVelocity(0.0, y.getValue().floatValue(), 0.0);
            }
        }
    }

    public static void matrixpuzo(int elytra) {
        elytra = elytra >= 0 && elytra < 9 ? elytra + 36 : elytra;
        if (elytra != -2) {
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        }
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        if (elytra != -2) {
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
        }

        lastStartFalling = System.currentTimeMillis();
    }
}
