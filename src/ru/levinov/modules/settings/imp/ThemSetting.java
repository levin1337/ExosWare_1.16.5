//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.levinov.modules.settings.imp;

import ru.levinov.modules.settings.Setting;

import java.util.Arrays;
import java.util.function.Supplier;

public class ThemSetting extends Setting {
    private int index;
    public String[] modes;

    public ThemSetting(String name, String current, String... modes) {
        super(name);
        this.modes = modes;
        this.index = Arrays.asList(modes).indexOf(current);
    }

    public boolean is(String mode) {
        return this.get().equals(mode);
    }

    public String get() {
        try {
            return this.index >= 0 && this.index < this.modes.length ? this.modes[this.index] : this.modes[0];
        } catch (ArrayIndexOutOfBoundsException var2) {
            return "ERROR";
        }
    }

    public void set(String mode) {
        this.index = Arrays.asList(this.modes).indexOf(mode);
    }

    public void set(int mode) {
        this.index = mode;
    }

    public ThemSetting setVisible(Supplier<Boolean> bool) {
        this.visible = bool;
        return this;
    }

    public SettingType getType() {
        return SettingType.THEME_SETTING;
    }

    public int getIndex() {
        return this.index;
    }

    public String[] getModes() {
        return this.modes;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
