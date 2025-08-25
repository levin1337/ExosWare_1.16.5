package ru.levinov.modules.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.math.RayTraceUtil;
import ru.levinov.util.misc.TimerUtil;

import java.util.Iterator;

@FunctionAnnotation(
        name = "CrystalOptimizer",
        type = Type.Combat,
        desc = "Быстрое взаимодействие с кристаллами"
)
public class CrystalOptimizer extends Function {
    private final TimerUtil timerUtil = new TimerUtil();

    public void onEvent(Event event) {
        if (event instanceof EventUpdate && this.timerUtil.hasTimeElapsed(0L)) {
            ItemStack itemStack = mc.player.getHeldItemMainhand();
            if (itemStack.getItem() == Items.END_CRYSTAL) {
                mc.rightClickDelayTimer = 0;
            }

            timerUtil.reset();
            Iterator<Entity> entities = mc.world.getAllEntities().iterator();

            while(entities.hasNext()) {
                Entity entity = (Entity)entities.next();
                if (entity instanceof EnderCrystalEntity && (double)mc.player.getDistance(entity) <= 7.0 && RayTraceUtil.getMouseOver(entity, mc.player.rotationYaw, mc.player.rotationPitch, 7.0) == entity) {
                    float xPosCrystal = (float)entity.getPosY();
                    float xPosPlayer = (float)mc.player.getPosY() + 0.5F;
                    if (!(xPosCrystal < xPosPlayer)) {
                        mc.playerController.attackEntity(mc.player, entity);
                        mc.player.swingArm(Hand.MAIN_HAND);
                        break;
                    }
                }
            }
        }

    }
}
