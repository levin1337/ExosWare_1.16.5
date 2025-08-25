package ru.levinov.modules.impl.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.misc.TimerUtil;

@FunctionAnnotation(
        name = "FastEXP",
        type = Type.Util,
        desc = "Быстрое взаимодействие с опытом"
)
public class FastEXP extends Function {
    private final TimerUtil timerUtil = new TimerUtil();

    public void onEvent(Event event) {
        if (event instanceof EventUpdate && timerUtil.hasTimeElapsed(0L)) {
            ItemStack itemStack = mc.player.getHeldItemMainhand();
            if (itemStack.getItem() == Items.EXPERIENCE_BOTTLE) {
                mc.rightClickDelayTimer = 0;
            }
        }
    }
}