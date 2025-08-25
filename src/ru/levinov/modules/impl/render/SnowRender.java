package ru.levinov.modules.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.render.RenderUtil;

import java.util.Random;

@FunctionAnnotation(name = "ParticleSnow", type = Type.Render)
public class SnowRender extends Function {

    private final ModeSetting mode = new ModeSetting("Метод ", "Type-1","Type-1", "Type-2", "Type-3");
    public SliderSetting quantity = new SliderSetting("Количество", 1, 1f, 5f, 1f);
    private final BooleanOption metel = new BooleanOption("Метель", true).setVisible(() -> mode.is("Type-1"));


    private final Random random = new Random();

    public SnowRender() {
        addSettings(mode,quantity,metel);
    }
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender renderEvent) {
            double range = 15;
            PlayerEntity player = Minecraft.getInstance().player;
            if (player == null || player.world == null) return;

            double x = player.getPosX();
            double y = player.getPosY() - 15;
            double z = player.getPosZ();

            for (int i = 0; i < quantity.getValue().floatValue(); i++) {
                double particleX = x + (random.nextDouble() - 0.5) * range * 2;
                double particleY = y + 15 + (random.nextDouble() * 10);
                double particleZ = z + (random.nextDouble() - 0.5) * range * 2;
                double motionY = -0.1 - (random.nextDouble() * 0.05);
                if (mode.is("Type-1")) {
                    if (metel.get()) {
                        mc.world.addParticle(ParticleTypes.WHITE_ASH, particleX, particleY, particleZ, 2, motionY, 0);
                    } else {
                        mc.world.addParticle(ParticleTypes.WHITE_ASH, particleX, particleY, particleZ, 0, motionY, 0);
                    }
                }
                if (mode.is("Type-2")) {
                    mc.world.addParticle(ParticleTypes.CLOUD, particleX, particleY, particleZ, 0, motionY, 0);
                }
                if (mode.is("Type-3")) {
                    mc.world.addParticle(ParticleTypes.SNEEZE, particleX, particleY, particleZ, 0, motionY, 0);
                }
            }
        }
    }
}
