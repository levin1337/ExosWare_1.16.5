package ru.levinov.modules.impl.render;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;


@FunctionAnnotation(name = "SwingAnimation", type = Type.Render,
        desc = "Анимация для рук",
        keywords = {"Animations"})
public class SwingAnimationFunction extends Function {
    public final ModeSetting swordAnim = new ModeSetting("Мод", "Self", "Smooth", "Self", "Block", "Back","Swipe","Default","Big","Break","Down","Bonk","Astolfo","Fap","Kick","1.8","Glide");

    public final BooleanOption onlyAura = (new BooleanOption("Только с Aura", true));


    public final SliderSetting angle = new SliderSetting("Угол", 100, 0, 360, 1).setVisible(() -> swordAnim.is("Self") || swordAnim.is("Block") || swordAnim.is("Big"));
    public final SliderSetting swipePower = new SliderSetting("Сила взмаха", 8, 1, 10, 1).setVisible(() -> swordAnim.is("Self") || swordAnim.is("Block") || swordAnim.is("Back") || swordAnim.is("Big"));
    public final SliderSetting swipeSpeed = new SliderSetting("Плавность взмаха", 11, 1, 20, 1);
    public final SliderSetting right_x = new SliderSetting("RightX", 0.0F, -2, 2, 0.1F);
    public final SliderSetting right_y = new SliderSetting("RightY", 0.0F, -2, 2, 0.1F);
    public final SliderSetting right_z = new SliderSetting("RightZ", 0.0F, -2, 2, 0.1F);
    public final SliderSetting left_x = new SliderSetting("LeftX", 0.0F, -2, 2, 0.1F);
    public final SliderSetting left_y = new SliderSetting("LeftY", 0.0F, -2, 2, 0.1F);
    public final SliderSetting left_z = new SliderSetting("LeftZ", 0.0F, -2, 2, 0.1F);


    public final BooleanOption damage = new BooleanOption("Урон", true);

    public final BooleanOption run = new BooleanOption("Ходьба", true);

    public final BooleanOption blurinv = new BooleanOption("InvBlur", true);

    public SwingAnimationFunction() {
        addSettings(swordAnim,onlyAura, angle, swipePower, swipeSpeed, right_x, right_y, right_z, left_x, left_y, left_z,damage,run,blurinv);
    }
    @Override
    public void onEvent(Event event) {}
}
