package ru.levinov.modules.impl.render;

import net.minecraft.potion.Effects;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventOverlaysRender;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;

/**
 * @author levin1337
 * @since 09.06.2023
 */

@FunctionAnnotation(name = "NoRender", type = Type.Render)
public class NoRenderFunction extends Function {

    public MultiBoxSetting element = new MultiBoxSetting("��������",
            new BooleanOption("����� �� ������", true),
            new BooleanOption("������ �������", true),
            new BooleanOption("����� �����", false),
            new BooleanOption("�������", false),
            new BooleanOption("������", false),
            new BooleanOption("�����", true),
            new BooleanOption("�����", true),
            new BooleanOption("�����", true));
    public final BooleanOption effectcamera = new BooleanOption("����������� ������", false);


    public NoRenderFunction() {
        addSettings(element,effectcamera);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventOverlaysRender) {
            handleEventOverlaysRender((EventOverlaysRender) event);
        } else if (event instanceof EventUpdate) {
            handleEventUpdate((EventUpdate) event);
        }
    }

    /**
     * ������������ �������, ��������� � ���������� ��������.
     * ���� ��� ������� ������������� ������������� �������� � ���� ������� �������,
     * ������������� ���� ������ ��������� �������.
     *
     * @param event ������� ��������� �������
     */
    private void handleEventOverlaysRender(EventOverlaysRender event) {
        EventOverlaysRender.OverlayType overlayType = event.getOverlayType();

        boolean cancelOverlay = switch (overlayType) {
            case FIRE_OVERLAY -> element.get(0);
            case BOSS_LINE -> element.get(2);
            case SCOREBOARD -> element.get(3);
            case TITLES -> element.get(4);
            case TOTEM -> element.get(5);
            case FOG -> element.get(7);
        };

        if (cancelOverlay) {
            event.setCancel(true);
        }
    }

    /**
     * ������������ ������� ���������� ����.
     * ���� ������������ ������� �������, ��������� ������������ ��������.
     * - ���� ������� 6 ������� � � ���� ���� �����, ��������� ����� � �����.
     * - ���� ������� 1 ������� � � ������ ������� ������� ��� �������,
     *   ������� ������� ������� � ������� � ������.
     *
     * @param event ������� ���������� ����
     */
    private void handleEventUpdate(EventUpdate event) {
        
        boolean isRaining = element.get(6) && mc.world.isRaining();

        boolean hasEffects = element.get(1) &&
                (mc.player.isPotionActive(Effects.BLINDNESS)
                || mc.player.isPotionActive(Effects.NAUSEA));

        if (isRaining) {
            mc.world.setRainStrength(0);
            mc.world.setThunderStrength(0);
        }

        if (hasEffects) {
            mc.player.removePotionEffect(Effects.NAUSEA);
            mc.player.removePotionEffect(Effects.BLINDNESS);
        }
    }
}
