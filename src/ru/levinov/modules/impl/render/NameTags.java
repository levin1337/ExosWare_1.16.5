package ru.levinov.modules.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import org.joml.Vector4d;
import org.joml.Vector4i;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.impl.combat.Aura;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.modules.settings.imp.SliderSetting;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.math.MathUtil;
import ru.levinov.util.math.PlayerPositionTracker;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.levinov.util.math.PlayerPositionTracker.isInView;
import static ru.levinov.util.render.ColorUtil.*;
import static ru.levinov.util.render.RenderUtil.Render2D.*;

@FunctionAnnotation(name = "NameTags", type = Type.Render,
keywords = {"esp","espbox","espbox"})
public class NameTags extends Function {


    public MultiBoxSetting elements = new MultiBoxSetting("�������� ���",
            new BooleanOption("�����", false),
            new BooleanOption("��������", false),
            new BooleanOption("����� ��������", false),
            new BooleanOption("�����", true),
            new BooleanOption("�������", false),
            new BooleanOption("�����", true));


    private final BooleanOption indexItem = new BooleanOption("���������� �����/����������", false);
    public SliderSetting size = new SliderSetting("������", 1, 1, 3, 0.5f);

    public NameTags() {
        addSettings(indexItem,elements, size);
    }

    public HashMap<Vector4d, PlayerEntity> positions = new HashMap<>();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender render) {
            if (render.isRender3D()) {
                updatePlayerPositions(render.partialTicks);
            }

            if (render.isRender2D()) {
                renderPlayerElements(render.matrixStack);
            }
        }
    }

    // ��������� ������� ������� ��� ����������� �� 3D-�����
    private void updatePlayerPositions(float partialTicks) {
        positions.clear();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (isInView(player) && player.botEntity) {
                if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON && player == mc.player) {
                    continue;
                }
                Vector4d position = PlayerPositionTracker.updatePlayerPositions(player, partialTicks);
                if (position != null) {
                    positions.put(position, player);
                }
            }
        }
    }

    // ���������� �������� ������ �� 2D-�����
    private void renderPlayerElements(MatrixStack stack) {

        Vector4i colors = new Vector4i(ColorUtil.getColorStyle(0),
                ColorUtil.getColorStyle(90),
                ColorUtil.getColorStyle(180),
                ColorUtil.getColorStyle(270));

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        for (Map.Entry<Vector4d, PlayerEntity> entry : positions.entrySet()) {
            Vector4d position = entry.getKey();
            PlayerEntity player = entry.getValue();
            if (elements.get(0)) {
                renderBox(position.x, position.y, position.z, position.w, rgba(0, 0, 0, 128), colors);
            }
            if (elements.get(1)) {
                float height = (float) (position.w - position.y);
                player.animationPerc = AnimationMath.fast(player.animationPerc, MathHelper.clamp((player.getHealth() / player.getMaxHealth()), 0, 1), 15);

              //  drawRectBuilding(position.x - 2 - size.getValue().floatValue() - 0.5f, position.y - 0.5f, size.getValue().floatValue() + 1, height + 1, rgba(0, 0, 0, 128));
                drawVerticalBuilding(position.x - 3 - size.getValue().floatValue(), position.y + (height * (1 - player.animationPerc)), size.getValue().floatValue() + 1, height - height * (1 - player.animationPerc), Color.red.getRGB(), Color.red.getRGB());
            }

        }
        tessellator.draw();


        BloomHelper.registerRenderCall(() -> {
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            for (Map.Entry<Vector4d, PlayerEntity> entry : positions.entrySet()) {
                Vector4d position = entry.getKey();
                PlayerEntity player = entry.getValue();
                if (elements.get(0)) {
                    renderBox(position.x, position.y, position.z, position.w, rgba(0, 0, 0, 0), colors);
                }
                if (elements.get(1)) {
                    float height = (float) (position.w - position.y);
                    player.animationPerc = AnimationMath.fast(player.animationPerc, MathHelper.clamp((player.getHealth() / player.getMaxHealth()), 0, 1), 15);
                    drawVerticalBuilding(position.x - 2 - size.getValue().floatValue(), position.y + (height * (1 - player.animationPerc)), size.getValue().floatValue(), height - height * (1 - player.animationPerc), colors.z, colors.x);
                }

            }
            tessellator.draw();
        });

        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();

        for (Map.Entry<Vector4d, PlayerEntity> entry : positions.entrySet()) {
            Vector4d position = entry.getKey();
            PlayerEntity player = entry.getValue();
            double x = position.x;
            double y = position.y;
            double endX = position.z;
            double endY = position.w;
            float height = (float) (position.w - position.y);
            String healthText =  "HP:" + (int) player.getHealth();
            float width = (float) (position.z - position.x);

            ITextComponent name = player.getDisplayName();

            if (elements.get(2)) {
                BloomHelper.registerRenderCall(() -> {
                    Fonts.durman[10].drawStringWithOutline(stack, healthText, position.x - 3 - size.getValue().floatValue() - Fonts.verdana[10].getWidth(healthText), position.y + (height * (1 - player.animationPerc)) + 1, colors.x);
                });
                Fonts.durman[10].drawStringWithOutline(stack, healthText, position.x - 3 - size.getValue().floatValue() - Fonts.verdana[10].getWidth(healthText), position.y + (height * (1 - player.animationPerc)) + 1, colors.x);
            }
            if (elements.get(3)) {
                renderTags(stack, (float) x, (float) y, (float) endX, (float) endY, player);
            }
            if (elements.get(4)) {
                renderEffects(player, (float) y, (float) endX, stack);
            }
        }


    }

    private void renderEffects(PlayerEntity player,
                               float y,
                               float endX,
                               MatrixStack matrices) {
        EffectInstance[] effects = player.getActivePotionEffects().toArray(new EffectInstance[0]);
        int effectCount = effects.length;

        for (int i = 0; i < effectCount; i++) {
            EffectInstance p = effects[i];

            if (p == null) {
                continue;
            }

            String effectName = I18n.format(p.getEffectName());
            String effectAmplifier = I18n.format("enchantment.level." + (p.getAmplifier() + 1));
            String effectDuration = EffectUtils.getPotionDurationString(p, 1);
            String effectString = effectName + " " + effectAmplifier + TextFormatting.GRAY + "(" + effectDuration + ")" + TextFormatting.RESET;

            Fonts.verdana[12].drawStringWithShadow(matrices, effectString, endX + 2.5f, y - 2 + ((i + 1) * 8), -1);
        }
    }

    private void renderBox(double x,
                           double y,
                           double endX,
                           double endY,
                           int back,
                           Vector4i colors) {
        float size = MathHelper.clamp(this.size.getValue().floatValue() + 1, 2, 5);
     //   drawRectOutlineBuilding(x - 0.5f, y - 0.5f, endX + 0.5f, endY + 0.5f, size, back);
        drawRectOutlineBuildingGradient(x, y, endX, endY, size - 1, colors);
    }

    public static void drawItemStack(ItemStack stack,
                                     double x,
                                     double y,
                                     String altText,
                                     boolean withoutOverlay) {

        RenderSystem.translated(x, y, 0);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (!withoutOverlay)
            mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer, stack, 0, 0, altText);
        RenderSystem.translated(-x, -y, 0);
    }

    private void renderTags(MatrixStack matrixStack,
                            float posX,
                            float posY,
                            float endPosX,
                            float endPosY,
                            PlayerEntity entity) {
        float maxOffsetY = 0;

        ITextComponent text = entity.getDisplayName();
        TextComponent name = (TextComponent) text;

        String friendPrefix = Managment.FRIEND_MANAGER.isFriend(entity.getName().getString())
                ? TextFormatting.GREEN + "[F] "
                : "";
        ITextComponent friendText = ITextComponent.getTextComponentOrEmpty(friendPrefix);

        TextComponent friendPrefixComponent = (TextComponent) friendText;
        if (Managment.FRIEND_MANAGER.isFriend(entity.getName().getString()) && (Managment.FUNCTION_MANAGER.nameProtect.state && Managment.FUNCTION_MANAGER.nameProtect.friends.get())) {
            friendPrefixComponent.append(new StringTextComponent(TextFormatting.GREEN + "����"));
        } else {
            friendPrefixComponent.append(name);
        }
        name = friendPrefixComponent;
        if (Aura.target != null && Aura.fixHP.get()) {
            name.append(new StringTextComponent(TextFormatting.DARK_GRAY + " [" + TextFormatting.RED + (int) Aura.target.getHealth() + TextFormatting.DARK_GRAY + "]"));
        } else {
            name.append(new StringTextComponent(TextFormatting.DARK_GRAY + " [" + TextFormatting.RED + (int) entity.getHealth() + TextFormatting.DARK_GRAY + "]"));
        }
        float width = mc.fontRenderer.getStringPropertyWidth(name);
        float height = 16;

        TextComponent finalName = name;
        MathUtil.scaleElements((posX + endPosX) / 2f, posY - height / 2, 0.5f, () -> {
            //RenderUtil.Render2D.drawRoundedRect((posX + endPosX) / 2f - width / 2f - 8, posY - height - 10, width + 15, height + 2, 1, rgba(15, 15, 15, 200));
            RenderUtil.Render2D.drawShadowyestfps((posX + endPosX) / 2f - width / 2f - 8, posY - height - 10, width + 18, height + 4, 8, rgba(15, 15, 15, 175));
            mc.fontRenderer.func_243246_a(matrixStack, finalName, (posX + endPosX) / 2f - width / 2f + 1, posY - height - 4, -1);
        });

        maxOffsetY += 25;
        List<ItemStack> stacks = new ArrayList<>(Arrays.asList(entity.getHeldItemMainhand(), entity.getHeldItemOffhand()));
        entity.getArmorInventoryList().forEach(stacks::add);
        stacks.removeIf(w -> w.getItem() instanceof AirItem);
        int totalSize = stacks.size() * 10;
        maxOffsetY += 19;
        AtomicInteger iterable = new AtomicInteger();

        if (elements.get(5)) {
            float finalMaxOffsetY = maxOffsetY;
            MathUtil.scaleElements((posX + endPosX) / 2, posY - maxOffsetY / 2, 0.7f, () -> {
                renderArmorAndEnchantment(stacks, matrixStack, posX, endPosX, posY, finalMaxOffsetY, totalSize, iterable);
            });
        }
        int totalSize2 = stacks.size() * 10;
        AtomicInteger iterable2 = new AtomicInteger();
        if (this.indexItem.get()) {
            int maxOffsetY2 = 19;
            float finalMaxOffsetY2 = (float)maxOffsetY2;
            MathUtil.scaleElements(posX + endPosX - 150.0F, posY - (float)(maxOffsetY2 / 2) - 70.0F, 0.7F, () -> {
                this.renderNameTalik(stacks, matrixStack, posX, endPosX, posY, finalMaxOffsetY2, totalSize2, iterable2);
            });
        }
    }
    private void renderNameTalik(List<ItemStack> stacks, MatrixStack matrixStack, float posX, float endPosX, float posY, float finalMaxOffsetY, int totalSize, AtomicInteger iterable) {
        Iterator var9 = stacks.iterator();

        while(true) {
            ItemStack stack;
            do {
                if (!var9.hasNext()) {
                    return;
                }

                stack = (ItemStack)var9.next();
            } while(stack.isEmpty());

            talikName(stack, (double)(posX + (endPosX - posX) / 2.0F + (float)(iterable.get() * 20) - (float)totalSize + 2.0F), (double)(posY - finalMaxOffsetY + 10.0F), false);
            iterable.getAndIncrement();
            ArrayList<String> lines = this.getEnchantment(stack);
            float center = posX + (endPosX - posX) / 2.0F + (float)(iterable.get() * 20) - (float)totalSize - 10.0F;
            int i = 0;

            for(Iterator var14 = lines.iterator(); var14.hasNext(); ++i) {
                String s = (String)var14.next();
                Fonts.verdana[12].drawCenteredString(matrixStack, s, (double)center, (double)(posY - finalMaxOffsetY + 5.0F - (float)(i * 7)), -1);
            }
        }
    }
    public static void talikName(ItemStack stack, double x, double y, boolean withoutOverlay) {
        RenderSystem.translated(-x, -y, 0.0);
        Item headItemget = Items.PLAYER_HEAD;
        String head = null;
        Item totemItemget = Items.TOTEM_OF_UNDYING;
        String totem = null;
        if (stack.getItem() == headItemget) {
            head = stack.getDisplayName().getString();
        }

        if (stack.getItem() == totemItemget) {
            totem = stack.getDisplayName().getString();
        }

        RenderSystem.translated(x, y, 0.0);
        if (!withoutOverlay) {
            if (head != null) {
                mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer, stack, -45, -60, head);
            } else if (totem != null) {
                mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer, stack, -45, -60, totem);
            }
        }

        RenderSystem.translated(x, y, 0.0);
    }
    private void renderArmorAndEnchantment(List<ItemStack> stacks,
                                           MatrixStack matrixStack,
                                           float posX,
                                           float endPosX,
                                           float posY,
                                           float finalMaxOffsetY,
                                           int totalSize,
                                           AtomicInteger iterable) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }

            drawItemStack(stack, posX + (endPosX - posX) / 2f + iterable.get() * 20 - totalSize + 2,
                    posY - finalMaxOffsetY + 10, null, false);
            iterable.getAndIncrement();

            ArrayList<String> lines = getEnchantment(stack);
            float center = (posX + (endPosX - posX) / 2f + iterable.get() * 20) - totalSize - 10;
            int i = 0;
            for (String s : lines) {
                Fonts.durman[12].drawCenteredString(matrixStack, s,
                        center,
                        posY - finalMaxOffsetY + 5 - (i * 7),
                        0xFFFFFFFF);
                i++;
            }
        }

    }


    private ArrayList<String> getEnchantment(ItemStack stack) {
        ArrayList<String> list = new ArrayList<>();

        Item item = stack.getItem();
        if (item instanceof AxeItem) {
            handleAxeEnchantments(list, stack);
        } else if (item instanceof ArmorItem) {
            handleArmorEnchantments(list, stack);
        } else if (item instanceof BowItem) {
            handleBowEnchantments(list, stack);
        } else if (item instanceof SwordItem) {
            handleSwordEnchantments(list, stack);
        } else if (item instanceof ToolItem) {
            handleToolEnchantments(list, stack);
        }

        return list;
    }

    private void handleAxeEnchantments(ArrayList<String> list, ItemStack stack) {
        int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
        int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);

        if (sharpness > 0) {
            list.add("Shr" + sharpness);
        }
        if (efficiency > 0) {
            list.add("Eff" + efficiency);
        }
        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
    }

    private void handleArmorEnchantments(ArrayList<String> list, ItemStack stack) {
        int protection = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);
        int thorns = EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        int feather = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack);
        int depth = EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, stack);
        int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
        int bindingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        int fireProt = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, stack);
        int blastProt = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, stack);

        if (vanishingCurse > 0) {
            list.add("Vanish ");
        }
        if (fireProt > 0) {
            list.add("Firp" + fireProt);
        }
        if (blastProt > 0) {
            list.add("Bla" + blastProt);
        }
        if (bindingCurse > 0) {
            list.add("Bindi" + bindingCurse);
        }
        if (depth > 0) {
            list.add("Dep" + depth);
        }
        if (feather > 0) {
            list.add("Fea" + feather);
        }
        if (protection > 0) {
            list.add("Pro" + protection);
        }
        if (thorns > 0) {
            list.add("Thr" + thorns);
        }
        if (mending > 0) {
            list.add("Men" + mending);
        }
        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
    }

    private void handleBowEnchantments(ArrayList<String> list, ItemStack stack) {
        int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
        int bindingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        int infinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        int flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);

        if (vanishingCurse > 0) {
            list.add("Vanish" + vanishingCurse);
        }
        if (bindingCurse > 0) {
            list.add("Binding" + bindingCurse);
        }
        if (infinity > 0) {
            list.add("Inf" + infinity);
        }
        if (power > 0) {
            list.add("Pow" + power);
        }
        if (punch > 0) {
            list.add("Pun" + punch);
        }
        if (mending > 0) {
            list.add("Men" + mending);
        }
        if (flame > 0) {
            list.add("Fla" + flame);
        }
        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
    }

    private void handleSwordEnchantments(ArrayList<String> list, ItemStack stack) {
        int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
        int looting = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack);
        int bindingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        int sweeping = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, stack);
        int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
        int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);
        int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);

        if (vanishingCurse > 0) {
            list.add("Vanish" + vanishingCurse);
        }
        if (looting > 0) {
            list.add("Loot" + looting);
        }
        if (bindingCurse > 0) {
            list.add("Bindi" + bindingCurse);
        }
        if (sweeping > 0) {
            list.add("Swe" + sweeping);
        }
        if (sharpness > 0) {
            list.add("Shr" + sharpness);
        }
        if (knockback > 0) {
            list.add("Kno" + knockback);
        }
        if (fireAspect > 0) {
            list.add("Fir" + fireAspect);
        }
        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
        if (mending > 0) {
            list.add("Men" + mending);
        }
    }

    private void handleToolEnchantments(ArrayList<String> list, ItemStack stack) {
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
        int bindingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
        int silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack);
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);

        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
        if (mending > 0) {
            list.add("Men" + mending);
        }
        if (vanishingCurse > 0) {
            list.add("Vanish" + vanishingCurse);
        }
        if (bindingCurse > 0) {
            list.add("Binding" + bindingCurse);
        }
        if (efficiency > 0) {
            list.add("Eff" + efficiency);
        }
        if (silkTouch > 0) {
            list.add("Sil" + silkTouch);
        }
        if (fortune > 0) {
            list.add("For" + fortune);
        }
    }


}

