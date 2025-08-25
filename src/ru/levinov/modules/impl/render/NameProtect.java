package ru.levinov.modules.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.TextSetting;
import ru.levinov.util.ClientUtil;

@FunctionAnnotation(name = "NameProtect", type = Type.Render)
public class NameProtect extends Function {

    public TextSetting name = new TextSetting("Ник", "levin1337");
    public BooleanOption friends = new BooleanOption("Друзья", false);

    public NameProtect() {
        addSettings(name, friends);
    }

    @Override
    public void onEvent(Event event) {

    }


    public String patch(String text) {
        String out = text;
        if (this.state) {
            out = text.replaceAll(Minecraft.getInstance().session.getUsername(), name.text);
        }
        return out;
    }

    public ITextComponent patchFriendTextComponent(ITextComponent text, String name) {
        ITextComponent out = text;
        if (this.friends.get() && this.state) {
            out = ClientUtil.replace(text, name, this.name.text);
        }
        return out;
    }
}
