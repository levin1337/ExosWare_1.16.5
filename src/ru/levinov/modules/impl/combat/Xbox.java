package ru.levinov.modules.impl.combat;

import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ColorSetting;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;

/**
 * @author levin1337
 * @since 07.06.2023
 */
@FunctionAnnotation(name = "Hitb��", type = Type.Combat,desc = "���������� ���-����� ������",
        keywords = {"�������","Hitbox"})
public class Xbox extends Function {

    public final SliderSetting size = new SliderSetting("������", 0.2f, 0.f, 3.5f, 0.05f);
    public final BooleanOption invisible = new BooleanOption("���������", false);

    public final BooleanOption colorHit = new BooleanOption("����", true);

    public final ModeSetting mode = new ModeSetting("����� �����", "������������","������������", "����").setVisible(() -> colorHit.get());
    public ColorSetting color = new ColorSetting("���� ���-�����", -1).setVisible(() -> mode.is("����"));
    public final SliderSetting alpha = new SliderSetting("������������", 1f, 1f, 255f, 1f);

    public Xbox() {
        addSettings(size, invisible,colorHit,mode,color,alpha);
    }

    @Override
    public void onEvent(final Event event) {
        handleEvent(event);
    }

    /**
     * ������������ �������.
     */
    private void handleEvent(Event event) {
        // ��������, �������� �� ������� ����� EventRender � �������� �� 3D-�����������
        if (!(event instanceof EventRender && ((EventRender) event).isRender3D()))
            return;

        // ��������, ������� �� ����� �����������
        if (invisible.get())
            return;

        // ���������� ������������� ������ �������� ��� �������
        adjustBoundingBoxesForPlayers();
    }

    /**
     * ����������� ������� ������ ��� ��������� ������.
     */
    private void adjustBoundingBoxesForPlayers() {
        // ������� ���� ������� � ����
        for (PlayerEntity player : mc.world.getPlayers()) {
            // ��������, ����� �� ���������� ������� ������ ��� ������������� ��������
            if (shouldSkipPlayer(player))
                continue;

            // ���������� ��������� ������� � ��������� ������ �������� ��� ������
            float sizeMultiplier = this.size.getValue().floatValue() * 2.5F;
            setBoundingBox(player, sizeMultiplier);
        }
    }

    /**
     * �������� �� ��������� ������
     */
    private boolean shouldSkipPlayer(PlayerEntity player) {
        // ��������, ����� �� ���������� ������� ������ ��� ������������� ��������
        // ����� ������������, ���� ��� ������� ����� (mc.player) ��� ���� ����� �����
        return player == mc.player || !player.isAlive();
    }

    /**
     * ������������� ����� ������ ��� ��������
     */
    private void setBoundingBox(Entity entity, float size) {
        // ���������� ������ �������� ��� �������� � ��������� ��
        AxisAlignedBB newBoundingBox = calculateBoundingBox(entity, size);
        entity.setBoundingBox(newBoundingBox);
    }

    /**
     * ���������� ��������� ����������� � ������������ ����� �������� ��� �������� � ��������
     * � ����������� ������ �������� ��������
     */
    private AxisAlignedBB calculateBoundingBox(Entity entity, float size) {
        // ���������� ��������� ����������� � ������������ ����� �������� ��� ��������
        double minX = entity.getPosX() - size;
        double minY = entity.getBoundingBox().minY;
        double minZ = entity.getPosZ() - size;
        double maxX = entity.getPosX() + size;
        double maxY = entity.getBoundingBox().maxY;
        double maxZ = entity.getPosZ() + size;

        // �������� � ����������� ������ �������� ��������
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}

