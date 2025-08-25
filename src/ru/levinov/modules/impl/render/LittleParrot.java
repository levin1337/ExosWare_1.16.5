package ru.levinov.modules.impl.render;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(name = "LittleParrot", type = Type.Render)
public class LittleParrot extends Function {
    private ParrotEntity parrot;
    private float wingFlapTimer = 0; // Таймер для махания крыльями

    @Override
    public void onEvent(Event event) {
        // Проверяем, является ли событие игровым событием (например, рендеринг)
        if (event instanceof EventRender eventRender) {
            // Проверяем, есть ли уже попугай
            if (parrot == null) {
                // Создаем попугая и устанавливаем его на плечо игрока
                parrot = new ParrotEntity(EntityType.PARROT, mc.world);
                mc.world.addEntity(parrot); // Добавляем попугая в мир
                parrot.startRiding(mc.player); // "Сажаем" попугая на игрока
            }

            // Обновляем позицию попугая, чтобы он всегда находился над головой игрока
            if (parrot != null) {
                double playerX = mc.player.getPosX();
                double playerY = mc.player.getPosY() + mc.player.getEyeHeight() + 0.6f; // Высота над головой
                double playerZ = mc.player.getPosZ();

                // Получаем вектор направления взгляда игрока
                Vector3d lookVec = mc.player.getLookVec();


                // Устанавливаем позицию попугая немного сбоку и выше
                parrot.setPosition(playerX, playerY, playerZ); // Устанавливаем позицию попугая

                // Обновляем анимацию махания крыльями
           //     updateWingFlap();
            }
        }
    }

    private void updateWingFlap() {
        // Изменяем таймер махания крыльями
        wingFlapTimer += 0.1; // Увеличиваем таймер, можно настроить скорость

        // Устанавливаем состояние махания крыльями в зависимости от таймера
        if (wingFlapTimer >= 1.0) {
            wingFlapTimer = 0; // Сбрасываем таймер
        }

        // Устанавливаем анимацию махания крыльями
        parrot.isFlying(); // Если таймер больше 0.5, попугай "летит"
        parrot.isPartying();
    }
    @Override
    public void onDisable() {
        parrot.remove();
    }
}
