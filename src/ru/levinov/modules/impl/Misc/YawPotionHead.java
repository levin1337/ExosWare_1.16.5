package ru.levinov.modules.impl.Misc;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;

@FunctionAnnotation(
        name = "YawPotionHead",
        type = Type.Misc,
        desc = "Поворот головы вниз если бафы в руке"
)
public class YawPotionHead extends Function {

    private float previousPitch;
    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            ItemStack itemStack = mc.player.getHeldItemMainhand();
            if (itemStack.getItem() == Items.SPLASH_POTION) {
                float[] angles = new float[]{mc.player.rotationYaw, 90.0F};
                this.previousPitch = 90.0F;
                e.setYaw(angles[0]);
                e.setPitch(this.previousPitch);
                mc.player.rotationPitchHead = this.previousPitch;
                mc.player.rotationYawHead = angles[0];
                mc.player.renderYawOffset = angles[0];
            }
        }
    }
}