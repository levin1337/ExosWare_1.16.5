package ru.levinov.modules.impl.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.world.InventoryUtil;
import ru.levinov.util.world.WorldUtil;

/**
 * @author levin1337
 * @since 05.06.2023
 */
@FunctionAnnotation(name = "AutoTotem", type = Type.Combat,desc = "Авто взятие тотема в руку при здоровье")
public class AutoTotemFunction extends Function {

    private final ModeSetting autototemMode = new ModeSetting("Мод", "RW", "RW", "Matrix");

    private final SliderSetting health = new SliderSetting("Здоровье", 4.5f, 1.f, 20.f, 0.05f);
    private final BooleanOption swapBack = new BooleanOption("Возвращать предмет", true);
    private final BooleanOption noBallSwitch = new BooleanOption("Не брать если шар в руке", false);
    private final MultiBoxSetting mode = new MultiBoxSetting("Срабатывать",
            new BooleanOption("Золотые сердца", true),
            new BooleanOption("Кристаллы", true),
            new BooleanOption("Обсидиан", false),
            new BooleanOption("Якорь", false),
            new BooleanOption("Падение", true));


    private final SliderSetting HPElytra = (new SliderSetting("Брать раньше если в элитрах на", 4.0F, 2.0F, 6.0F, 1F));

    private final SliderSetting radiusExplosion = new SliderSetting("Дистанция до кристала", 6, 1, 8, 1).setVisible(() -> mode.get(1));
    private final SliderSetting radiusObs = new SliderSetting("Дистанция до обсидиана", 6, 1, 8, 1).setVisible(() -> mode.get(2));
    private final SliderSetting radiusAnch = new SliderSetting("Дистанция до якоря", 6, 1, 8, 1).setVisible(() -> mode.get(2));

    int oldItem = -1;

    public AutoTotemFunction() {
        addSettings(autototemMode, mode, health, swapBack, noBallSwitch,HPElytra,radiusExplosion,radiusObs,radiusAnch);
    }

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventUpdate) {
            handleEventUpdate((EventUpdate) event);
        }
    }

    /**
     * Обработка события обновления.
     *
     * @param event событие обновления.
     */
    private void handleEventUpdate(EventUpdate event) {
        // Получаем слот тотема
        final int slot = InventoryUtil.getItemSlot(Items.TOTEM_OF_UNDYING);

        // Если левая рука не пустая
        boolean handNotNull = !(mc.player.getHeldItemOffhand().getItem() instanceof AirItem);

        // Проверяем, находится ли тотем в руке игрока (в любой из двух рук)
        final boolean totemInHand = mc.player.getHeldItemOffhand()
                .getItem() == Items.TOTEM_OF_UNDYING
                || mc.player.getHeldItemMainhand()
                .getItem() == Items.TOTEM_OF_UNDYING;

        if (condition()) {
            // Если выполняется условие
            // и есть свободный слот для тотема
            // и тотем не находится в руке, то перемещаем его в руку
            if (slot >= 0 && !totemInHand) {
                InventoryUtil.moveItem(slot, 45, handNotNull);
                if (handNotNull && oldItem == -1) {
                    oldItem = slot;
                }
            }
        } else if (oldItem != -1 && swapBack.get()) {
            // Если условие не выполняется,
            // но был сохранен предыдущий слот тотема и активирован флаг swapBack,
            // то возвращаем тотем на предыдущее место
            InventoryUtil.moveItem(oldItem, 45, handNotNull);
            oldItem = -1;
        }
    }


    /**
     * Проверка всех условий
     */
    private boolean condition() {
        // Рассчитываем количество поглощенного урона от эффекта поглощения
        final float absorption = this.mode.get(0) && mc.player
                .isPotionActive(Effects.ABSORPTION)
                ? mc.player.getAbsorptionAmount()
                : 0.0f;

        // Проверяем условия, при которых нужно использовать тотем
        if (mc.player.getHealth() + absorption <= this.health.getValue().floatValue())
            return true;

        if (!this.isBall()) {
            if (this.checkCrystal())
                return true;

            if (this.checkObsidian())
                return true;

            if (this.checkAnchor())
                return true;
        }

        return this.checkHPElytra() ? true : this.checkFall();
    }

    /**
     * Проверка условия для использования тотема при падении.
     *
     * @return true, если нужно использовать тотем при падении, иначе false.
     */
    private boolean checkFall() {
        if (!this.mode.get(4)) {
            return false;
        }

        if (mc.player.isElytraFlying()) {
            return false;
        }

        return mc.player.fallDistance > 10.0f;
    }


    private boolean checkHPElytra() {
     return ((ItemStack)mc.player.inventory.armorInventory.get(2)).getItem() == Items.ELYTRA && mc.player.getHealth() <= health.getValue().floatValue() + HPElytra.getValue().floatValue();
    }
    /**
     * Проверка, если у игрока шар в левой руке.
     *
     * @return true, если у игрока шар в левой руке, иначе false.
     */
    private boolean isBall() {
        if (this.mode.get(3) && mc.player
                .fallDistance > 5.0f)
            return false;

        return this.noBallSwitch.get() && mc.player.getHeldItemOffhand()
                .getItem() instanceof SkullItem;
    }

    /**
     * Проверка условия для использования тотема при наличии обсидиановых блоков в радиусе.
     *
     * @return true, если нужно использовать тотем при наличии обсидиановых блоков в радиусе, иначе false.
     */
    private boolean checkObsidian() {
        if (!mode.get(2))
            return false;

        return WorldUtil.TotemUtil
                .getBlock(radiusObs.getValue().floatValue(), Blocks.OBSIDIAN) != null;
    }

    /**
     * Проверка условия для использования тотема при наличии якорей возрождения в радиусе.
     *
     * @return true, если нужно использовать тотем при наличии якорей возрождения в радиусе, иначе false.
     */
    private boolean checkAnchor() {
        if (!mode.get(3))
            return false;

        return WorldUtil.TotemUtil
                .getBlock(radiusAnch.getValue().floatValue(), Blocks.RESPAWN_ANCHOR) != null;
    }

    /**
     * Проверка условия для использования тотема при наличии кристаллов или TNT в радиусе.
     *
     * @return true, если нужно использовать тотем при наличии кристаллов или TNT в радиусе, иначе false.
     */
    private boolean checkCrystal() {
        if (!mode.get(1))
            return false;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof EnderCrystalEntity
                    && mc.player.getDistance(entity) <= radiusExplosion.getValue().floatValue())
                return true;

            if ((entity instanceof TNTEntity || entity instanceof TNTMinecartEntity)
                    && mc.player.getDistance(entity) <= radiusExplosion.getValue().floatValue())
                return true;
        }
        return false;
    }


    /**
     * Сброс состояния переменных.
     */
    private void reset() {
        this.oldItem = -1;
    }


    @Override
    protected void onEnable() {
        reset();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        reset();
        super.onDisable();
    }
}
