package ru.levinov.modules.impl.player;

import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import net.minecraft.potion.Effects;
@FunctionAnnotation(name = "NoDizEffect", type = Type.Player,desc = "Убирает плохие эффекты",
        keywords = {"NoBadEffect","NoBadEffects"})
public class NoDizEffect extends Function {
    private final BooleanOption LEVITATION = new BooleanOption("Левитация", false);
    private final BooleanOption SLOW_FALLING = new BooleanOption("Плавное падение", false);
    private final BooleanOption JUMP_BOOST = new BooleanOption("Прыгучесть", false);
    private final BooleanOption NAUSEA = new BooleanOption("Тошнота", false);
    private final BooleanOption BLINDNESS = new BooleanOption("Слепота", false);
    private final BooleanOption HUNGER = new BooleanOption("Голод", false);
    private final BooleanOption Weakness = new BooleanOption("Слабость", false);

    public NoDizEffect() {
        addSettings(LEVITATION, SLOW_FALLING, JUMP_BOOST, NAUSEA, BLINDNESS, HUNGER,Weakness);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate e) {
            if (LEVITATION.get()) {
                if (mc.player.isPotionActive(Effects.LEVITATION)) {
                    mc.player.removePotionEffect(Effects.LEVITATION);
                }
            }
            if (SLOW_FALLING.get()) {
                if (mc.player.isPotionActive(Effects.SLOW_FALLING)) {
                    mc.player.removePotionEffect(Effects.SLOW_FALLING);
                }
            }
            if (JUMP_BOOST.get()) {
                if (mc.player.isPotionActive(Effects.JUMP_BOOST)) {
                    mc.player.removePotionEffect(Effects.JUMP_BOOST);
                }
            }
            if (NAUSEA.get()) {
                if (mc.player.isPotionActive(Effects.NAUSEA)) {
                    mc.player.removePotionEffect(Effects.NAUSEA);
                }
            }
            if (BLINDNESS.get()) {
                if (mc.player.isPotionActive(Effects.BLINDNESS)) {
                    mc.player.removePotionEffect(Effects.BLINDNESS);
                }
            }
            if (HUNGER.get()) {
                if (mc.player.isPotionActive(Effects.HUNGER)) {
                    mc.player.removePotionEffect(Effects.HUNGER);
                }
            }
            if (Weakness.get()) {
                if (mc.player.isPotionActive(Effects.WEAKNESS)) {
                    mc.player.removePotionEffect(Effects.WEAKNESS);
                }
            }
        }
    }
}
