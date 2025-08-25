package ru.levinov.modules.impl.Misc;

import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.modules.settings.imp.TextSetting;
import ru.levinov.util.misc.TimerUtil;

import java.util.*;
import java.util.regex.Pattern;

@FunctionAnnotation(
        name = "Spammer",
        type = Type.Misc,
        desc = "Спамит в чат за вас"
)
public class Spammer extends Function {

    private final ModeSetting mode = new ModeSetting("Мод", "JungleMine", "JungleMine", "HellHub", "WildGrief","ReallCraft","ProstoTrainer", "Личка", "Custom");
    public TextSetting custom = new TextSetting("Сообщение", "! тест").setVisible(() -> mode.is("Custom"));
    private final BooleanOption antispam = (new BooleanOption("Рандомайзер", true));
    private final SliderSetting timer = new SliderSetting("Задержка", 1500F, 1000F, 8000F, 100f);
    private final TimerUtil delay = new TimerUtil();

    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
    public Spammer() {
        addSettings(mode, timer, custom, antispam);
    }

    private List<String> messages = new ArrayList<>(Arrays.asList(
            "Лучший сервачёк апи -- levin. junglemine. pro бесплатный донатикс /levin",
            "Заходи на levin. junglemine. pro - крутой сервер",
            "Бесплатный донат на сервере levin. junglemine. pro - не упусти шанс!",
            "Этот сервер закрывается новый айпии теперь это levin. junglemine. pro",
            "Хороший пвп кит на сервере  levin. junglemine. pro"
    ));
    private int messageIndex = 0;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (mode.is("JungleMine")) {
                if (antispam.get()) {
                    Random random = new Random();
                    String randomLetters = randomize(random);
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage("! Новiй грiф проeкт levin.junglemine.pro  levin " + randomLetters);
                        delay.reset();
                    }
                } else {
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage("! Новiй грiф проeкт levin.junglemine.pro бесплатный кейс /levin ");
                        delay.reset();
                    }
                }
            }
            if (mode.is("WildGrief")) {
                if (antispam.get()) {
                    Random random = new Random();
                    String randomLetters = randomize(random);
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage("! Новыйй топовый сервачеeк levin.junglemine.pro  d" + randomLetters);
                        delay.reset();
                    }
                } else {
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage("! Новыйй топовый сервачек levin.junglemine.pro ck");
                        delay.reset();
                    }
                }
            }
            if (mode.is("HellHub")) {
                if (antispam.get()) {
                    Random random = new Random();
                    String randomLetters = randomize(random);
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage("! Новый топовый сервер levin.junglemine .pro бесплатный донатикс /levin " + randomLetters);
                        delay.reset();
                    }
                } else {
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage("! Новый топовый сервер levin.junglemine .pro бесплатный донатикс /levin");
                        delay.reset();
                    }
                }
            }
            if (mode.is("Личка")) {
                if (antispam.get()) {
                    Random random = new Random();
                    String randomLetters = randomize(random);
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        for (ScorePlayerTeam team : mc.world.getScoreboard().getTeams().stream().sorted(Comparator.comparing(Team::getName)).toList()) {
                            String name = team.getMembershipCollection().toString();
                            name = name.substring(1, name.length() - 1);

                            if (namePattern.matcher(name).matches()) {
                                mc.player.sendChatMessage("/msg " + name + " Новый топовый сервер levin.junglemine.pro бесплатный донатикс /levin " + randomLetters);
                            }
                            delay.reset();
                        }
                    }
                } else {
                    for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                        if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                            mc.player.sendChatMessage("! " + info.getGameProfile().getName().toString() + " Новый топовый сервер levin.junglemine.pro бесплатный донатикс /levin");
                            delay.reset();
                        }
                    }
                }
            }
            if (mode.is("ReallCraft")) {
                if (antispam.get()) {
                    Random random = new Random();
                    String randomLetters = randomize(random);
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage("! Лучший сервачёк апи - levin. junglemine. pro бесплатный донатикс /levin " + randomLetters);
                        delay.reset();
                    }
                } else {
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage("! Лучший сервачёк апи - levin. junglemine. pro бесплатный донатикс /levin");
                        delay.reset();
                    }
                }
            }
            if (mode.is("Custom")) {
                if (antispam.get()) {
                    Random random = new Random();
                    String randomLetters = randomize(random);
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage(custom.text.toString() + " " + randomLetters);
                        delay.reset();
                    }
                } else {
                    if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {
                        mc.player.sendChatMessage(custom.text.toString() + "");
                        delay.reset();
                    }
                }
            }

            if (mode.is("ProstoTrainer")) {
                Random random = new Random();
                String randomLetters = randomize(random);
                if (delay.hasTimeElapsed((long) timer.getValue().floatValue())) {

                    mc.player.sendChatMessage("! " + randomLetters + " " + messages.get(messageIndex) + " " + randomLetters);
                    delay.reset();

                    // Переходим к следующему сообщению в списке
                    messageIndex++;
                    if (messageIndex >= messages.size()) {
                        messageIndex = 0; // Возвращаемся к началу списка
                    }
                }
            }

        }
    }
    public static String randomize(Random random) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            char randomLetter = (char) ('a' + random.nextInt(26));
            sb.append(randomLetter);
        }
        return sb.toString();
    }

}