package ru.levinov.modules.impl.render;

import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.*;
import ru.levinov.util.animations.Animation;
import ru.levinov.util.math.MathUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author levin1337
 * @since 26.06.2023
 */
@FunctionAnnotation(name = "CustomWorld", type = Type.Render)
public class CustomWorld extends Function {

    public MultiBoxSetting modes = new MultiBoxSetting("Изменять",
            new BooleanOption("Время", false),
            new BooleanOption("Туман", false));

    private ModeSetting timeOfDay = new ModeSetting("Время суток", "Ночь", "День", "Закат", "Рассвет", "Ночь", "Полночь", "Полдень","Из Жизни").setVisible(() -> modes.get(0));

    public ColorSetting colorFog = new ColorSetting("Цвет тумана", -1).setVisible(() -> modes.get(1));
    public SliderSetting distanceFog = new SliderSetting("Дальность тумана", 4.0F, 1.1f, 150.0F, 0.01f).setVisible(() -> modes.get(1));

    public CustomWorld() {
        addSettings(modes, timeOfDay, colorFog, distanceFog);
    }

    float time;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPacket eventPacket && ((EventPacket) event).isReceivePacket()) {
            if (eventPacket.getPacket() instanceof SUpdateTimePacket) {
                if (modes.get(0)) {
                    eventPacket.setCancel(true);
                }
            }
        }
        if (event instanceof EventUpdate) {
            if (modes.get(0)) {
                float time = 0;
                switch (timeOfDay.get()) {
                    case "День" -> {
                        mc.world.setDayTime((long) 1000);
                    }
                    case "Закат" -> {
                        mc.world.setDayTime((long) 12000);
                    }
                    case "Рассвет" -> {
                        mc.world.setDayTime((long) 23000);
                    }
                    case "Ночь" -> {
                        mc.world.setDayTime((long) 13000);
                    }
                    case "Полночь" -> {
                        mc.world.setDayTime((long) 18000);
                    }
                    case "Полдень" -> {
                        mc.world.setDayTime((long) 6000);
                    }
                    case "Из Жизни" -> {
                        setTimeToMoscow();
                    }
                }

            }
        }
    }
    private void setTimeToMoscow() {
        World world = mc.world;
        if (world != null) {
            ZonedDateTime moscowTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
            int hours = moscowTime.getHour();
            int minutes = moscowTime.getMinute();

            //тик вылета спермы
            long timeInTicks = (hours * 1000L / 24) + (minutes * 1000L / (24 * 60));

            // Я мартовский кот
            if (moscowTime.getMonthValue() == 12 || moscowTime.getMonthValue() <= 2) {
                // Зимой
                timeInTicks += 5000;
            }
            mc.world.setDayTime((long) timeInTicks);
        }
    }
}
