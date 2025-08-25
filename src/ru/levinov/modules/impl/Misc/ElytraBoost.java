package ru.levinov.modules.impl.Misc;

import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.world.InventoryUtil;

@FunctionAnnotation(
        name = "ElytraBoost",
        type = Type.Misc,
        desc = "Ускоряет вас на элитрах",
        keywords = {"SuperFireWork"}
)
public class ElytraBoost extends Function {



    public static final ModeSetting mode = new ModeSetting("Мод", "BravoHvH", "ReallyWorld","BravoHvH", "Свой");
    private final TimerUtil timerUtil = new TimerUtil();
    public final SliderSetting speed = new SliderSetting("Cкорость", 1.7F, 1.5F, 3.5F, 0.01F);
    private final BooleanOption use = new BooleanOption("Автоматический фейрверк", false);
    private final SliderSetting time = (new SliderSetting("Задержка феерверка", 1000.0F, 500.0F, 2000.0F, 50.0F));
    public static final BooleanOption doble = new BooleanOption("Дублировать", true);

    public ElytraBoost() {
        addSettings(mode,speed, time,use,doble);
    }

    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if ((float) timerUtil.getTime() > time.getValue().floatValue()) {
                if (use.get()) {
                    useFirework();
                } else {
                    timerUtil.reset();
                }
            }
        }
    }

    private void useFirework() {
        if (InventoryUtil.getItemSlot(Items.FIREWORK_ROCKET) == -1) {
            ClientUtil.sendMesage(TextFormatting.RED + "У вас нету фейерверов!");
        } else {
            InventoryUtil.inventorySwapClick1337(Items.FIREWORK_ROCKET, false);
            timerUtil.reset();
        }
    }

    public void onDisable() {
        super.onDisable();
    }
}