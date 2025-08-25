package ru.levinov.modules.impl.util;

import net.minecraft.util.text.ITextComponent;
import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ButtonSetting;
import ru.levinov.ui.Spotify.Spotify;

@FunctionAnnotation(name = "MiniBots", type = Type.Util,desc = "Мини боты")
public class MiniBots extends Function {

    public ButtonSetting buttonSetting = new ButtonSetting("Открыть панель", () -> {
   //     mc.displayGuiScreen(new WelcomeScreen(ITextComponent.getTextComponentOrEmpty("")));
        mc.displayGuiScreen(new Spotify(ITextComponent.getTextComponentOrEmpty("")));
    });

    public MiniBots() {
        super();
        addSettings(buttonSetting);
    }

    @Override
    public void onEvent(Event event) {
        mc.displayGuiScreen(new Spotify(ITextComponent.getTextComponentOrEmpty("")));
        toggle();
    }
}
