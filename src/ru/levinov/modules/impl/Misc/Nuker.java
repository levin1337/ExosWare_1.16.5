package ru.levinov.modules.impl.Misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

@FunctionAnnotation(
        name = "Nuker",
        type = Type.Misc,
        desc = "Ломание блоков в радиусе"
)
public class Nuker extends Function {
    private final ModeSetting mode2 = new ModeSetting("Мод ломания", "Авто", "Авто", "Shift","Таймер");
    private final ModeSetting mode = new ModeSetting("Мод наводки", "Визуально","Наводка", "Визуально");

    private final SliderSetting radius = new SliderSetting("Радиус", 2F, 1.0F, 4, 1F);

    private final BooleanOption bloom = (new BooleanOption("Подсветка текущего блока", true));
    private final BooleanOption blockbreak = new BooleanOption("Не ломать под собой", true);

    private final SliderSetting timebreak = new SliderSetting("Время ломания", 100f, 10f, 5000f, 10F);


    private final TimerUtil timerHelper = new TimerUtil();

    public Nuker() {
        addSettings(mode2,mode, radius, bloom,blockbreak,timebreak);
    }

    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            for (int y = (int) (mc.player.getPosY()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
                for (int x = (int) (mc.player.getPosX() - (double) this.radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                    for (int z = (int) (mc.player.getPosZ() - (double) this.radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = mc.world.getBlockState(pos);
                        if (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.LAVA && state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.BARRIER) {
                            if (blockbreak.get()) {
                                if (pos.getY() == (int) mc.player.getPosY() - 1 && (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK)) {
                                    continue; // Пропускаем блок, если игрок на нем стоит
                                }
                            }
                            BlockPos wheatPos = this.findNearestWheat(mc.player.getPosition(), (int) radius.getValue().floatValue());
                            if (wheatPos != null) {
                                float[] rotation = rots(new Vector3d((double) wheatPos.getX() + 0.5, (double) wheatPos.getY() + 0.5, (double) wheatPos.getZ() + 0.5));
                                if (mode.is("Визуально")) {
                                    mc.player.rotationYawHead = rotation[0];
                                    mc.player.renderYawOffset = rotation[1];
                                    mc.player.rotationPitchHead = rotation[1];
                                }
                                if (mode.is("Наводка")) {
                                    mc.player.rotationYaw = rotation[0];
                                    mc.player.rotationPitch = rotation[1];
                                }

                                if (mode2.is("Авто")) {
                                    if (timerHelper.hasTimeElapsed((long) mc.player.getDigSpeed(mc.world.getBlockState(new BlockPos((double) ((int) wheatPos.x), (double) ((int) wheatPos.y), (double) ((int) wheatPos.z))).getBlock().getDefaultState()))) {
                                        mc.player.swingArm(Hand.MAIN_HAND);
                                        mc.playerController.onPlayerDamageBlock(new BlockPos(wheatPos.x, wheatPos.y, wheatPos.z), mc.player.getHorizontalFacing());
                                        timerHelper.reset();
                                    }
                                }
                                if (mode2.is("Shift")) {
                                    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                                        if (timerHelper.hasTimeElapsed(1)) mc.player.getDigSpeed(mc.world.getBlockState(new BlockPos((double) ((int) wheatPos.x), (double) ((int) wheatPos.y), (double) ((int) wheatPos.z))).getBlock().getDefaultState()); {
                                            mc.player.swingArm(Hand.MAIN_HAND);
                                            mc.playerController.onPlayerDamageBlock(new BlockPos(wheatPos.x, wheatPos.y, wheatPos.z), mc.player.getHorizontalFacing());
                                            timerHelper.reset();
                                        }
                                    }
                                }
                                if (mode2.is("Таймер")) {
                                    if (timerHelper.hasTimeElapsed((long) timebreak.getValue().floatValue())) mc.player.getDigSpeed(mc.world.getBlockState(new BlockPos((double) ((int) wheatPos.x), (double) ((int) wheatPos.y), (double) ((int) wheatPos.z))).getBlock().getDefaultState()); {
                                        mc.player.swingArm(Hand.MAIN_HAND);
                                        mc.playerController.onPlayerDamageBlock(new BlockPos(wheatPos.x, wheatPos.y, wheatPos.z), mc.player.getHorizontalFacing());
                                        timerHelper.reset();
                                    }
                                }
                            } else {
                            }
                        }
                    }
                }
            }
        }
        if (event instanceof EventRender) {
            for (int y = (int) (mc.player.getPosY()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
                for (int x = (int) (mc.player.getPosX() - (double) this.radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                    for (int z = (int) (mc.player.getPosZ() - (double) this.radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = mc.world.getBlockState(pos);
                        BlockPos wheatPos = this.findNearestWheat(mc.player.getPosition(), (int) radius.getValue().floatValue());
                        if (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.LAVA && state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.BARRIER) {
                            if (bloom.get()) {
                                if (pos.getY() == (int) mc.player.getPosY() - 1 && (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK)) {
                                    continue; // Пропускаем блок, если игрок на нем стоит
                                }
                                RenderUtil.Render3D.drawBlockBox(wheatPos, ColorUtil.getColorStyle(360));
                            }
                        }
                    }
                }
            }
        }
    }

    private BlockPos findNearestWheat(BlockPos position, int radius) {
        for (int y = (int) (mc.player.getPosY()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
            for (int x = (int) (mc.player.getPosX() - (double) this.radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                for (int z = (int) (mc.player.getPosZ() - (double) this.radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.LAVA && state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.BARRIER) {
                        if (blockbreak.get()) {
                            if (pos.getY() == (int) mc.player.getPosY() - 1 && (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK)) {
                                continue; // Пропускаем блок, если игрок на нем стоит
                            }
                        }
                        return pos;
                    }
                }
            }
        }

        return null;
    }
    public static float[] rots(Vector3d vec) {
        double x = vec.x - mc.player.getPosX();
        double y = vec.y - (mc.player.getPosY());
        double z = vec.z - mc.player.getPosZ();
        double u = (double) MathHelper.sqrt(x * x + z * z);
        float u2 = (float)(MathHelper.atan2(z, x) * 57.29577951308232 - 90.0);
        float u3 = (float)(-MathHelper.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};
    }

}