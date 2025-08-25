package ru.levinov.modules.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ModeSetting;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import static ru.levinov.modules.impl.movement.SpeedFunction.getElytra;

@FunctionAnnotation(name = "NoFall", type = Type.Player,desc = "������� ���� �� �������")
public class NoFall extends Function {
    private final ModeSetting mode = new ModeSetting("���", "�����", "�����", "������", "�����2","Elytra");


    public NoFall() {
        addSettings(mode);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            if (mode.is("�����")) {
                if (mc.player.ticksExisted % 3 == 0 && mc.player.fallDistance > 3) {
                    e.setY(e.getY() + 0.2f);
                }
            }
            if (mode.is("������")) {
                BlockPos waterBlockPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
                Block waterBlock = mc.world.getBlockState(waterBlockPos).getBlock();
                if (waterBlock == Blocks.GRASS && waterBlock == Blocks.DIRT && waterBlock == Blocks.STONE && waterBlock == Blocks.SAND && waterBlock == Blocks.OAK_LOG) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), true));
                }
            }
            if (mode.is("�����2")) {
                if (mc.player.fallDistance >= 2.0F) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), true));
                }
            }
            if (mode.is("Elytra")) {
                // ���������, ���� ����� ������
                if (mc.player.fallDistance >= 0.6F) {
                    int elytra = getElytra(); // �������� ������ ������ � ���������
                    int chestplate = getChestplate(); // �������� ������ ���������� � ���������

                    // ���� ����� �� � ���� ��� ���� � ������ �������
                    if (!mc.player.isInWater() && !mc.player.isInLava() && elytra != -1) {
                        // �������� ������
                        if (elytra != -2) {
                            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player); // ����� ������ �� ���������
                            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player); // �������� ������
                        }
                        mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING)); // ���������� ����� � ������ �������
                        mc.player.startFallFlying(); // �������� ������ � ��������

                        // ������������ � ����������
                        if (chestplate != -1) {
                            if (elytra != -2) {
                                mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player); // ������� ������
                                mc.playerController.windowClick(0, chestplate, 1, ClickType.PICKUP, mc.player); // �������� ���������
                            }
                            toggle();
                        }

                    }
                }
            }
        }
    }
    private int getChestplate() {
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlotType.CHEST) {
                return i; // ���������� ������ ����������
            }
        }
        return -1; // ��������� �� ������
    }
}
