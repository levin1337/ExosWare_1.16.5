package ru.levinov.modules.impl.player;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventCalculateCooldown;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.HudUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author levin1337
 * @since 09.06.2023
 */
@FunctionAnnotation(name = "ItemCooldown", type = Type.Player, desc = "Таймер на предметы")
public class GappleCooldownFunction extends Function {

    //5.6 apple , ender pearl 13.5

    public static final MultiBoxSetting items = new MultiBoxSetting("Предметы",
            new BooleanOption("Геплы", true),
            new BooleanOption("Перки", true),
            new BooleanOption("Хорусы", true),
            new BooleanOption("Чарки", false));

    private static final SliderSetting gappleTime = new SliderSetting("Кулдаун гепла", 4.2F, 1.0F, 10.0F, 0.05F)
            .setVisible(() -> items.get(0));
    private static final SliderSetting pearlTime = new SliderSetting("Кулдаун перок", 13.5F, 1.0F, 15.0F, 0.05F)
            .setVisible(() -> items.get(1));
    private static final SliderSetting horusTime = new SliderSetting("Кулдаун хорусов", 2.3F, 1.0F, 10.0F, 0.05F)
            .setVisible(() -> items.get(2));
    private static final SliderSetting enchantmentGappleTime = new SliderSetting("Кулдаун чарок", 4.5F, 1.0F, 10.0F, 0.05F)
            .setVisible(() -> items.get(3));
    private BooleanOption onlyPvP = new BooleanOption("Только в PVP", false);
    public HashMap<Item, Long> lastUseItemTime = new HashMap<>();// 3 секунды

    public GappleCooldownFunction() {
        addSettings(items, gappleTime, enchantmentGappleTime, pearlTime, horusTime, onlyPvP);
    }
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventCalculateCooldown calculateCooldown) {
            applyGoldenAppleCooldown(calculateCooldown);
        }
    }

    /**
     * Применяет задержку использования золотого яблока.
     * Получает текущее время задержки и устанавливает соответствующую задержку
     * для золотого яблока.
     */
    private void applyGoldenAppleCooldown(EventCalculateCooldown calcCooldown) {
        List<Item> itemsToRemove = new ArrayList<>();

        for (Map.Entry<Item, Long> entry : lastUseItemTime.entrySet()) {
            ItemEnum itemEnum = ItemEnum.getItemEnum(entry.getKey());

            if (itemEnum == null || calcCooldown.itemStack != itemEnum.getItem() || !itemEnum.getActive().get() || isNotPvP()) {
                continue;
            }

            long time = System.currentTimeMillis() - entry.getValue();
            float timeSetting = itemEnum.getTime().get() * 1000.0F;

            if (time < timeSetting && itemEnum.getActive().get()) {
                calcCooldown.setCooldown(time / timeSetting);
            } else {
                itemsToRemove.add(itemEnum.getItem());
            }
        }

        itemsToRemove.forEach(lastUseItemTime::remove);
    }

    public boolean isNotPvP() {
        return onlyPvP.get() && !ClientUtil.isPvP();
    }

    public boolean isCurrentItem(ItemEnum item) {
        if (!item.getActive().get()) {
            return false;
        }

        return item.getActive().get() && Arrays.stream(ItemEnum.values()).anyMatch(e -> e == item);
    }

    @Getter
    public enum ItemEnum {
        CHORUS(Items.CHORUS_FRUIT, () -> items.get(2), () -> horusTime.getValue().floatValue()),
        GOLDEN_APPLE(Items.GOLDEN_APPLE, () -> items.get(0), () -> gappleTime.getValue().floatValue()),
        ENCHANTED_GOLDEN_APPLE(Items.ENCHANTED_GOLDEN_APPLE, () -> items.get(3), () -> enchantmentGappleTime.getValue().floatValue()),
        ENDER_PEARL(Items.ENDER_PEARL, () -> items.get(1), () -> pearlTime.getValue().floatValue());

        private final Item item;
        private final Supplier<Boolean> active;
        private final Supplier<Float> time;


        ItemEnum(Item item, Supplier<Boolean> active, Supplier<Float> time) {
            this.item = item;
            this.active = active;
            this.time = time;
        }

        public static ItemEnum getItemEnum(Item item) {
            return Arrays.stream(ItemEnum.values())
                    .filter(e -> e.getItem() == item)
                    .findFirst()
                    .orElse(null);
        }
    }

}