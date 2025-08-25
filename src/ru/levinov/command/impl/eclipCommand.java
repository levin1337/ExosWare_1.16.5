package ru.levinov.command.impl;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.math.NumberUtils;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.world.InventoryUtil;

@Cmd(
        name = "eclip",
        description = "Телепорт с помощью элитры."
)
public class eclipCommand extends Command {
    private ItemStack oldStack = null;
    float y = 0.0F;

    public eclipCommand() {
    }

    public void run(String[] args) throws Exception {
        ItemStack itemStack = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        int elytra = InventoryUtil.getItemSlot(Items.ELYTRA);
        if (!NumberUtils.isNumber(args[1])) {
            this.error();
        } else {
            this.y = Float.parseFloat(args[1]);
            if (elytra == -1) {
                ClientUtil.sendMesage(TextFormatting.BLUE + "Для данного Модуля нужна элитра в инвентаре!");
                this.error();
            } else {
                //Надеваем элики
                if (this.reasonToEquipElytra(itemStack)) {
                    ItemStack n = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                    oldStack = n.copy();
                    InventoryUtil.moveItem(elytra, 6, true);
                }
                //ПАКЕТЫ КЛИПА
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));

                int i;
                for(i = 0; i < 19; ++i) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
                }

                for(i = 0; i < 19; ++i) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + Double.parseDouble(args[1]), mc.player.getPosZ(), false));
                }

                mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() + Double.parseDouble(args[1]), mc.player.getPosZ());
                //возращение грудака
                if (oldStack != null) {
                    int oldStackSlot = InventoryUtil.getItemSlot(oldStack.getItem());
                    InventoryUtil.moveItem(oldStackSlot, 6, true);
                    oldStack = null;
                }

            }
        }
    }

    private boolean reasonToEquipElytra(ItemStack stack) {
        return stack.getItem() != Items.ELYTRA;
    }

    public void error() {
        this.sendMessage(TextFormatting.GRAY + "Ошибка в использовании" + TextFormatting.WHITE + ":");
        this.sendMessage(".eclip Y" + TextFormatting.GRAY);
    }
}
