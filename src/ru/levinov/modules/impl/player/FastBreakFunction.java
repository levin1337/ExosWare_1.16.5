package ru.levinov.modules.impl.player;

import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
/**
 * @author levin1337
 * @since 04.06.2023
 */
@FunctionAnnotation(name = "FastBreak", type = Type.Player,desc = "Быстрое ломание")
public class FastBreakFunction extends Function {

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            // Сбрасываем задержку удара блока для игрока
            mc.playerController.blockHitDelay = 0;

            // Проверяем, превышает ли текущий урон блока значение 1.0F
            if (mc.playerController.curBlockDamageMP > 1.0F) {
                // Если превышает, устанавливаем значение урона блока равным 1.0F
                mc.playerController.curBlockDamageMP = 1.0F;
            }
        }
    }
}
