package ru.levinov.modules.impl.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.events.Event;
import ru.levinov.events.impl.game.EventMouseTick;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.ClientUtil;

/**
 * @author levin1337
 * @since 09.06.2023
 */
@FunctionAnnotation(name = "MiddleClickFriend", type = Type.Util,desc = "Друзья на колесо мыши",
        keywords = {"MCF"})
public class MiddleClickFriendFunction extends Function {

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMouseTick e) {
            handleMouseTickEvent(e);
        }
    }

    /**
     * Обрабатывает событие нажатия кнопки мыши.
     *
     * @param event событие нажатия кнопки мыши
     */
    private void handleMouseTickEvent(EventMouseTick event) {
        if (event.getButton() == 2 && mc.pointedEntity instanceof LivingEntity) {
            String entityName = mc.pointedEntity.getName().getString();
            if (Managment.FRIEND_MANAGER.isFriend(entityName)) {
                Managment.FRIEND_MANAGER.removeFriend(entityName);
                displayRemoveFriendMessage(entityName);
            } else {
                Managment.FRIEND_MANAGER.addFriend(entityName);
                displayAddFriendMessage(entityName);
            }
        }
    }

    /**
     * Отображает сообщение о удалении друга.
     *
     * @param friendName имя друга
     */
    private void displayRemoveFriendMessage(String friendName) {
        ClientUtil.sendMesage(TextFormatting.RED + "Удалил " + TextFormatting.RESET + friendName + " из друзей!");
    }

    /**
     * Отображает сообщение о добавлении друга.
     *
     * @param friendName имя друга
     */
    private void displayAddFriendMessage(String friendName) {
        ClientUtil.sendMesage(TextFormatting.GREEN + "Добавил " + TextFormatting.RESET + friendName + " в друзья!");
    }
}
