package ru.levinov.modules.impl.util;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;

@FunctionAnnotation(
        name = "ClientSounds",
        type = Type.Util,
        desc = "����� �������"
)
public class ClientSounds extends Function {
    public final ModeSetting mode = new ModeSetting("���", "Type-1", new String[]{"Type-1", "Type-2", "Type-3","Type-4", "NoteBlock"});
    public SliderSetting volume = new SliderSetting("���������", 0.4F, 0.01F, 3.0F, 0.01F);

    public static BooleanOption soundgui = new BooleanOption("���� ��������", true);

    public static BooleanOption soundcheckbox = new BooleanOption("���� ���-�����", true);


    public ClientSounds() {
        addSettings(mode, volume,soundgui,soundcheckbox);
    }

    public void onEvent(Event event) {
    }
}