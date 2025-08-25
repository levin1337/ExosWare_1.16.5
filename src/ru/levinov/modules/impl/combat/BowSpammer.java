package ru.levinov.modules.impl.combat;

import net.minecraft.item.BowItem;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.misc.TimerUtil;

/**
 * @author levin1337
 * @since 29.06.2023
 */
@FunctionAnnotation(name = "BowSpam", type = Type.Combat,desc = "Спамит стрелями из лука",
        keywords = {"BowSpammer","FastBow"})
public class BowSpammer extends Function {
    private final SliderSetting slider = new SliderSetting("Время", 1000f, 800, 3000f, 100f);


    private final TimerUtil timerHelper = new TimerUtil();

    public BowSpammer() {
        addSettings(slider);
    }
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            handleUpdateEvent(eventUpdate);
        }
    }

    /**
     * Обрабатывает событие обновления
     *
     * @param eventUpdate обработчик обновления
     */
    private void handleUpdateEvent(EventUpdate eventUpdate) {
        if (mc.player.inventory.getCurrentItem().getItem() instanceof BowItem && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 1.9f) {
                mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), mc.player.getHorizontalFacing()));
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.player.stopActiveHand();
        }
    }
}
