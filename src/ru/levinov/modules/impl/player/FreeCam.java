package ru.levinov.modules.impl.player;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventLivingUpdate;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.FreeCamera;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.movement.MoveUtil;

/**
 * @author levin1337
 * @since 19.06.2023
 */

@SuppressWarnings("all")
@FunctionAnnotation(name = "FreeCam", type = Type.Player,desc = "Просмотр через блоки")
public class FreeCam extends Function {
    private final SliderSetting speed = new SliderSetting(
            "Скорость по XZ",
            0.3f,
            0.1f,
            5.0f,
            0.05f
    );
    private final SliderSetting motionY = new SliderSetting(
            "Скорость Y",
            0.3f,
            0.1f,
            5.0f,
            0.05f
    );
    private Vector3d clientPosition = null;
    public FreeCamera player = null;

    public FreeCam() {
        addSettings(speed, motionY);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventLivingUpdate livingUpdateEvent) {
            if (player != null) {
                player.noClip = true;
                player.setOnGround(false);
                MoveUtil.setMotion(speed.getValue().floatValue(), player);

                if (Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown()) {
                    player.setPosition(player.getPosX(), player.getPosY() + motionY.getValue().floatValue(), player.getPosZ());
                }
                if (Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown()) {
                    player.setPosition(player.getPosX(), player.getPosY() - motionY.getValue().floatValue(), player.getPosZ());
                }
                player.motion.y = 0.0f;
            }
        }

        if (event instanceof EventPacket e) {
            if (e.getPacket() instanceof CPlayerPacket p) {
                if (p.moving) {
                    p.x = player.getPosX();
                    p.y = player.getPosY();
                    p.z = player.getPosZ();
                }
                p.onGround = player.isOnGround();
                if (p.rotating) {
                    p.yaw = player.rotationYaw;
                    p.pitch = player.rotationPitch;
                }
            }
        }
        if (event instanceof EventMotion motionEvent) {

            handleMotionEvent(motionEvent);
        }
        if (event instanceof EventRender && ((EventRender) event).isRender2D()) {
            handleRender2DEvent((EventRender) event);
        }
    }

    /**
     * Обработчик события onEnable.
     * Инициализирует фейкового игрока и добавляет его в мир.
     */
    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null) {
            return;
        }
        mc.player.setJumping(false);
        initializeFakePlayer();
        addFakePlayer();
        player.spawn();
        mc.player.movementInput = new MovementInput();
        mc.player.moveForward = 0;
        mc.player.moveStrafing = 0;
        mc.setRenderViewEntity(player);
    }

    /**
     * Обработчик события onDisable.
     * Удаляет фейкового игрока из мира.
     */
    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player == null) {
            return;
        }
        removeFakePlayer();
        mc.setRenderViewEntity(null);
        mc.player.movementInput = new MovementInputFromOptions(mc.gameSettings);

    }

    /**
     * Обработчик события EventLivingUpdate.
     * Устанавливает необходимые значения и состояния для игрока.
     */
    private void handleLivingUpdate() {

    }

    /**
     * Обработчик события EventMotion.
     * Отправляет пакет CPlayerPacket на сервер(если игрок находится на Sunrise) и отменяет событие.
     */
    private void handleMotionEvent(EventMotion motionEvent) {
        if (ClientUtil.isConnectedToServer("sunrise")) {
            if (mc.player.ticksExisted % 10 == 0) {
                mc.player.connection.sendPacket(new CPlayerPacket(mc.player.isOnGround()));
            }
        }
        motionEvent.setCancel(true);
    }

    /**
     * Обработчик события EventRender.
     * Отображает информацию о перемещении игрока в 2D рендере.
     */
    private void handleRender2DEvent(EventRender renderEvent) {
        MainWindow resolution = mc.getMainWindow();

        if (clientPosition == null) {
            return;
        }

        int xPosition = (int) (player.getPosX() - mc.player.getPosX());
        int yPosition = (int) (player.getPosY() - mc.player.getPosY());
        int zPosition = (int) (player.getPosZ() - mc.player.getPosZ());

        String position = "X:" + xPosition + " Y:" + yPosition + " Z:" + zPosition;


        Fonts.gilroyBold[16].drawCenteredStringWithOutline(renderEvent.matrixStack,
                position,
                resolution.getScaledWidth() / 2F,
                resolution.getScaledHeight() / 2F + 10,
                -1);
    }

    /**
     * Инициализирует фейкового игрока.
     * Устанавливает начальные значения позиции и углов поворота.
     */
    private void initializeFakePlayer() {
        clientPosition = mc.player.getPositionVec();
        player = new FreeCamera(1337228);
        player.copyLocationAndAnglesFrom(mc.player);
        player.rotationYawHead = mc.player.rotationYawHead;
    }

    /**
     * Добавляет фейкового игрока в мир и сохраняет текущую позицию игрока.
     */
    private void addFakePlayer() {
        clientPosition = mc.player.getPositionVec();
        mc.world.addEntity(1337228, player);
    }

    /**
     * Удаляет фейкового игрока из мира.
     * Восстанавливает состояния и позицию игрока.
     */
    private void removeFakePlayer() {
        resetFlying();
        mc.world.removeEntityFromWorld(1337228);
        player = null;
        clientPosition = null;
    }

    /**
     * Сбрасывает состояние полета игрока, если оно было выключено до работы модуля.
     */
    private void resetFlying() {

    }
}
