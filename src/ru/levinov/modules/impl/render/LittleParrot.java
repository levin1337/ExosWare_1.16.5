package ru.levinov.modules.impl.render;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(name = "LittleParrot", type = Type.Render)
public class LittleParrot extends Function {
    private ParrotEntity parrot;
    private float wingFlapTimer = 0; // ������ ��� ������� ��������

    @Override
    public void onEvent(Event event) {
        // ���������, �������� �� ������� ������� �������� (��������, ���������)
        if (event instanceof EventRender eventRender) {
            // ���������, ���� �� ��� �������
            if (parrot == null) {
                // ������� ������� � ������������� ��� �� ����� ������
                parrot = new ParrotEntity(EntityType.PARROT, mc.world);
                mc.world.addEntity(parrot); // ��������� ������� � ���
                parrot.startRiding(mc.player); // "������" ������� �� ������
            }

            // ��������� ������� �������, ����� �� ������ ��������� ��� ������� ������
            if (parrot != null) {
                double playerX = mc.player.getPosX();
                double playerY = mc.player.getPosY() + mc.player.getEyeHeight() + 0.6f; // ������ ��� �������
                double playerZ = mc.player.getPosZ();

                // �������� ������ ����������� ������� ������
                Vector3d lookVec = mc.player.getLookVec();


                // ������������� ������� ������� ������� ����� � ����
                parrot.setPosition(playerX, playerY, playerZ); // ������������� ������� �������

                // ��������� �������� ������� ��������
           //     updateWingFlap();
            }
        }
    }

    private void updateWingFlap() {
        // �������� ������ ������� ��������
        wingFlapTimer += 0.1; // ����������� ������, ����� ��������� ��������

        // ������������� ��������� ������� �������� � ����������� �� �������
        if (wingFlapTimer >= 1.0) {
            wingFlapTimer = 0; // ���������� ������
        }

        // ������������� �������� ������� ��������
        parrot.isFlying(); // ���� ������ ������ 0.5, ������� "�����"
        parrot.isPartying();
    }
    @Override
    public void onDisable() {
        parrot.remove();
    }
}
