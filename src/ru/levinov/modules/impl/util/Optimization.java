package ru.levinov.modules.impl.util;

import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import ru.levinov.events.Event;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.util.misc.TimerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author levin1337
 * @since 10.08.2024
 */
@FunctionAnnotation(name = "Optimization", type = Type.Util,desc = "Оптимизация клиента")
public class Optimization extends Function {

    public final BooleanOption ofSky = new BooleanOption("Графика облоков", true);
    public final BooleanOption ofCustomSky = new BooleanOption("Графика неба", true);
    public final BooleanOption entityShadows = new BooleanOption("Энтити", true);
    public final BooleanOption shadowshat = new BooleanOption("Чат", false);
    public final BooleanOption fancyGraphics = new BooleanOption("Графика", false);
    public final BooleanOption ambientOcclusion = new BooleanOption("Амбиентное затенение", false);
    public final BooleanOption enableVsync = new BooleanOption("Синхронизация", false);
    public final BooleanOption mipmapLevels = new BooleanOption("МипМап", false);
    public final BooleanOption memory = new BooleanOption("Память системы", true);
    public final BooleanOption backroundinvenory = new BooleanOption("Удалить фон инвентаря", false);
    public final MultiBoxSetting optimizeSelection = new MultiBoxSetting("Оптимизировать", new BooleanOption("Освещение",true), new BooleanOption("Партиклы",true), new BooleanOption("Подсветка клиента.", false));

    private final TimerUtil timerHelper = new TimerUtil();

     public Optimization() {
         addSettings(optimizeSelection,ofSky,ofCustomSky,entityShadows,shadowshat,fancyGraphics,ambientOcclusion,enableVsync,mipmapLevels,memory,backroundinvenory);
     }

    @Override
    public void onEvent(Event event) {
        if (this.ofSky.get()) {
            mc.gameSettings.ofSky = false;
        } else {
            mc.gameSettings.ofSky = true;
        }

        if (this.ofCustomSky.get()) {
            mc.gameSettings.ofCustomSky = false;
        } else {
            mc.gameSettings.ofCustomSky = true;
        }

        if (this.entityShadows.get()) {
            mc.gameSettings.entityShadows = false;
        } else {
            mc.gameSettings.entityShadows = true;
        }

        if (this.shadowshat.get()) {
            mc.gameSettings.autoJump = false;
            mc.gameSettings.ofChatShadow = false;
        } else {
            mc.gameSettings.ofChatShadow = true;
        }




        if (fancyGraphics.get()) {
            mc.gameSettings.ofOcclusionFancy = false; // Отключаем "красивую" графику
        }

        if (ambientOcclusion.get()) {
            mc.gameSettings.ambientOcclusionStatus = AmbientOcclusionStatus.OFF; // Отключаем амбиентное затенение
        }
        if (enableVsync.get()) {
            mc.gameSettings.vsync = false;
        }
        if (mipmapLevels.get()) {
            mc.gameSettings.mipmapLevels = 0; // Отключаем мипмаппинг
        }








        if (memory.get()) {
            if (timerHelper.hasTimeElapsed(300000)) {
                System.gc();
                timerHelper.reset();
            }
        }
    }

    public void updatePlayerEffects(PlayerEntity player) {
        // Проверяем, активен ли эффект спектральной стрелы
        if (player.isPotionActive(Effects.GLOWING)) {
            // Удаляем эффект спектральной стрелы
            player.removePotionEffect(Effects.GLOWING);
        }
    }

    protected void onDisable() {
        super.onDisable();
        mc.gameSettings.ofSky = true;
        mc.gameSettings.ofCustomSky = true;
        mc.gameSettings.entityShadows = true;
        mc.gameSettings.autoJump = false;
        mc.gameSettings.ofChatShadow = true;
    }
}
