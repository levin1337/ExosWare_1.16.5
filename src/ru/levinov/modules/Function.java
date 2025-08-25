package ru.levinov.modules;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.events.Event;
import ru.levinov.managment.Managment;
import ru.levinov.modules.impl.util.ClientSounds;
import ru.levinov.modules.settings.Configurable;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.*;
import ru.levinov.ui.beta.component.impl.*;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.SoundUtil;
import ru.levinov.util.misc.AudioUtil;

import java.util.Arrays;
import java.util.Date;


public abstract class Function extends Configurable implements IMinecraft {
    public float degree = 0;

    private final FunctionAnnotation info = this.getClass().getAnnotation(FunctionAnnotation.class);

    public String name;
    public String keywords;
    public Type category;
    public int bind;
    public String desc;
    public float animation;
    public boolean state, util;
    public boolean expanded;

    public Function() {
        initializeProperties();
    }

    public Function(String name, Type category) {
        this.name = name;
        this.category = category;
        state = false;
        bind = 0;
        init();
    }

    public void init() {
    }

    private void initializeProperties() {
        name = info.name();
        desc = info.desc();
        category = info.type();
        state = false;
        bind = info.key();
        keywords = Arrays.toString(info.keywords());
    }

    public void setStateNotUsing(final boolean enabled) {
        state = enabled;
    }


    public void setState(final boolean enabled) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (!enabled)
            this.onDisable();
        else
            this.onEnable();

        state = enabled;
    }

    /**
     * Переключает состояние функции (включено/выключено).
     * Вызывает соответствующие методы onEnable() или onDisable() в зависимости от состояния.
     */
    public void toggle() {
        if (mc.player != null && mc.world != null) {
            this.state = !this.state;
            ClientSounds clientSounds = Managment.FUNCTION_MANAGER.clientSounds;
            if (!this.state) {
                this.onDisable();
                if (clientSounds.state && clientSounds.mode.is("Type-1")) {
                    AudioUtil.playSound("NurOff.wav", Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }

                if (clientSounds.state && clientSounds.mode.is("NoteBlock")) {
                    SoundUtil.playSound(Math.max(2.0F + (!this.state ? -0.25F : 0.0F), 0.0F), Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }

                if (clientSounds.state && clientSounds.mode.is("Type-2")) {
                    AudioUtil.playSound("AkrOff.wav", Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }

                if (clientSounds.state && clientSounds.mode.is("Type-3")) {
                    AudioUtil.playSound("CelOff.wav", Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }
                if (clientSounds.state && clientSounds.mode.is("Type-4")) {
                    AudioUtil.playSound("disableOld.wav", Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }
            } else {
                this.onEnable();
                if (clientSounds.state && clientSounds.mode.is("Type-1")) {
                    AudioUtil.playSound("NurOn.wav", Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }

                if (clientSounds.state && clientSounds.mode.is("NoteBlock")) {
                    SoundUtil.playSound(2.25F, Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }

                if (clientSounds.state && clientSounds.mode.is("Type-2")) {
                    AudioUtil.playSound("AkrOn.wav", Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }

                if (clientSounds.state && clientSounds.mode.is("Type-3")) {
                    AudioUtil.playSound("CelOn.wav", Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }
                if (clientSounds.state && clientSounds.mode.is("Type-4")) {
                    AudioUtil.playSound("enableOld.wav", Managment.FUNCTION_MANAGER.clientSounds.volume.getValue().floatValue());
                }
            }

            String var10001 = this.name;
            Managment.NOTIFICATION_MANAGER.add(var10001 + " was " + (this.state ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"), "Function Debug", 2);
        }
    }

    /**
     * Проверяет текущее состояние функции.
     *
     * @return true, если функция включена, false - если выключена.
     */
    public boolean isState() {
        return this.state;
    }

    /**
     * Вызывается при выключении функции.
     * Может быть переопределен в подклассе для добавления специфической логики.
     */
    protected void onDisable() {
    }

    /**
     * Вызывается при включении функции.
     * Может быть переопределен в подклассе для добавления специфической логики.
     */
    protected void onEnable() {
    }

    /**
     * Сохраняет состояние функции в объект JsonObject.
     *
     * @return объект JsonObject, содержащий сохраненные данные функции.
     */
    public JsonObject save() {
        JsonObject object = new JsonObject();



        object.addProperty("bind", bind);
        object.addProperty("state", state);

        for (Setting setting : getSettingList()) {
            String name = setting.getName();
            switch (setting.getType()) {
                case BOOLEAN_OPTION -> object.addProperty(name, ((BooleanOption) setting).get());
                case SLIDER_SETTING -> object.addProperty(name, ((SliderSetting) setting).getValue().floatValue());
                case MODE_SETTING -> object.addProperty(name, ((ModeSetting) setting).getIndex());
                case COLOR_SETTING -> object.addProperty(name, ((ColorSetting) setting).get());
                case MULTI_BOX_SETTING -> {
                    ((MultiBoxSetting) setting).options.forEach(option -> object.addProperty(option.getName(), option.get()));
                }
                case BIND_SETTING -> object.addProperty(name, ((BindSetting) setting).getKey());
                case TEXT_SETTING -> object.addProperty(name, ((TextSetting) setting).text);
            }
        }
        return object;
    }

    /**
     * Загружает состояние функции из объекта JsonObject.
     *
     * @param object объект JsonObject, содержащий сохраненные данные функции.
     */
    public void load(JsonObject object, boolean start) {
        if (object != null) {
            if (object.has("bind")) {
                bind = object.get("bind").getAsInt();
            }

            if (object.has("state")) {
                // Проверяем, является ли значение логическим
                if (object.get("state").isJsonPrimitive() && object.get("state").getAsJsonPrimitive().isBoolean()) {
                    if (start) {
                        setStateNotUsing(object.get("state").getAsBoolean());
                    } else {
                        setState(object.get("state").getAsBoolean());
                    }
                }
            }

            for (Setting setting : getSettingList()) {
                String name = setting.getName();
                if (!object.has(name) && !(setting instanceof MultiBoxSetting)) {
                    continue;
                }

                switch (setting.getType()) {
                    case BOOLEAN_OPTION -> {
                        if (object.get(name).isJsonPrimitive() && object.get(name).getAsJsonPrimitive().isBoolean()) {
                            ((BooleanOption) setting).set(object.get(name).getAsBoolean());
                        }
                    }
                    case SLIDER_SETTING -> {
                        if (object.get(name).isJsonPrimitive() && object.get(name).getAsJsonPrimitive().isNumber()) {
                            ((SliderSetting) setting).setValue((float) object.get(name).getAsDouble());
                        }
                    }
                    case MODE_SETTING -> {
                        if (object.get(name).isJsonPrimitive() && object.get(name).getAsJsonPrimitive().isNumber()) {
                            ((ModeSetting) setting).setIndex(object.get(name).getAsInt());
                        }
                    }
                    case BIND_SETTING -> {
                        if (object.get(name).isJsonPrimitive() && object.get(name).getAsJsonPrimitive().isNumber()) {
                            ((BindSetting) setting).setKey(object.get(name).getAsInt());
                        }
                    }
                    case COLOR_SETTING -> {
                        if (object.get(name).isJsonPrimitive() && object.get(name).getAsJsonPrimitive().isNumber()) {
                            ((ColorSetting) setting).color = object.get(name).getAsInt();
                        }
                    }
                    case MULTI_BOX_SETTING -> {
                        ((MultiBoxSetting) setting).options.forEach(option -> {
                            String optionName = option.getName();
                            if (object.has(optionName) && object.get(optionName).isJsonPrimitive() && object.get(optionName).getAsJsonPrimitive().isBoolean()) {
                                option.set(object.get(optionName).getAsBoolean());
                            }
                        });
                    }
                    case TEXT_SETTING -> {
                        if (object.get(name).isJsonPrimitive() && object.get(name).getAsJsonPrimitive().isString()) {
                            ((TextSetting) setting).text = object.get(name).getAsString();
                        }
                    }
                }
            }
        }
    }

    /**
     * Обработчик события.
     * Метод, который будет вызываться при возникновении события.
     *
     * @param event событие, которое произошло.
     */
    public abstract void onEvent(final Event event);
}
