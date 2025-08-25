package ru.levinov.modules.impl.Misc;

import net.minecraft.block.Blocks;
import net.minecraft.client.particle.TotemOfUndyingParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.util.misc.TimerUtil;

@FunctionAnnotation(name = "TeleportItem", type = Type.Misc,desc = "Телепорт к предметам на земле")
public class TeleportItem extends Function {

    public final MultiBoxSetting elements = new MultiBoxSetting("Тепать к",
            new BooleanOption("Шары", true),
            new BooleanOption("Элитры", true),
            new BooleanOption("Эндер-жемчюги", false),
            new BooleanOption("Любые-Мечи", true),
            new BooleanOption("Броня", true),
            new BooleanOption("Зелья", true)
    );


    public TeleportItem() {
        addSettings(elements);
    }
    private final TimerUtil timerHelper = new TimerUtil();

    public void onEvent(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof ItemEntity) {
                    ItemStack itemStack = ((ItemEntity) entity).getItem();
                    //Тута можете сделать выбор там отмычек или чего другого я не сделал так
                    // я делаю под другой сервус
                    if (itemStack.getItem() instanceof SkullItem && elements.get(0)
                            || itemStack.getItem() instanceof ElytraItem && elements.get(1)
                            || itemStack.getItem() instanceof EnderPearlItem && elements.get(2)
                            || itemStack.getItem() instanceof SwordItem && elements.get(3)
                            || itemStack.getItem() instanceof ArmorItem && elements.get(4)
                            || itemStack.getItem() instanceof PotionItem && elements.get(5)) { // Проверка на ебло

                        //Позиция
                        BlockPos headPos = new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                        // Головка на земле
                        if (mc.world.getBlockState(headPos.down()).getBlock() != Blocks.AIR ||
                                mc.world.getBlockState(headPos.down()).getBlock() == Blocks.GRASS_BLOCK ||
                                mc.world.getBlockState(headPos.down()).getBlock() == Blocks.DIRT || // Земля
                                mc.world.getBlockState(headPos.down()).getBlock() == Blocks.STONE ||
                                mc.world.getBlockState(headPos.down()).getBlock() == Blocks.SAND || // Песок
                                mc.world.getBlockState(headPos.down()).getBlock() == Blocks.GRAVEL // Гравий
                        ) {
                            float x = headPos.getX();
                            float y = headPos.getY();
                            float z = headPos.getZ();
                            // Радиус е
                            if (mc.player.getDistance(entity) <= 100) {
                                int i;
                                for (i = 0; i < 19; ++i) {
                                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), false));
                                }

                                for (i = 0; i < 19; ++i) {
                                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
                                }

                                mc.player.setPosition(x, y, z);
                            }
                            toggle();
                        }
                    }
                }
            }
        }
    }

    public float[] rotations(Entity entity) {
        double x = entity.getPosX() - mc.player.getPosX();
        double y = entity.getPosY() - mc.player.getPosY() - 1.5;
        double z = entity.getPosZ() - mc.player.getPosZ();

        double u = Math.sqrt(x * x + z * z);
        float u2 = (float) (Math.atan2(z, x) * 57.29577951308232 - 90.0);
        float u3 = (float) (-Math.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};

    }
}
