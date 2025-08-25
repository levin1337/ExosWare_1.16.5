package ru.levinov.modules.impl.combat;

import ru.levinov.events.Event;
import ru.levinov.events.impl.game.EventAttack;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.misc.AudioUtil;

@FunctionAnnotation(
        name = "ClickSounds",
        type = Type.Combat,
        desc = "Звуки при ударе",
        keywords = {"HitSound","HitSounds"}
)
public class ClickSounds extends Function {
    public final ModeSetting mode = new ModeSetting("Мод", "NeverLose", "NeverLose", "Bonk", "Bubble","Metallic");
    public SliderSetting volume = new SliderSetting("Громкость", 0.2F, 0.1F, 1.0F, 0.1F);



    public ClickSounds() {
        addSettings(mode, volume);
    }

    public void onEvent(Event event) {
        if (event instanceof EventAttack eventAttack) {
            if (mode.is("NeverLose")) {
                AudioUtil.playSound("bell.wav", volume.getValue().floatValue());
            }
            if (mode.is("Bonk")) {
                AudioUtil.playSound("bonk.wav", volume.getValue().floatValue());
            }
            if (mode.is("Bubble")) {
                AudioUtil.playSound("bubble.wav", volume.getValue().floatValue());
            }
            if (mode.is("Metallic")) {
                AudioUtil.playSound("metallic.wav", volume.getValue().floatValue());
            }
        }
    }
}