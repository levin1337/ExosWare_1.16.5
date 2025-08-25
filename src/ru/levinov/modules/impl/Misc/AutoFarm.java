package ru.levinov.modules.impl.Misc;

import net.minecraft.block.*;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.system.CallbackI;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventMotion;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.movement.MoveUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.world.InventoryUtil;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


@FunctionAnnotation(
        name = "AutoFarm",
        type = Type.Misc,
        desc = "Авто. ломание на ферме"
)
public class AutoFarm extends Function {
    private boolean movingForward = true;
    private final StopWatch stopWatchMain = new StopWatch();
    private final StopWatch stopWatch = new StopWatch();
    private boolean autoRepair, expValid;
    private long lastToggleTime = System.currentTimeMillis();
    private final BooleanOption autorun = (new BooleanOption("Авто-Ход", true));
    private final BooleanOption autosell = (new BooleanOption("Авто-Сдача", false));

    private final ModeSetting mode = new ModeSetting("Мод фарма", "Обычный","Обычный","Шар огородника", "Садить","Ягоды FunTime","Морковь FunTime");

    private final ModeSetting moderun = new ModeSetting("Мод ходьбы", "Таймер","Таймер","Координаты").setVisible(autorun::get);


    public final MultiBoxSetting elements = new MultiBoxSetting("Что ломать?",
            new BooleanOption("Семена", true),
            new BooleanOption("Арбузные блоки", true),
            new BooleanOption("Тыквенные блоки", true)
    );

    private final SliderSetting radius = new SliderSetting("Радиус", 2.0F, 1.0F, 6.0F, 1.0F);
    private final BooleanOption bloom = (new BooleanOption("Подсветка текущего блока", true));

    private final SliderSetting timerun = new SliderSetting("Время ходьбы", 3000F, 1000F, 10000F, 500F).setVisible(autorun::get);
    private final SliderSetting timesell = new SliderSetting("Время сдачи", 25000F, 3000F, 60000F, 1000F).setVisible(autosell::get);


    private final TimerUtil timerHelper = new TimerUtil();
    public AutoFarm() {
        addSettings(mode,moderun, elements,radius, bloom,autorun,autosell,timerun,timesell);
    }


    //Ломает только за этим радиусом за него выходить незя
    public void onEvent(Event event) {
        if (mode.is("Обычный")) {
            if (event instanceof EventMotion e) {
                for (int x = (int) (mc.player.getPosX() - (double) radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                    for (int y = (int) (mc.player.getPosY() - (double) radius.getValue().floatValue()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
                        for (int z = (int) (mc.player.getPosZ() - (double) radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = mc.world.getBlockState(pos);
                            if (state.getBlock() instanceof CropsBlock && elements.get(0) || state.getBlock() instanceof MelonBlock && elements.get(1) || state.getBlock() instanceof PumpkinBlock && elements.get(2)) {
                                BlockPos wheatPos = findNearestWheat(mc.player.getPosition(), (int) radius.getValue().floatValue());
                                if (wheatPos != null) {
                                    float[] rotation = rots(new Vector3d((double) wheatPos.getX() + 0.5, (double) wheatPos.getY() + 0.5, (double) wheatPos.getZ() + 0.5));

                                    mc.player.rotationYawHead = rotation[0];
                                    mc.player.renderYawOffset = rotation[1];
                                    mc.player.rotationPitchHead = rotation[1];

                                    if (timerHelper.hasTimeElapsed((long) mc.player.getDigSpeed(mc.world.getBlockState(new BlockPos((double) ((int) wheatPos.x), (double) ((int) wheatPos.y), (double) ((int) wheatPos.z))).getBlock().getDefaultState()))) {
                                        mc.player.swingArm(Hand.MAIN_HAND);
                                        mc.playerController.onPlayerDamageBlock(new BlockPos(wheatPos.x, wheatPos.y, wheatPos.z), mc.player.getHorizontalFacing());
                                        timerHelper.reset();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (mode.is("Шар огородника")) {
            if (event instanceof EventUpdate e) {
                for (int x = (int) (mc.player.getPosX() - (double) radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                    for (int y = (int) (mc.player.getPosY() - (double) radius.getValue().floatValue()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
                        for (int z = (int) (mc.player.getPosZ() - (double) radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = mc.world.getBlockState(pos);
                            if (state.getBlock() instanceof CropsBlock && state.get(CropsBlock.AGE) > 7 && elements.get(0) || state.getBlock() instanceof MelonBlock && elements.get(1) || state.getBlock() instanceof PumpkinBlock && elements.get(2) || state.getBlock() instanceof BeetrootBlock && state.get(BeetrootBlock.BEETROOT_AGE) == 3) {
                                BlockPos wheatPos = findSphere(mc.player.getPosition(), (int) radius.getValue().floatValue());
                                if (wheatPos != null) {
                                    float[] rotation = rots(new Vector3d((double) wheatPos.getX() + 0.5, (double) wheatPos.getY() + 0.5, (double) wheatPos.getZ() + 0.5));
                                    mc.player.rotationYawHead = rotation[0];
                                    mc.player.renderYawOffset = rotation[1];
                                    mc.player.rotationPitchHead = rotation[1];
                                    if (timerHelper.hasTimeElapsed((long) mc.player.getDigSpeed(mc.world.getBlockState(new BlockPos((double) ((int) wheatPos.x), (double) ((int) wheatPos.y), (double) ((int) wheatPos.z))).getBlock().getDefaultState()))) {
                                        mc.player.setSprinting(false);
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
        if (mode.is("Ягоды FunTime")) {
            if (event instanceof EventUpdate e) {
                for (int y = (int) (mc.player.getPosY()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
                    for (int x = (int) (mc.player.getPosX() - (double) this.radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                        for (int z = (int) (mc.player.getPosZ() - (double) this.radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = mc.world.getBlockState(pos);
                            if (state.getBlock() instanceof SweetBerryBushBlock && state.get(SweetBerryBushBlock.AGE) == 3) {
                                BlockPos wheatPos = uagodaFt(mc.player.getPosition(), (int) radius.getValue().floatValue());
                                if (wheatPos != null) {
                                    float[] rotation = rots(new Vector3d((double) wheatPos.getX() + 0.5, (double) wheatPos.getY() + 0.5, (double) wheatPos.getZ() + 0.5));
                                    mc.player.rotationYaw = rotation[0];
                                    mc.player.renderYawOffset = rotation[1];
                                    mc.player.rotationPitch = rotation[1];
                                    BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(
                                            new Vector3d(wheatPos.getX() + 0.5, wheatPos.getY() + 0.5f, wheatPos.getZ() + 0.5),
                                            Direction.UP,
                                            wheatPos,
                                            false
                                    );
                                    if (timerHelper.hasTimeElapsed(80)) {
                                        if (mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, rayTraceResult) == ActionResultType.SUCCESS) {
                                            mc.player.swingArm(Hand.MAIN_HAND);
                                        }
                                        timerHelper.reset();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (mode.is("Морковь FunTime")) {
            if (event instanceof EventUpdate e) {
                List<Item> hoeItems = List.of(Items.NETHERITE_HOE, Items.DIAMOND_HOE);
                List<Item> plantsItems = List.of(Items.CARROT, Items.POTATO);
                Slot expSlot = InventoryUtil.getInventorySlot(Items.EXPERIENCE_BOTTLE);
                Slot plantSlot = InventoryUtil.getInventorySlot(plantsItems);
                Slot hoeSlot = InventoryUtil.getInventorySlot(hoeItems);
                int expCount = InventoryUtil.getInventoryCount(Items.EXPERIENCE_BOTTLE);
                Item mainHandItem = mc.player.getHeldItemMainhand().getItem();
                Item offHandItem = mc.player.getHeldItemOffhand().getItem();
                if (hoeSlot == null || MoveUtil.isMoving() || !timerHelper.hasTimeElapsed(500)) return;
                float itemStrength = 1 - MathHelper.clamp((float) hoeSlot.getStack().getDamage() / (float) hoeSlot.getStack().getMaxDamage(), 0, 1);
                if (itemStrength < 0.05) {
                    autoRepair = true;
                } else if (itemStrength == 1 && autoRepair) {
                    timerHelper.reset();
                    autoRepair = false;
                    expValid = false;
                    return;
                }
                expValid = expCount >= 320 || expCount != 0 && expValid;

                if (mc.player.getFoodStats().needFood()) {
                    Slot slot = InventoryUtil.getSlotFoodMaxSaturation();
                    if (!offHandItem.equals(slot.getStack().getItem())) {
                        if (mc.currentScreen instanceof ContainerScreen<?>) {
                            mc.player.closeScreen();
                            return;
                        }
                        InventoryUtil.clickSlot(slot, 40, ClickType.SWAP, false);
                    }
                    mc.playerController.processRightClick(mc.player, mc.world, Hand.OFF_HAND);
                } else if (mc.player.inventory.getFirstEmptyStack() == -1) {
                    if (!plantsItems.contains(offHandItem)) {
                        InventoryUtil.clickSlot(plantSlot, 40, ClickType.SWAP, false);
                        return;
                    }
                    if (mc.currentScreen instanceof ContainerScreen<?> screen) {
                        if (screen.getTitle().getString().equals("? Выберите секцию")) {
                            InventoryUtil.clickSlotId(21, 0, ClickType.PICKUP, true);
                            return;
                        }
                        if (screen.getTitle().getString().equals("Скупщик еды")) {
                            InventoryUtil.clickSlotId(offHandItem.equals(Items.CARROT) ? 10 : 11, 0, ClickType.PICKUP, true);
                            return;
                        }
                    }
                    if (timerHelper.hasTimeElapsed(1000)) {
                        mc.player.sendChatMessage("/buyer");
                        timerHelper.reset();
                    }
                } else if (autoRepair) {
                    if (expValid) {
                        if (mc.currentScreen instanceof ContainerScreen<?>) {
                            mc.player.closeScreen();
                            timerHelper.reset();
                            return;
                        }
                        if (!offHandItem.equals(Items.EXPERIENCE_BOTTLE)) {
                            InventoryUtil.clickSlot(expSlot, 40, ClickType.SWAP, false);
                        }
                        if (!hoeItems.contains(mainHandItem)) {
                            InventoryUtil.clickSlot(hoeSlot, mc.player.inventory.currentItem, ClickType.SWAP, false);
                        }
                        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                    } else if (timerHelper.hasTimeElapsed(800)) {
                        if (mc.currentScreen instanceof ContainerScreen<?> screen) {
                            if (screen.getTitle().getString().contains("Пузырек опыта")) {
                                mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getTag() != null && s.slotNumber < 45)
                                        .min(Comparator.comparingInt(s -> InventoryUtil.getPrice(s.getStack()) / s.getStack().getCount()))
                                        .ifPresent(s -> InventoryUtil.clickSlot(s, 0, ClickType.QUICK_MOVE, true));
                                timerHelper.reset();
                                return;
                            } else if (screen.getTitle().getString().contains("Подозрительная цена")) {
                                InventoryUtil.clickSlotId(0, 0, ClickType.QUICK_MOVE, true);
                                timerHelper.reset();
                                return;
                            }
                        }
                        mc.player.sendChatMessage("/ah search Пузырёк Опыта");
                        timerHelper.reset();
                    }
                } else {
                    BlockPos pos = mc.player.getPosition();
                    if (mc.world.getBlockState(pos).getBlock().equals(Blocks.FARMLAND)) {
                        if (hoeItems.contains(mainHandItem) && plantsItems.contains(offHandItem)) {
                            mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.OFF_HAND, new BlockRayTraceResult(mc.player.getPositionVec(), Direction.UP, pos, false)));
                            IntStream.range(0, 3).forEach(i -> mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(mc.player.getPositionVec(), Direction.UP, pos.up(), false))));
                            mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos.up(), Direction.UP));
                        } else {
                            if (mc.currentScreen instanceof ContainerScreen<?>) {
                                mc.player.closeScreen();
                                timerHelper.reset();
                                return;
                            }
                            if (!plantsItems.contains(offHandItem)) {
                                InventoryUtil.clickSlot(plantSlot, 40, ClickType.SWAP, false);
                            }
                            if (!hoeItems.contains(mainHandItem)) {
                                InventoryUtil.clickSlot(hoeSlot, mc.player.inventory.currentItem, ClickType.SWAP, false);
                            }
                        }
                    }
                }
            }
        }
        //Свечение для каждого мода
        if (event instanceof EventRender && bloom.get()) {
            if (mode.is("Обычный")) {
                for (int x = (int) (mc.player.getPosX() - (double) radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                    for (int y = (int) (mc.player.getPosY() - (double) radius.getValue().floatValue()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
                        for (int z = (int) (mc.player.getPosZ() - (double) radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = mc.world.getBlockState(pos);
                            if (state.getBlock() instanceof CropsBlock && elements.get(0) || state.getBlock() instanceof MelonBlock && elements.get(1) || state.getBlock() instanceof PumpkinBlock && elements.get(2)) {
                                BlockPos wheatPos = findNearestWheat(mc.player.getPosition(), (int) radius.getValue().floatValue());
                                if (state.getBlock() != Blocks.AIR) {
                                    RenderUtil.Render3D.drawBlockBox(wheatPos, ColorUtil.rgba(128, 255, 128, 255));
                                }
                            }
                        }
                    }
                }
            }
            if (mode.is("Шар огородника")) {
                for (int x = (int) (mc.player.getPosX() - (double) radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                    for (int y = (int) (mc.player.getPosY() - (double) radius.getValue().floatValue()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
                        for (int z = (int) (mc.player.getPosZ() - (double) radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = mc.world.getBlockState(pos);
                            if (state.getBlock() instanceof CropsBlock && state.get(CropsBlock.AGE) == 7 && elements.get(0) || state.getBlock() instanceof MelonBlock && elements.get(1) || state.getBlock() instanceof PumpkinBlock && elements.get(2) || state.getBlock() instanceof BeetrootBlock && state.get(BeetrootBlock.BEETROOT_AGE) == 3) {
                                BlockPos wheatPos = findSphere(mc.player.getPosition(), (int) radius.getValue().floatValue());
                                if (state.getBlock() != Blocks.AIR) {
                                    RenderUtil.Render3D.drawBlockBox(wheatPos, ColorUtil.rgba(128, 255, 128, 255));
                                }
                            }
                        }
                    }
                }
            }
            if (mode.is("Ягоды FunTime")) {
                for (int y = (int) (mc.player.getPosY()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
                    for (int x = (int) (mc.player.getPosX() - (double) this.radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                        for (int z = (int) (mc.player.getPosZ() - (double) this.radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = mc.world.getBlockState(pos);
                            if (state.getBlock() instanceof SweetBerryBushBlock && state.get(SweetBerryBushBlock.AGE) == 3) {
                                BlockPos wheatPos = uagodaFt(mc.player.getPosition(), (int) radius.getValue().floatValue());
                                if (state.getBlock() != Blocks.AIR) {
                                    RenderUtil.Render3D.drawBlockBox(wheatPos, ColorUtil.rgba(128, 255, 128, 255));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (event instanceof EventUpdate e) {
            if (moderun.is("Таймер")) {
                if (autorun.get()) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastToggleTime >= timerun.getValue().floatValue()) {
                        movingForward = !movingForward; // Переключаем направление
                        lastToggleTime = currentTime; // Обновляем время последнего переключения
                    }
                    if (movingForward) {
                        mc.gameSettings.keyBindForward.setPressed(true); // Движение вперед
                        mc.gameSettings.keyBindBack.setPressed(false);   // Отключаем движение назад
                    } else {
                        mc.gameSettings.keyBindForward.setPressed(false); // Отключаем движение вперед
                        mc.gameSettings.keyBindBack.setPressed(true);     // Движение назад
                    }
                } else {
                }
            }
        }
        if (event instanceof EventUpdate e) {
            if (autosell.get()) {
                if (timerHelper.hasTimeElapsed((long) timesell.getValue().floatValue())) {
                    mc.player.sendChatMessage("/sellfarm");
                    timerHelper.reset();
                }
            }
        }
    }

    private BlockPos uagodaFt(BlockPos position, int radius) {
        for (int y = (int) (mc.player.getPosY()); (double) y <= mc.player.getPosY() + (double) this.radius.getValue().floatValue(); ++y) {
            for (int x = (int) (mc.player.getPosX() - (double) this.radius.getValue().floatValue()); (double) x <= mc.player.getPosX() + (double) this.radius.getValue().floatValue(); ++x) {
                for (int z = (int) (mc.player.getPosZ() - (double) this.radius.getValue().floatValue()); (double) z <= mc.player.getPosZ() + (double) this.radius.getValue().floatValue(); ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock() instanceof SweetBerryBushBlock && state.get(SweetBerryBushBlock.AGE) == 3) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private BlockPos findNearestWheat(BlockPos position, int radius) {
        for(int x = (int)(mc.player.getPosX() - (double)radius); (double)x <= mc.player.getPosX() + (double)radius; ++x) {
            for(int y = (int)(mc.player.getPosY() - (double)radius); (double)y <= mc.player.getPosY() + (double)radius; ++y) {
                for(int z = (int)(mc.player.getPosZ() - (double)radius); (double)z <= mc.player.getPosZ() + (double)radius; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock() instanceof CropsBlock && elements.get(0) || state.getBlock() instanceof MelonBlock && elements.get(1) || state.getBlock() instanceof PumpkinBlock && elements.get(2)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }
    private BlockPos findSphere(BlockPos position, int radius) {
        for(int x = (int)(mc.player.getPosX() - (double)radius); (double)x <= mc.player.getPosX() + (double)radius; ++x) {
            for(int y = (int)(mc.player.getPosY() - (double)radius); (double)y <= mc.player.getPosY() + (double)radius; ++y) {
                for(int z = (int)(mc.player.getPosZ() - (double)radius); (double)z <= mc.player.getPosZ() + (double)radius; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock() instanceof CropsBlock && state.get(CropsBlock.AGE) == 7 && elements.get(0) || state.getBlock() instanceof MelonBlock && elements.get(1) || state.getBlock() instanceof PumpkinBlock && elements.get(2) || state.getBlock() instanceof BeetrootBlock && state.get(BeetrootBlock.BEETROOT_AGE) == 3) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }


    //Ротация
    public static float[] rots(Vector3d vec) {
        double x = vec.x - mc.player.getPosX();
        double y = vec.y - (mc.player.getPosY() + 2);
        double z = vec.z - mc.player.getPosZ();
        double u = (double) MathHelper.sqrt(x * x + z * z);
        float u2 = (float)(MathHelper.atan2(z, x) * 57.29577951308232 - 90.0);
        float u3 = (float)(-MathHelper.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};
    }
    @Override
    public void onDisable() {
        super.onDisable();
        autoRepair = false;
        expValid = false;
    }
    @Override
    public void onEnable() {
        super.onEnable();
    }
}

