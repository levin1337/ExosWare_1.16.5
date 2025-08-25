package ru.levinov.modules.impl.combat;

import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ColorSetting;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;

/**
 * @author levin1337
 * @since 07.06.2023
 */
@FunctionAnnotation(name = "Hitbох", type = Type.Combat,desc = "Увеличение хит-бокса Энтити",
        keywords = {"ХитБокс","Hitbox"})
public class Xbox extends Function {

    public final SliderSetting size = new SliderSetting("Размер", 0.2f, 0.f, 3.5f, 0.05f);
    public final BooleanOption invisible = new BooleanOption("Невидимые", false);

    public final BooleanOption colorHit = new BooleanOption("Цвет", true);

    public final ModeSetting mode = new ModeSetting("Выбор цвета", "Тематический","Тематический", "Свой").setVisible(() -> colorHit.get());
    public ColorSetting color = new ColorSetting("Цвет хит-бокса", -1).setVisible(() -> mode.is("Свой"));
    public final SliderSetting alpha = new SliderSetting("Прозрачность", 1f, 1f, 255f, 1f);

    public Xbox() {
        addSettings(size, invisible,colorHit,mode,color,alpha);
    }

    @Override
    public void onEvent(final Event event) {
        handleEvent(event);
    }

    /**
     * Обрабатываем событие.
     */
    private void handleEvent(Event event) {
        // Проверка, является ли событие типом EventRender и включено ли 3D-отображение
        if (!(event instanceof EventRender && ((EventRender) event).isRender3D()))
            return;

        // Проверка, включен ли режим невидимости
        if (invisible.get())
            return;

        // Выполнение корректировки границ хитбокса для игроков
        adjustBoundingBoxesForPlayers();
    }

    /**
     * Настраиваем хитбокс игрока под кастомный размер.
     */
    private void adjustBoundingBoxesForPlayers() {
        // Перебор всех игроков в мире
        for (PlayerEntity player : mc.world.getPlayers()) {
            // Проверка, нужно ли пропустить данного игрока при корректировке хитбокса
            if (shouldSkipPlayer(player))
                continue;

            // Вычисление множителя размера и установка нового хитбокса для игрока
            float sizeMultiplier = this.size.getValue().floatValue() * 2.5F;
            setBoundingBox(player, sizeMultiplier);
        }
    }

    /**
     * Проверка на валидного игрока
     */
    private boolean shouldSkipPlayer(PlayerEntity player) {
        // Проверка, нужно ли пропустить данного игрока при корректировке хитбокса
        // Игрок пропускается, если это текущий игрок (mc.player) или если игрок мертв
        return player == mc.player || !player.isAlive();
    }

    /**
     * Устанавливаем новый размер для хитбокса
     */
    private void setBoundingBox(Entity entity, float size) {
        // Вычисление нового хитбокса для сущности и установка ее
        AxisAlignedBB newBoundingBox = calculateBoundingBox(entity, size);
        entity.setBoundingBox(newBoundingBox);
    }

    /**
     * Вычисление координат минимальной и максимальной точек хитбокса для сущности и создание
     * и возвращение нового хитбокса сущности
     */
    private AxisAlignedBB calculateBoundingBox(Entity entity, float size) {
        // Вычисление координат минимальной и максимальной точек хитбокса для сущности
        double minX = entity.getPosX() - size;
        double minY = entity.getBoundingBox().minY;
        double minZ = entity.getPosZ() - size;
        double maxX = entity.getPosX() + size;
        double maxY = entity.getBoundingBox().maxY;
        double maxZ = entity.getPosZ() + size;

        // Создание и возвращение нового хитбокса сущности
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}

