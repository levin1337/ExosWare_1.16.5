package ru.levinov.modules.impl.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.world.InventoryUtil;
import ru.levinov.util.world.WorldUtil;

/**
 * @author levin1337
 * @since 05.06.2023
 */
@FunctionAnnotation(name = "AutoTotem", type = Type.Combat,desc = "���� ������ ������ � ���� ��� ��������")
public class AutoTotemFunction extends Function {

    private final ModeSetting autototemMode = new ModeSetting("���", "RW", "RW", "Matrix");

    private final SliderSetting health = new SliderSetting("��������", 4.5f, 1.f, 20.f, 0.05f);
    private final BooleanOption swapBack = new BooleanOption("���������� �������", true);
    private final BooleanOption noBallSwitch = new BooleanOption("�� ����� ���� ��� � ����", false);
    private final MultiBoxSetting mode = new MultiBoxSetting("�����������",
            new BooleanOption("������� ������", true),
            new BooleanOption("���������", true),
            new BooleanOption("��������", false),
            new BooleanOption("�����", false),
            new BooleanOption("�������", true));


    private final SliderSetting HPElytra = (new SliderSetting("����� ������ ���� � ������� ��", 4.0F, 2.0F, 6.0F, 1F));

    private final SliderSetting radiusExplosion = new SliderSetting("��������� �� ��������", 6, 1, 8, 1).setVisible(() -> mode.get(1));
    private final SliderSetting radiusObs = new SliderSetting("��������� �� ���������", 6, 1, 8, 1).setVisible(() -> mode.get(2));
    private final SliderSetting radiusAnch = new SliderSetting("��������� �� �����", 6, 1, 8, 1).setVisible(() -> mode.get(2));

    int oldItem = -1;

    public AutoTotemFunction() {
        addSettings(autototemMode, mode, health, swapBack, noBallSwitch,HPElytra,radiusExplosion,radiusObs,radiusAnch);
    }

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventUpdate) {
            handleEventUpdate((EventUpdate) event);
        }
    }

    /**
     * ��������� ������� ����������.
     *
     * @param event ������� ����������.
     */
    private void handleEventUpdate(EventUpdate event) {
        // �������� ���� ������
        final int slot = InventoryUtil.getItemSlot(Items.TOTEM_OF_UNDYING);

        // ���� ����� ���� �� ������
        boolean handNotNull = !(mc.player.getHeldItemOffhand().getItem() instanceof AirItem);

        // ���������, ��������� �� ����� � ���� ������ (� ����� �� ���� ���)
        final boolean totemInHand = mc.player.getHeldItemOffhand()
                .getItem() == Items.TOTEM_OF_UNDYING
                || mc.player.getHeldItemMainhand()
                .getItem() == Items.TOTEM_OF_UNDYING;

        if (condition()) {
            // ���� ����������� �������
            // � ���� ��������� ���� ��� ������
            // � ����� �� ��������� � ����, �� ���������� ��� � ����
            if (slot >= 0 && !totemInHand) {
                InventoryUtil.moveItem(slot, 45, handNotNull);
                if (handNotNull && oldItem == -1) {
                    oldItem = slot;
                }
            }
        } else if (oldItem != -1 && swapBack.get()) {
            // ���� ������� �� �����������,
            // �� ��� �������� ���������� ���� ������ � ����������� ���� swapBack,
            // �� ���������� ����� �� ���������� �����
            InventoryUtil.moveItem(oldItem, 45, handNotNull);
            oldItem = -1;
        }
    }


    /**
     * �������� ���� �������
     */
    private boolean condition() {
        // ������������ ���������� ������������ ����� �� ������� ����������
        final float absorption = this.mode.get(0) && mc.player
                .isPotionActive(Effects.ABSORPTION)
                ? mc.player.getAbsorptionAmount()
                : 0.0f;

        // ��������� �������, ��� ������� ����� ������������ �����
        if (mc.player.getHealth() + absorption <= this.health.getValue().floatValue())
            return true;

        if (!this.isBall()) {
            if (this.checkCrystal())
                return true;

            if (this.checkObsidian())
                return true;

            if (this.checkAnchor())
                return true;
        }

        return this.checkHPElytra() ? true : this.checkFall();
    }

    /**
     * �������� ������� ��� ������������� ������ ��� �������.
     *
     * @return true, ���� ����� ������������ ����� ��� �������, ����� false.
     */
    private boolean checkFall() {
        if (!this.mode.get(4)) {
            return false;
        }

        if (mc.player.isElytraFlying()) {
            return false;
        }

        return mc.player.fallDistance > 10.0f;
    }


    private boolean checkHPElytra() {
     return ((ItemStack)mc.player.inventory.armorInventory.get(2)).getItem() == Items.ELYTRA && mc.player.getHealth() <= health.getValue().floatValue() + HPElytra.getValue().floatValue();
    }
    /**
     * ��������, ���� � ������ ��� � ����� ����.
     *
     * @return true, ���� � ������ ��� � ����� ����, ����� false.
     */
    private boolean isBall() {
        if (this.mode.get(3) && mc.player
                .fallDistance > 5.0f)
            return false;

        return this.noBallSwitch.get() && mc.player.getHeldItemOffhand()
                .getItem() instanceof SkullItem;
    }

    /**
     * �������� ������� ��� ������������� ������ ��� ������� ������������ ������ � �������.
     *
     * @return true, ���� ����� ������������ ����� ��� ������� ������������ ������ � �������, ����� false.
     */
    private boolean checkObsidian() {
        if (!mode.get(2))
            return false;

        return WorldUtil.TotemUtil
                .getBlock(radiusObs.getValue().floatValue(), Blocks.OBSIDIAN) != null;
    }

    /**
     * �������� ������� ��� ������������� ������ ��� ������� ������ ����������� � �������.
     *
     * @return true, ���� ����� ������������ ����� ��� ������� ������ ����������� � �������, ����� false.
     */
    private boolean checkAnchor() {
        if (!mode.get(3))
            return false;

        return WorldUtil.TotemUtil
                .getBlock(radiusAnch.getValue().floatValue(), Blocks.RESPAWN_ANCHOR) != null;
    }

    /**
     * �������� ������� ��� ������������� ������ ��� ������� ���������� ��� TNT � �������.
     *
     * @return true, ���� ����� ������������ ����� ��� ������� ���������� ��� TNT � �������, ����� false.
     */
    private boolean checkCrystal() {
        if (!mode.get(1))
            return false;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof EnderCrystalEntity
                    && mc.player.getDistance(entity) <= radiusExplosion.getValue().floatValue())
                return true;

            if ((entity instanceof TNTEntity || entity instanceof TNTMinecartEntity)
                    && mc.player.getDistance(entity) <= radiusExplosion.getValue().floatValue())
                return true;
        }
        return false;
    }


    /**
     * ����� ��������� ����������.
     */
    private void reset() {
        this.oldItem = -1;
    }


    @Override
    protected void onEnable() {
        reset();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        reset();
        super.onDisable();
    }
}
