package ru.levinov.modules.impl.Misc;

import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(
        name = "PoseRe",
        type = Type.Misc,
        desc = "Смена позиции"
)
public class PoseRe extends Function {

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            PlayerEntity player = mc.player; // Получаем объект игрока
            player.setPose(Pose.SWIMMING);

        }
    }
}
