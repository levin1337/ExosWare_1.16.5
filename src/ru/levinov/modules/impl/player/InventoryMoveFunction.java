package ru.levinov.modules.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.*;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.world.InventoryUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author levin1337
 * @since 04.06.2023
 */
@FunctionAnnotation(name = "InventoryMove", type = Type.Player,desc = "Ходьба в инвентаре")
public class InventoryMoveFunction extends Function {
    private BooleanOption bypass = new BooleanOption("Обход FunTime", false);
    private final List<IPacket<?>> packet = new ArrayList<>();
    public TimerUtil wait = new TimerUtil();


    public InventoryMoveFunction() {
        addSettings(bypass);
    }
    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventUpdate) {
            handleEventUpdate();
        }
        if(bypass.get()) {
            if (event instanceof EventUpdate) {
                onUpdate();
            } else if (event instanceof EventPacket ep) {
                onPacket(ep);
            }
        }else{
            if (event instanceof EventUpdate) {
                onUpdate();
            }
        }
    }

    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof CClickWindowPacket p && MoveUtil.isMoving()) {
            if (mc.currentScreen instanceof InventoryScreen || mc.currentScreen instanceof ChestScreen) {
                packet.add(p);
                e.setCancel(true);
            }
        }
    }

    /**
     * Обрабатывает событие типа EventUpdate.
     */
    private void handleEventUpdate() {
        // Создаем массив с соответствующими игровыми клавишами
        final KeyBinding[] keys = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint};

        // Проверяем, отображается ли экран чата  или экран редактирования знака
        if (mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof EditSignScreen)
            return;

        // Проходимся по массиву клавиш
        for (KeyBinding keyBinding : keys) {
            // Устанавливаем состояние клавиши на основе текущего состояния
            keyBinding.setPressed(InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode()));
        }

    }

    private void updateKeyBindingState(KeyBinding[] keyBindings) {
        for (KeyBinding keyBinding : keyBindings) {
            boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
            keyBinding.setPressed(isKeyPressed);
        }
    }
    public void onUpdate() {
        if (mc.player != null) {

            final KeyBinding[] pressedKeys = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                    mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump,
                    mc.gameSettings.keyBindSprint};
            if (!wait.hasTimeElapsed(400)) {
                for (KeyBinding keyBinding : pressedKeys) {
                    keyBinding.setPressed(false);
                }
                return;
            }


            if (mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof EditSignScreen) {
                return;
            }

            updateKeyBindingState(pressedKeys);
            if(bypass.get()) {
                if (!(mc.currentScreen instanceof InventoryScreen) && !packet.isEmpty() && MoveUtil.isMoving()) {
                    new Thread(() -> {
                        wait.reset();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        for (IPacket p : packet) {
                            mc.player.connection.sendPacket(p);
                        }
                        packet.clear();
                    }).start();
                }
            } else {
                KeyBinding[] keys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};
                if (!(mc.currentScreen instanceof ChatScreen) && !(mc.currentScreen instanceof EditSignScreen)) {
                    KeyBinding[] var2 = keys;
                    int var3 = keys.length;

                    for (int var4 = 0; var4 < var3; ++var4) {
                        KeyBinding keyBinding = var2[var4];
                        keyBinding.setPressed(InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode()));
                    }
                }
            }
        }
    }
}