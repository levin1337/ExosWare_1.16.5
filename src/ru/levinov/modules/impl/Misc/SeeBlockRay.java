package ru.levinov.modules.impl.Misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

@FunctionAnnotation(
        name = "XRay",
        type = Type.Misc,
        desc = "Позволит видеть руды через блоки"
)
public class SeeBlockRay extends Function {
    //Совет у кого слабый пк не ставить больше 30-35 будет лагать
    public SliderSetting radius = new SliderSetting("Радиус", 25.0F, 0.0F, 50.0F, 1.0F);
    private final BooleanOption ygol = new BooleanOption("Уголь", false);
    private final BooleanOption iron = new BooleanOption("Железо", true);
    private final BooleanOption redstone = new BooleanOption("Редстоун", false);
    private final BooleanOption golda = new BooleanOption("Золото", false);
    private final BooleanOption emerald = new BooleanOption("Изумруды", false);
    private final BooleanOption diamond = new BooleanOption("Алмаз", false);
    private final BooleanOption ancient_debris = new BooleanOption("Незерит", true);
    private final BooleanOption lapise = new BooleanOption("Лазурит", false);

    private final BooleanOption boneblock = new BooleanOption("Костяной блок", false);

    public SeeBlockRay() {
        addSettings(radius, ygol, iron, redstone, golda, emerald, diamond, ancient_debris, lapise,boneblock);
    }

    public void onEvent(Event event) {
        if (event instanceof EventRender) {
            for(int x = (int)(mc.player.getPosX() - (double)this.radius.getValue().floatValue()); (double)x <= mc.player.getPosX() + (double)this.radius.getValue().floatValue(); ++x) {
                for(int y = (int)(mc.player.getPosY() - (double)this.radius.getValue().floatValue()); (double)y <= mc.player.getPosY() + (double)this.radius.getValue().floatValue(); ++y) {
                    for(int z = (int)(mc.player.getPosZ() - (double)this.radius.getValue().floatValue()); (double)z <= mc.player.getPosZ() + (double)this.radius.getValue().floatValue(); ++z) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = mc.world.getBlockState(pos);
                        BlockPos check;
                        if (this.ancient_debris.get() && state.getBlock() == Blocks.ANCIENT_DEBRIS) {
                            check = this.carbanara(mc.player.getPosition(), (int)this.radius.getValue().floatValue());
                            if (check != null) {
                                RenderUtil.Render3D.drawBlockBox(pos, ColorUtil.rgba(128, 255, 128, 255));
                            }
                        }

                        if (this.ygol.get() && state.getBlock() == Blocks.COAL_ORE) {
                            check = this.carbanara(mc.player.getPosition(), (int)this.radius.getValue().floatValue());
                            if (check != null) {
                                RenderUtil.Render3D.drawBlockBox(pos, ColorUtil.rgba(0, 0, 0, 255));
                            }
                        }

                        if (this.iron.get() && state.getBlock() == Blocks.IRON_ORE) {
                            check = this.carbanara(mc.player.getPosition(), (int)this.radius.getValue().floatValue());
                            if (check != null) {
                                RenderUtil.Render3D.drawBlockBox(pos, ColorUtil.rgba(105, 105, 105, 255));
                            }
                        }

                        if (this.redstone.get() && state.getBlock() == Blocks.REDSTONE_ORE) {
                            check = this.carbanara(mc.player.getPosition(), (int)this.radius.getValue().floatValue());
                            if (check != null) {
                                RenderUtil.Render3D.drawBlockBox(pos, ColorUtil.rgba(255, 0, 0, 255));
                            }
                        }

                        if (this.golda.get() && state.getBlock() == Blocks.GOLD_ORE) {
                            check = this.carbanara(mc.player.getPosition(), (int)this.radius.getValue().floatValue());
                            if (check != null) {
                                RenderUtil.Render3D.drawBlockBox(pos, ColorUtil.rgba(255, 255, 0, 255));
                            }
                        }

                        if (this.emerald.get() && state.getBlock() == Blocks.EMERALD_ORE) {
                            check = this.carbanara(mc.player.getPosition(), (int)this.radius.getValue().floatValue());
                            if (check != null) {
                                RenderUtil.Render3D.drawBlockBox(pos, ColorUtil.rgba(0, 128, 0, 255));
                            }
                        }

                        if (this.diamond.get() && state.getBlock() == Blocks.DIAMOND_ORE) {
                            check = this.carbanara(mc.player.getPosition(), (int)this.radius.getValue().floatValue());
                            if (check != null) {
                                RenderUtil.Render3D.drawBlockBox(pos, ColorUtil.rgba(0, 255, 255, 255));
                            }
                        }

                        if (this.lapise.get() && state.getBlock() == Blocks.LAPIS_ORE) {
                            check = this.carbanara(mc.player.getPosition(), (int)this.radius.getValue().floatValue());
                            if (check != null) {
                                RenderUtil.Render3D.drawBlockBox(pos, ColorUtil.rgba(0, 0, 255, 255));
                            }
                        }
                        if (this.boneblock.get() && state.getBlock() == Blocks.BONE_BLOCK) {
                            check = this.carbanara(mc.player.getPosition(), (int)this.radius.getValue().floatValue());
                            if (check != null) {
                                RenderUtil.Render3D.drawBlockBox(pos, ColorUtil.rgba(255, 255, 255, 255));
                            }
                        }
                    }
                }
            }
        }

    }

    private BlockPos carbanara(BlockPos position, int radius) {
        for(int x = (int)(mc.player.getPosX() - (double)radius); (double)x <= mc.player.getPosX() + (double)radius; ++x) {
            for(int y = (int)(mc.player.getPosY() - (double)radius); (double)y <= mc.player.getPosY() + (double)radius; ++y) {
                for(int z = (int)(mc.player.getPosZ() - (double)radius); (double)z <= mc.player.getPosZ() + (double)radius; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (this.ancient_debris.get() && state.getBlock() == Blocks.ANCIENT_DEBRIS) {
                        return pos;
                    }

                    if (this.ygol.get() && state.getBlock() == Blocks.COAL_ORE) {
                        return pos;
                    }

                    if (this.iron.get() && state.getBlock() == Blocks.IRON_ORE) {
                        return pos;
                    }

                    if (this.redstone.get() && state.getBlock() == Blocks.REDSTONE_ORE) {
                        return pos;
                    }

                    if (this.golda.get() && state.getBlock() == Blocks.GOLD_ORE) {
                        return pos;
                    }

                    if (this.emerald.get() && state.getBlock() == Blocks.EMERALD_ORE) {
                        return pos;
                    }

                    if (this.diamond.get() && state.getBlock() == Blocks.DIAMOND_ORE) {
                        return pos;
                    }

                    if (this.lapise.get() && state.getBlock() == Blocks.LAPIS_ORE) {
                        return pos;
                    }
                    if (this.boneblock.get() && state.getBlock() == Blocks.BONE_BLOCK) {
                        return pos;
                    }
                }
            }
        }

        return position;
    }

    protected void onDisable() {
        super.onDisable();
    }

    protected void onEnable() {
        super.onEnable();
    }
}
