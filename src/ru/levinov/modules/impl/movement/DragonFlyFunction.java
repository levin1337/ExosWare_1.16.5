package ru.levinov.modules.impl.movement;

import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMove;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.movement.MoveUtil;

/**
 * @author levin1337
 * @since 27.06.2023
 */
@FunctionAnnotation(name = "KingFly", type = Type.Movement,desc = "Быстрое летание во /fly")
public class DragonFlyFunction extends Function {
    private final SliderSetting dragonFlySpeed = new SliderSetting("Скорость флая", 1.6f, 1.0f, 10.0F, 0.01f);
    private final SliderSetting dragonFlyMotionY = new SliderSetting("Скорость флая по Y", 0.6f, 0.1f, 5, 0.01f);

    public DragonFlyFunction() {
        addSettings(dragonFlySpeed,dragonFlyMotionY);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMove move) {
            handleDragonFly(move);
        }
    }

    /**
     * Обработка движения при /fly
     *
     * @param move Обработчик EventMove
     */
    private void handleDragonFly(EventMove move) {
        if (mc.player.abilities.isFlying) {

            if (!mc.player.isSneaking() && mc.gameSettings.keyBindJump.isKeyDown()) {
                move.motion().y = dragonFlyMotionY.getValue().floatValue();
            }
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                move.motion().y = -dragonFlyMotionY.getValue().floatValue();
            }

            MoveUtil.MoveEvent.setMoveMotion(move, dragonFlySpeed.getValue().floatValue());
        }
    }
}
