package ru.levinov.modules.impl.util;

import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.system.CallbackI;
import ru.levinov.events.Event;
import ru.levinov.events.impl.game.EventMouseTick;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.impl.combat.Aura;
import ru.levinov.modules.impl.player.GappleCooldownFunction;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.world.InventoryUtil;

/**
 * @author levin1337
 * @since 07.06.2023
 */
@FunctionAnnotation(name = "MiddleClickPearl", type = Type.Util,desc = "Жемчюг на колесо мыши",
        keywords = {"MCP"})
public class MiddleClickPearlFunction extends Function {
    private BooleanOption inventory = new BooleanOption("Кидать из инва", true);
    private BooleanOption legit = new BooleanOption("Легит", false);


    public MiddleClickPearlFunction() {
        addSettings(inventory,legit);
    }

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventMouseTick mouseTick) {
            if (mouseTick.getButton() == 2) {
                handleMouseTickEvent();
            }
        }
    }

    /**
     * Обрабатывает событие EventMouseTick при нажатии на правую кнопку мыши (кнопка с кодом 2).
     */
    private void handleMouseTickEvent() {
        if (!mc.player.getCooldownTracker().hasCooldown(Items.ENDER_PEARL) && InventoryUtil.getPearls() >= 0) {
            sendHeldItemChangePacket(InventoryUtil.getPearls());

            sendPlayerRotationPacket(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.isOnGround());
            useItem(Hand.MAIN_HAND);

            sendHeldItemChangePacket(mc.player.inventory.currentItem);
        } else {
            if (inventory.get()) {
                //     ClientUtil.sendMesage(TextFormatting.RED + "У вас нету эндер-жемчюгов беру из инвентаря");
                if (InventoryUtil.getItemSlot(Items.ENDER_PEARL) == -1) {
                    //  ClientUtil.sendMesage(TextFormatting.RED + "У вас нету эндер-жемчюгов");
                } else {
                    InventoryUtil.inventorySwapClick1337(Items.ENDER_PEARL, false);
                }
            }
        }
    }

    /**
     * Отправляет пакет смены активного предмета.
     *
     * @param itemSlot Слот предмета
     */
    private void sendHeldItemChangePacket(int itemSlot) {
        if (legit.get()) {
            mc.player.inventory.currentItem = itemSlot;
        } else {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(itemSlot));
        }
        GappleCooldownFunction cooldown = Managment.FUNCTION_MANAGER.gappleCooldownFunction;
        GappleCooldownFunction.ItemEnum itemEnum = GappleCooldownFunction.ItemEnum.getItemEnum(Items.ENDER_PEARL);

        if (cooldown.state && itemEnum != null && cooldown.isCurrentItem(itemEnum)) {
            cooldown.lastUseItemTime.put(itemEnum.getItem(), System.currentTimeMillis());
        }
    }

    /**
     * Отправляет пакет поворота игрока, дабы кинуть перл не в сторону ротации киллауры.
     *
     * @param yaw      значение поворота по горизонтали
     * @param pitch    значение поворота по вертикали
     * @param onGround значения находится ли игрок на земле
     */
    private void sendPlayerRotationPacket(float yaw, float pitch, boolean onGround) {
        if (Managment.FUNCTION_MANAGER.auraFunction.target != null) {
            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(yaw, pitch, onGround));
        }
    }

    /**
     * Отправляет пакет использования предмета в главной руке.
     *
     * @param hand Активная рука
     */
    private void useItem(Hand hand) {
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(hand));
        mc.player.swingArm(hand);
    }
}
