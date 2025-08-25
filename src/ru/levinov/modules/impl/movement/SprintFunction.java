package ru.levinov.modules.impl.movement;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.impl.combat.Aura;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.util.movement.MoveUtil;

/**
 * @author levin1337
 * @since 03.06.2023
 */
@FunctionAnnotation(name = "AutoSprint", type = Type.Movement,desc = "Автоматический бег")
public class SprintFunction extends Function {


    public final ModeSetting mods = new ModeSetting("Выбор обхода", "Обычный", "Обычный","ReallyWorld");

    public BooleanOption keepSprint = new BooleanOption("Сохранять", true);
    public SprintFunction() {
        addSettings(mods,keepSprint);
    }

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventUpdate) {
            if (mods.is("ReallyWorld")) {
            }
            if (mods.is("Обычный")) {
                if (!mc.player.isSneaking() && !mc.player.collidedHorizontally) {
                    mc.player.setSprinting(MoveUtil.isMoving());
                }
            }
        }
    }
}
