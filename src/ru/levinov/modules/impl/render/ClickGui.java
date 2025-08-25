package ru.levinov.modules.impl.render;

import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;

@FunctionAnnotation(
        name = "ClickGui",
        type = Type.Render
)
public class ClickGui extends Function {

    public final ModeSetting guiselect = new ModeSetting("����� ClickGUI", "CS GUI2", "CS GUI","CS GUI2","DropDowm2");
    public final ModeSetting capes = new ModeSetting("����� Cape", "Cape", "Cape","Cape2","Cape3","Cape4","Cape5","��� �����");
    public final ModeSetting skins = new ModeSetting("����� Skin", "�������", "vladsuper31","�������");

    public SliderSetting blurVal = new SliderSetting("��������", 15.0F, 5.0F, 20.0F, 1.0F);

    public BooleanOption blur = new BooleanOption("��������", false);
    public BooleanOption glow = new BooleanOption("��������", false);
    public BooleanOption proxy = new BooleanOption("������������� ������", false);

    public ClickGui() {
        addSettings(guiselect,capes,skins,blurVal,blur,glow,proxy);
    }

    protected void onEnable() {
        super.onEnable();
        setState(false);
    }

    public void onEvent(Event event) {
    }
}