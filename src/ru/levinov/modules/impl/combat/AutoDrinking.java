package ru.levinov.modules.impl.combat;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.item.Items;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.SliderSetting;

@FunctionAnnotation(
        name = "AutoDrinking",
        type = Type.Combat,
        desc = "Авто зелье на определённом Здоровье"
)
public class AutoDrinking extends Function {
    private final SliderSetting healthThreshold = new SliderSetting("Здоровье", 13.0F, 4.0F, 20.0F, 0.05F);
    private boolean isEating;

    public AutoDrinking() {
        this.addSettings(new Setting[]{this.healthThreshold});
    }

    public void onEvent(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            this.handleEating();
        }

    }

    private void handleEating() {
        if (this.canEat()) {
            this.startEating();
        } else if (this.isEating) {
            this.stopEating();
        }

    }

    public boolean canEat() {
        float health = mc.player.getHealth();
        return !mc.player.getShouldBeDead() && mc.player.getHeldItemOffhand().getItem() == Items.POTION && health <= this.healthThreshold.getValue().floatValue() && !mc.player.getCooldownTracker().hasCooldown(Items.POTION);
    }

    private void startEating() {
        if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
            mc.gameSettings.keyBindUseItem.setPressed(true);
            this.isEating = true;
        }

    }

    private void stopEating() {
        mc.gameSettings.keyBindUseItem.setPressed(false);
        this.isEating = false;
    }
}
