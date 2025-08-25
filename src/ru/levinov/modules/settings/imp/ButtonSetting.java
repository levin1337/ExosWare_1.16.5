package ru.levinov.modules.settings.imp;

import lombok.Getter;
import lombok.Setter;
import ru.levinov.modules.settings.Setting;

import java.util.function.Supplier;

@Getter
@Setter
public class ButtonSetting extends Setting {

    private Runnable run;

    public ButtonSetting(String name, Runnable run) {
        super(name);
        this.run = run;
    }
    public ButtonSetting setVisible(Supplier<Boolean> bool) {
        visible = bool;
        return this;
    }

    @Override
    public SettingType getType() {
        return SettingType.BUTTON_SETTING;
    }
}
