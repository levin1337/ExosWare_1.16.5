package ru.levinov.modules.impl.combat;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventInput;
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

import java.util.ArrayList;
import java.util.List;

@FunctionAnnotation(
        name = "CrystalAura",
        type = Type.Combat,
        desc = "������ ������ ��������� � �������"
)
public class CrystalAura extends Function {

    public final MultiBoxSetting options = new MultiBoxSetting("���������",
            new BooleanOption("�� �������� ����", true),
            new BooleanOption("��������� ��������", false),
            new BooleanOption("������� ���������", true),
            new BooleanOption("�������", true),
            new BooleanOption("������ � � �������� ������", true),
            new BooleanOption("��������� �����", true)
    );

    private final ModeSetting distanceMode = new ModeSetting("��� �������", "�������", "�������", "Custom");
    private final SliderSetting customDistance = new SliderSetting("������", 5, 2.5f, 6, 0.05f).setVisible(() -> distanceMode.is("Custom"));
    private final SliderSetting customUp = new SliderSetting("�����", 2, 1, 6, 0.05f);
    private final SliderSetting customDown = new SliderSetting("����", 2, 1, 6, 0.05f);
    private final SliderSetting breakDelay = new SliderSetting("�������� (��)", 100, 0, 500, 1);
    private final BooleanOption slotcheck = new BooleanOption("����� �����", true);

    public CrystalAura() {
        addSettings(distanceMode,options, customDistance, customUp, customDown, breakDelay,slotcheck);
    }

    private Entity crystalTarget = null;
    public Vector2f rotate = new Vector2f(0, 0);
    private Vector3d obsidianVec = new Vector3d(0,0,0);
    private BlockPos closestObsidian = null;
    private Entity closestCrystal;
    private List<BlockPos> obsidianPositions = new ArrayList<>();

    private boolean crystalAttack = false;
    private final TimerUtil timerHelper = new TimerUtil();
    double distance() {
        return distanceMode.is("�������") ? mc.playerController.getBlockReachDistance() : customDistance.getValue().floatValue();
    }

    public boolean check() {
        return (crystalTarget != null || closestObsidian != null) && rotate != null && options.get("��������� ��������") && (options.get("�������"));
    }

    @Override
    public void onDisable() {
        reset();

        super.onDisable();
    }
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {
            if (options.get("��������� �����")) {
                if (obsidianVec != null) {
                    RenderUtil.Render3D.drawBlockBox(new BlockPos(obsidianVec.getX(), obsidianVec.getY(), obsidianVec.getZ()), ColorUtil.getColorStyle(360));
                }
            }
        }
        if (event instanceof EventInput e) {
            if (check()) {
                MoveUtil.fixMovement(e, rotate.x);
            }
        }
        if (event instanceof EventUpdate e) {
            findAndAttackCrystal();
            findAndClickObsidian();
        }
    }

    private void findAndAttackCrystal() {
        closestCrystal = null;
        double closestDistanceToTarget = Double.MAX_VALUE;
        double maxDistanceToCrystal = distance();

        if (!options.get("������� ���������")) {
            crystalAttack = true;
        }

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof EnderCrystalEntity enderCrystal) {
                double distanceToCrystal = mc.player.getDistance(enderCrystal);
                if (distanceToCrystal > maxDistanceToCrystal) {
                    continue;
                }

                if (mc.player.getPosY() >= enderCrystal.getPosY() && options.get("�� �������� ����")) {
                    continue;
                }

                if (distanceToCrystal < closestDistanceToTarget) {
                    closestDistanceToTarget = distanceToCrystal;
                    closestCrystal = enderCrystal;
                }
            }
        }

        if (closestCrystal != null && crystalAttack) {
            crystalTarget = closestCrystal;
                mc.playerController.attackEntity(mc.player, closestCrystal);
                mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
                crystalTarget = null;
        } else {
            reset();
        }
    }


    private void findAndClickObsidian() {
    //    int previousSlot = mc.player.inventory.currentItem;
        int previousSlot = InventoryUtil.getItem(Items.DIAMOND_SWORD,true);
        if (previousSlot == -1) {
            previousSlot = InventoryUtil.getItem(Items.NETHERITE_SWORD,true);
        }

        int crystal = InventoryUtil.getItem(Items.END_CRYSTAL,true);
        if (crystal == -1 || !options.get("������� ���������")) return;

        double closestDistanceToTarget = Double.MAX_VALUE;
        double maxDistanceToObsidian = distance() * 2;
        closestObsidian = null;
        obsidianPositions.clear();
        crystalAttack = false;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity == mc.player || entity instanceof EnderCrystalEntity || entity instanceof ArrowEntity || entity instanceof ProjectileItemEntity || entity instanceof ItemEntity || entity instanceof ThrowableEntity || entity instanceof FallingBlockEntity) {
                continue;
            }

            for (int x = (int) -distance(); x <= distance(); x++) {
                for (int z = (int) -distance(); z <= distance(); z++) {
                    for (int y = (int) -customDown.getValue().floatValue(); y <= customUp.getValue().floatValue(); y++) {
                        BlockPos pos = new BlockPos(entity.getPosX() + x, entity.getPosY() - 0.5f + y, entity.getPosZ() + z);
                        if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
                            Block blockAbove = mc.world.getBlockState(pos.up()).getBlock();
                            if (!(blockAbove instanceof AirBlock)) {
                                continue;
                            }

                            if (pos.getY() < mc.player.getPosY() && options.get("�� �������� ����") && !mc.player.isCreative() || !entity.isAlive()) {
                                continue;
                            }

                            if (entity.getPosition().equals(pos.up()) || mc.player.getPosition().equals(pos.up())) {
                                continue;
                            }
                            if (options.get("������ � � �������� ������")) {
                                if (entity.getPosY() - 0.5f < pos.getY()) {
                                    continue;
                                }
                            }

                            double distanceToPlayer = mc.player.getDistanceSq(Vector3d.copyCentered(pos));
                            if (distanceToPlayer > maxDistanceToObsidian) {
                                continue;
                            }

                            double distanceToTarget = entity.getDistanceSq(Vector3d.copyCentered(pos));
                            if (distanceToTarget < closestDistanceToTarget) {
                                closestDistanceToTarget = distanceToTarget;
                                closestObsidian = pos;
                                obsidianPositions.clear();
                                obsidianPositions.add(closestObsidian);
                            }
                        }
                    }
                }
            }
        }

        if (!obsidianPositions.isEmpty()) {
            if (slotcheck.get()) {
                mc.player.inventory.currentItem = crystal;
            } else {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(crystal));
            }


            obsidianVec = new Vector3d(closestObsidian.getX() + 0.5, closestObsidian.getY() + 0.5, closestObsidian.getZ() + 0.5);
            BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(obsidianVec, Direction.UP, closestObsidian, false);
            if (options.get("�������")) {
                float[] rotation = rots(new Vector3d(closestObsidian.getX() + 0.5, closestObsidian.getY() + 0.5, closestObsidian.getZ() + 0.5));
                //�����
                mc.player.rotationYawHead = rotation[0];
                mc.player.rotationPitchHead = rotation[1];
            }
            if (timerHelper.hasTimeElapsed((long) breakDelay.getValue().floatValue())) {
                mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, rayTraceResult);
                mc.player.swingArm(Hand.MAIN_HAND);
                mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
                crystalAttack = true;
                if (slotcheck.get()) {
                    mc.player.inventory.currentItem = previousSlot;
                } else {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
                }

                // ���������� ������
                timerHelper.reset();
            }

        }
    }
    public void reset() {
        closestObsidian = null;
        closestCrystal = null;
        crystalTarget = null;
        obsidianVec = null;
        obsidianPositions.clear();
        crystalAttack = false;
    }
    public static float[] rots(Vector3d vec) {
        double x = vec.x - mc.player.getPosX();
        double y = vec.y - (mc.player.getPosY() + 2);
        double z = vec.z - mc.player.getPosZ();
        double u = (double) MathHelper.sqrt(x * x + z * z);
        float u2 = (float)(MathHelper.atan2(z, x) * 57.29577951308232 - 90.0);
        float u3 = (float)(-MathHelper.atan2(y, u) * 57.29577951308232);
        return new float[]{u2, u3};
    }
}