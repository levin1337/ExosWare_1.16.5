package ru.levinov.modules.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import lombok.Getter;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import org.joml.Vector4i;
import ru.levinov.Launch;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.events.impl.game.EventAttack;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.modules.settings.imp.MultiBoxSetting;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.MarkerUtils.Mathf;
import ru.levinov.util.animations.Animation;
import ru.levinov.util.animations.Direction;
import ru.levinov.util.animations.impl.EaseBackIn;
import ru.levinov.util.drag.Dragging;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.font.styled.StyledFont;
import ru.levinov.util.misc.HudUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.render.*;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static net.minecraft.client.Minecraft.debugFPS;
import static ru.levinov.util.font.Fonts.*;
import static ru.levinov.util.render.ColorUtil.rgba;
import static ru.levinov.util.render.RenderUtil.Render2D.*;

@FunctionAnnotation(name = "HUD", type = Type.Render,
        desc = "Клиентская часть",
        keywords = {"Худ"})
public class HUD2 extends Function {

    public final MultiBoxSetting elements = new MultiBoxSetting("Элементы",
            new BooleanOption("WaterMark", true),
            new BooleanOption("ArrayList", false),
            new BooleanOption("TargetHUD", true),
            new BooleanOption("KeyBinds", true),
            new BooleanOption("Potions", true),
            new BooleanOption("Notifications", true),
            new BooleanOption("StaffList", true)
    );


    public final BooleanOption inventoryHud = new BooleanOption("InventoryHUD", false);
    public final BooleanOption timerhud = new BooleanOption("TimerHUD", false);
    public final BooleanOption armorHud = new BooleanOption("ArmorHUD", true);

    public final ModeSetting headmode = new ModeSetting("Выбор головы", "3D", "3D","2D");
    public final BooleanOption particlehead = new BooleanOption("Партиклы из головы", true).setVisible(() -> headmode.is("2D"));
    public final ModeSetting particlemode = new ModeSetting("Вид партиклов", "Числа", "Числа","Орбизы").setVisible(() -> particlehead.get());

    public HUD2() {
        addSettings(elements,timerhud,armorHud,inventoryHud,headmode,particlemode,particlehead);
    }
    final int wex_color = new Color(8, 9, 13, 150).getRGB();
    final int newhud_color = new Color(8, 9, 13, 127).getRGB();
    public static final int delta_color = new Color(22, 22, 22, 255).getRGB();
    final int hud_color = new Color(0, 0, 0, 128).getRGB();
    final int t_color = Color.WHITE.getRGB();


    static int[] colors = new int[360];
    final StyledFont small = durman[14];

    MainWindow window;
    private float heightDynamic = 0;
    private int activeModules = 0;
    private float perc;
    float health = 0;
    float health2 = 0;
    private double scale = 0.0D;

    private static float fps = 0;
    List<Function> functions = new ArrayList<>();
    public Dragging InventoryHUD = Launch.createDrag(this, "InventoryHUD", 200, 30);
    final Dragging keyBinds = Launch.createDrag(this, "KeyBindHUD", 10, 100);
    final Dragging potionHUD = Launch.createDrag(this, "PotionHUD", 400, 300);
    final Dragging targetHUD = Launch.createDrag(this, "TargetHUD", 10, 300);
    final Dragging timerHUD = Launch.createDrag(this, "TimerHUD-new", 460, 449);
    public Dragging staffList = Launch.createDrag(this, "StaffList-new", 660, 40);

    Animation tHudAnimation = new EaseBackIn(400, 1, 1.5f);
    LivingEntity target = null;
    private final TimerUtil timerHelper = new TimerUtil();


    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;
        if (event instanceof EventUpdate) {
            staffPlayers.clear();
            for (ScorePlayerTeam team : mc.world.getScoreboard().getTeams().stream().sorted(Comparator.comparing(Team::getName)).toList()) {
                String name = team.getMembershipCollection().toString();
                name = name.substring(1, name.length() - 1);

                if (namePattern.matcher(name).matches()) {
                    if (prefixMatches.matcher(repairString(team.getPrefix().getString().toLowerCase(Locale.ROOT))).matches() || Managment.STAFF_MANAGER.isStaff(name)) {
                        staffPlayers.add(new StaffPlayer(name, team.getPrefix()));
                    }
                }
            }
        }
        if (event instanceof EventUpdate) {
            if (Managment.FUNCTION_MANAGER.getFunctions().isEmpty() || !functions.isEmpty()) return;
            updateFunctions();
        }

        if (event instanceof EventRender e && e.isRender2D()) {
            for (int i = 0; i < colors.length; i++) {
                colors[i] = Managment.STYLE_MANAGER.getCurrentStyle().getColor(i);
            }
            window = e.scaledResolution;
            final MatrixStack matrixStack = e.matrixStack;

            if (elements.get(0)) renderWatermark(matrixStack);
            if (elements.get(1)) renderFunctions(matrixStack);
            if (elements.get(2)) renderTarget(matrixStack);
            if (elements.get(3)) renderKeyBinds(matrixStack);
            if (elements.get(4)) renderPotions(matrixStack);

            if (elements.get(6)) renderStaffList(matrixStack);

            if (timerhud.get()) renderTimer(matrixStack);
            if (inventoryHud.get()) RenderInventoryHUD(matrixStack);
            if (armorHud.get()) ArmorRender(e,matrixStack);
            particleOn(matrixStack);
        }

        if (particlehead.get()) {
            if (event instanceof EventAttack e) {
                for (int i = 0; i < 6; ++i) {
                    particles.add(new HeadParticle(new Vector3d(targetHUD.getX() + 16.5f, targetHUD.getY() + 16.5f, 0.0)));
                }
            }
        }
    }
    //        long currentTime = System.currentTimeMillis();
    //        int frameIndex = (int) ((currentTime / 20) % 45) + 1; // ???? ? 1 ?? 45
    //        drawImage(new ResourceLocation("client/images/capes/Dance/"+ frameIndex + ".jpeg"), x, y, 110, 110, -1);
    //


    public CopyOnWriteArrayList<net.minecraft.util.text.TextComponent> components = new CopyOnWriteArrayList<>();

    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|adm|help|wne|мод|хелп|помо|адм|владе|отри|таф|taf|curat|курато|dev|раз|supp|сапп|yt|ютуб).*");

    private int activeStaff = 0;
    private float hDynam = 0;
    private float widthDynamic = 0;
    private float nameWidth = 0;
    List<StaffPlayer> staffPlayers = new ArrayList<>();

    private void renderStaffList(MatrixStack stack) {
        float posX = staffList.getX();
        float posY = staffList.getY();
        int headerHeight = 14;
        float width = Math.max(nameWidth + 40, 100);
        int padding = 5;
        int offset = 10;
        float height = activeStaff * offset;
        hDynam = AnimationMath.fast(this.hDynam, height, 15);
        widthDynamic = AnimationMath.fast(this.widthDynamic, width, 15);
        drawRoundedCorner(posX, posY, width, headerHeight + 2, 3.0F, HUD2.delta_color);
        hudicon[19].drawString(stack,"E", posX + widthDynamic / 2f - 45,  posY + 6f, ColorUtil.getColorStyle(180));
        durman[16].drawCenteredString(stack, "StaffList", posX + widthDynamic / 2f - 10, posY + 5f, -1);
        RenderUtil.Render2D.drawRoundedCorner(posX, posY + headerHeight , widthDynamic, hDynam + padding / 2f - 2, 3, HUD2.delta_color);
        int index = 0;
        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(posX, posY, widthDynamic, headerHeight + hDynam + padding / 2f + 5);
        if (!staffPlayers.isEmpty()) {
            for (StaffPlayer staff : staffPlayers) {
                String name = staff.getName();
                ITextComponent prefix = staff.getPrefix();
                String status = staff.getStatus().getString();
                msRegular[13].drawString(stack, name, posX + padding, posY + headerHeight + padding + (index * offset) - 1.5f, -1);
                msRegular[13].drawString(stack, status, posX + padding + 75, posY + headerHeight + padding + (index * offset) - 1.5f, -1);
                index++;
            }
        } else {
            nameWidth = 0;
        }
        SmartScissor.unset();
        SmartScissor.pop();
        activeStaff = index;
        staffList.setWidth(widthDynamic);
        staffList.setHeight(hDynam + headerHeight);
    }
    private void RenderInventoryHUD(MatrixStack stack) {
        float x = InventoryHUD.getX();
        float y = InventoryHUD.getY();
        float width = 16.0F;
        float height = 16.0F;
        float y1 = 17.0F;
        float x1 = 0.7F;


        drawRoundedRect(x, y, 150.0F, 15.0F, 2.0F, delta_color);
        durman[16].drawCenteredString(stack, "Inventory",x + 42F, y + 4.5f, -1);
        hudicon[20].drawString(stack, "G", x + 6, y + 5f, ColorUtil.getColorStyle(180));
        for(int i = 9; i < 36; ++i) {
            drawRoundedCorner(x, y + 18, width, height, 3.0F, delta_color);
            ItemStack slot = mc.player.inventory.getStackInSlot(i);
            HudUtil.drawItemStack(slot, x + 0.6F, y + 18.0F, true, true, 0.9F);
            x += width;
            x += x1;
            if (i == 17) {
                y += y1;
                x -= width * 9.0F;
                x -= x1 * 9.0F;
            }

            if (i == 26) {
                y += y1;
                x -= width * 9.0F;
                x -= x1 * 9.0F;
            }
        }

        InventoryHUD.setWidth(150);
        InventoryHUD.setHeight(15);
    }
    private void renderWatermark(MatrixStack matrixStack) {
            fps = AnimationMath.fast(fps, Minecraft.debugFPS, 6);
            int calcfps = Math.round(fps);
            String ping = HudUtil.calculatePing() + " ms";
            String bps = "" + HudUtil.calculateBPS() + " bps";
            int xPosition = (int) (mc.player.getPosX());
            int yPosition = (int) (mc.player.getPosY());
            int zPosition = (int) (mc.player.getPosZ());
            String cord = "" + xPosition + " " + yPosition + " " + zPosition;
            String text = "exosware";
            String user = Managment.USER_PROFILE.getName();
            String fps = calcfps + " FPS";
         //   String text2 = "<< " + calcfps + " fps | " + Managment.USER_PROFILE.getName() + " | " + ping + " | " + bps + " |  " + cord;
            RenderUtil.Render2D.drawRoundedCorner(10,10,62,15,3,delta_color);
            RenderUtil.Render2D.drawRoundedCorner(gilroy[17].getWidth(user) + 102,10,gilroy[17].getWidth(fps) + 22,15,3,delta_color);
            RenderUtil.Render2D.drawRoundedCorner(gilroy[17].getWidth(fps) + gilroy[17].getWidth(user) + 128,10,gilroy[17].getWidth(cord) + 22,15,3,delta_color);

            RenderUtil.Render2D.drawRoundedCorner(75,10,gilroy[17].getWidth(user) + 24,15,3,delta_color);
            RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/logo" + ".png"), 12.5f, 12f, 11, 11, ColorUtil.getColorStyle(180));
        durman[16].drawString(matrixStack,text,26,14.5f,Color.WHITE.getRGB());
        durman[16].drawString(matrixStack,fps,gilroy[17].getWidth(user) + 119,15.3f,Color.WHITE.getRGB());
            wexicon[23].drawString(matrixStack,"B", 79f,  15.3f, ColorUtil.getColorStyle(180));
            //fps
            wexicon[23].drawString(matrixStack,"F", gilroy[17].getWidth(user) + 105.5f,  15.5f, ColorUtil.getColorStyle(180));
            //????
        durman[16].drawString(matrixStack,user,91,15.3f,Color.WHITE.getRGB());
            //?????
            wexicon[23].drawString(matrixStack,"H", gilroy[17].getWidth(fps) + gilroy[17].getWidth(user) + 131,  15.5f, ColorUtil.getColorStyle(180));
        durman[16].drawString(matrixStack,cord, gilroy[17].getWidth(fps) + gilroy[17].getWidth(user) + 143  ,  15.5f, Color.WHITE.getRGB());

         //   msSemiBold[16].drawString(matrixStack,text2,msBold[19].getWidth(text) + 22,17.5f,Color.WHITE.getRGB());
    }
    private void renderKeyBinds(MatrixStack matrixStack) {
            float posX = keyBinds.getX();
            float posY = keyBinds.getY();
            int headerHeight = 14;
            int width = 100;
            int padding = 5;
            float height = activeModules * 10;
            heightDynamic = AnimationMath.fast(this.heightDynamic, height, 15);
            drawRoundedCorner(posX, posY, (float)width, headerHeight + 2, 3.0F, delta_color);
            wexicon[23].drawString(matrixStack,"M",posX + 5,  (double)(posY + 6F), ColorUtil.getColorStyle(180));
            durman[16].drawCenteredString(matrixStack,"KeyBinds", posX + 40, (double)(posY + 6F), -1);
            //??? ??? ???????
            drawRoundedCorner(posX, posY + (float)headerHeight - 2, (float)width, heightDynamic + 5, 4, delta_color);
            SmartScissor.push();
            SmartScissor.setFromComponentCoordinates((double)posX, (double)posY, (double)width, (double)((float)headerHeight + this.heightDynamic + (float)padding / 2.0F) + 5);
            int index = 0;
            for (Function f : Managment.FUNCTION_MANAGER.getFunctions()) {
                if (f.bind != 0 && f.state) {

                    String text = ClientUtil.getKey(f.bind);

                    if (text == null) {
                        continue;
                    }
                    String bindText = "[" + text.toUpperCase() + "]";
                    float bindWidth = durman[12].getWidth(bindText);
                    icons1[15].drawString(matrixStack, f.category.image, posX + padding + 1, posY + headerHeight + padding + (index * 10) + 1, ColorUtil.getColorStyle(180));
                    msRegular[13].drawString(matrixStack, f.name, posX + padding + 15, posY + headerHeight + padding + (index * 10), -1);
                    msRegular[13].drawString(matrixStack, bindText, posX + width - bindWidth - padding, posY + headerHeight + padding + (index * 10) + 1, -1);
                    index++;
                }
            }
            SmartScissor.unset();
            SmartScissor.pop();
            activeModules = index;

            keyBinds.setWidth(width);
            keyBinds.setHeight(activeModules * 10 + headerHeight);
    }
    private void renderFunctions(MatrixStack matrixStack) {
        float padding = 4.0F;
        float dumbOffset = 1.5F;
        float height = this.small.getFontHeight() - dumbOffset + padding + 3.0F;
        List<Function> fs = new ArrayList();
        Iterator var8 = this.functions.iterator();

        while(var8.hasNext()) {
            Function f = (Function)var8.next();
            f.animation = AnimationMath.fast(f.animation, f.state ? 1.0F : 0.0F, 25.0F);
            if (!((double)f.animation < 0.5)) {
                fs.add(f);
            }
        }

        List<Function> fs1 = new ArrayList();
        int in = 0;
        Iterator var10 = this.functions.iterator();

        Function f;
        while(var10.hasNext()) {
            f = (Function)var10.next();
            if (f.state) {
                fs1.add(f);
            }
        }

        for(var10 = fs1.iterator(); var10.hasNext(); ++in) {
            f = (Function)var10.next();
            boolean isLast = in == fs1.size() - 1;
            f.degree = isLast ? 3.0F : Math.min(this.small.getWidth(f.name) + 1.0F - this.small.getWidth(((Function)fs1.get(in + 1)).name), 3.0F);
        }

        float index = 0.0F;

        for(Iterator var19 = fs.iterator(); var19.hasNext(); ++in) {
            f = (Function) var19.next();
            float width = this.small.getWidth(f.name) + padding * 2.0F;
            float r_posX = (float)(this.window.scaledWidth() - 10) - width;
            float r_posY = (float)11 + index * height;
            GlStateManager.pushMatrix();
            GlStateManager.translated((double)(r_posX - 4.0F), (double)r_posY, 0.0);
            GlStateManager.scaled(1.0, (double)f.animation, 1.0);
            GlStateManager.translated((double)(-(r_posX - 4.0F)), (double)(-r_posY), 0.0);
            drawRoundedCorner(r_posX - 10, r_posY,  width + 10, height, 3.0F, delta_color);

            icons1[15].drawString(matrixStack, f.category.image, (double)(r_posX + padding - 10), r_posY + 6, ColorUtil.getColorStyle(180));
            durman[15].drawString(matrixStack,f.name, (double)(r_posX + padding + 1), (double)(r_posY - dumbOffset + height / 2.0F), -1);
            GlStateManager.popMatrix();
            index += f.animation;
        }

    }

    private void renderPotions(MatrixStack matrixStack) {
        float posX = this.potionHUD.getX();
        float posY = this.potionHUD.getY();
        int index = 0;

        List<EffectInstance> activeEffects = mc.player.getActivePotionEffects().stream()
                .sorted(Comparator.comparing(EffectInstance::getDuration))
                .toList();
        float maxWidth = 0;
        for (EffectInstance eff : activeEffects) {
            String effectName = I18n.format(eff.getEffectName());
            String text = effectName + " " + I18n.format("enchantment.level." + (eff.getAmplifier() + 1)) + " - " + EffectUtils.getPotionDurationString(eff, 1.0F);
            float textWidth = msRegular[14].getWidth(text) + 18;
            maxWidth = Math.max(maxWidth, textWidth);
        }

        float backgroundWidth = Math.max(100.0F, maxWidth + 6.0F);

        drawRoundedCorner(posX - 3.0F, posY - 3.0F, backgroundWidth, 15.5f, 3.0F, delta_color);

        hudicon[20].drawString(matrixStack, "B", (posX + 2.0F), posY + 3.0F, ColorUtil.getColorStyle(180));
        durman[16].drawCenteredString(matrixStack, "Potions", posX + 35.0F, posY + 2.5F, -1);
        float mixer = activeEffects.size() * 11f;
        drawRoundedCorner(posX - 3f, posY + 10.0F, backgroundWidth, mixer, 3, delta_color);

        for (EffectInstance eff : activeEffects) {
            String effectName = I18n.format(eff.getEffectName());
            String text = effectName + " " + I18n.format("enchantment.level." + (eff.getAmplifier() + 1)) + " - " + EffectUtils.getPotionDurationString(eff, 1.0F);
            PotionSpriteUploader potionSpriteUploader = mc.getPotionSpriteUploader();
            Effect effect = eff.getPotion();
            TextureAtlasSprite textureAtlasSprite = potionSpriteUploader.getSprite(effect);
            mc.getTextureManager().bindTexture(textureAtlasSprite.getAtlasTexture().getTextureLocation());
            DisplayEffectsScreen.blit(matrixStack, (int) (posX + 3), (int) (posY + 12F + (float) (index * 10)), 10, 8, 8, textureAtlasSprite);

            msRegular[14].drawString(matrixStack, text, (double) (posX + 18), (double) (posY + 10F + (float) (index * 10)) + 4.5, this.t_color);
            index++;
        }

        potionHUD.setWidth(backgroundWidth);
        potionHUD.setHeight(18.0F);
    }

    private final CopyOnWriteArrayList<HeadParticle> particles = new CopyOnWriteArrayList();
    float size;
    private void renderTarget(MatrixStack matrixStack) {
        //     RenderUtil.Render2D.drawRoundFaceCicrle(posX + 6.0F, posY + 6.0F, 26.0F, 26.0F, 12.0F, 1f, (AbstractClientPlayerEntity) this.target);
        //
        float posX = targetHUD.getX();
        float posY = targetHUD.getY();
        targetHUD.setWidth(125.0F);
        targetHUD.setHeight(35f);
        target = getTarget(target);
        scale = tHudAnimation.getOutput();
        if (scale == 0.0) {
            target = null;
        }

        if (target != null) {
            final String targetName = target.getName().getString();
            String substring = targetName.substring(0, Math.min(targetName.length(), 10));
            float healthPercentage = target.getHealth() / target.getMaxHealth();
            float getAbsorptionAmount = target.getAbsorptionAmount();
            health = AnimationMath.fast(health, healthPercentage, 25);
            health = MathHelper.clamp(health, 0.0F, 1.0F);
            health2 = AnimationMath.fast(health2, getAbsorptionAmount, 25);
            health2 = MathHelper.clamp(health2, 0.0F, 7.1f);
            String healthDisplay = String.format(Locale.ENGLISH, "%.0f", health * 20.0F);
            GlStateManager.pushMatrix();


            AnimationMath.sizeAnimation(posX + 50.0F, posY + 19.0F, scale);
            RenderUtil.Render2D.drawRoundedCorner(posX, posY, 125, 35, 3, delta_color);

            drawItemStack(posX + 115f, posY - 12f, -9.0F);

            if (target instanceof AbstractClientPlayerEntity) {

                if (headmode.is("3D")) {
                    RenderUtil.Render2D.drawEntity((int) (posX + 16), (int) (posY + 30), 12, 10, 10, target);
                }
                if (headmode.is("2D")) {
                    RenderUtil.Render2D.drawImage(new ResourceLocation("client/heads/head2.png"), posX + 2F, posY + 2.5F, 30, 30, Color.WHITE.getRGB());
                    particleOn(matrixStack);
                }
            } else {
                RenderUtil.Render2D.drawRoundedCorner(posX + 2, posY + 2.5f, 30, 30, 6, Color.white.getRGB());
                particleOn(matrixStack);
                RenderUtil.Render2D.drawImage(new ResourceLocation("client/imagesNEW/nulltarget.png"), posX + 5F, posY + 5F, 25, 25, Color.WHITE.getRGB());
            }
            //????
            int backroundhpcolor = (new Color(44, 41, 42, 255)).getRGB();
            //???
            drawRoundedCorner(posX + 34.2F, posY + 15F, 88, 4.0F, 2, backroundhpcolor);
            //??
            drawRoundedCorner(posX + 34.2F, posY + 15F, 88 * health, 4.0F, 2, ColorUtil.getColorStyle(180));
            //????? - ????
            drawRoundedCorner(posX + 34.2F, posY + 15F, 4 * health2, 4.0F, 2, Color.YELLOW.getRGB());
            //???
            durman[16].drawString(matrixStack, substring, posX + 34.0F, posY + 4.0F, t_color);
            //??
            float healthBarWidth = 88 * health; // 88 - ??? ???????????? ?????? ???????

// ??????? ??????: ??????? ??? ? ????? ??????? ????????
            float textXPosition = posX + 74 + (healthBarWidth - msMedium[13].getWidth(healthDisplay)) / 2;

// ????????? ??????
            msMedium[13].drawString(matrixStack, healthDisplay, textXPosition, posY + 25f, Color.WHITE.getRGB());   //????? ?????????
            GlStateManager.popMatrix();
        }
    }
    public void particleOn(MatrixStack matrixStack) {
        if (particlehead.get() && headmode.is("2D")) {
            if (target != null) {
                for (HeadParticle p : particles) {
                    if (System.currentTimeMillis() - p.time > 2000L) {
                        particles.remove(p);
                    }
                    p.update();
                    size = 1.0f - (float) (System.currentTimeMillis() - p.time) / 2000.0f;
                    if (particlehead.get() && particlemode.is("Числа")) {
                        gilroy[15].drawString(matrixStack, ClientUtil.gradient(String.valueOf(p.number), ColorUtil.getColorStyle(60), ColorUtil.getColorStyle(360)), p.pos.x, p.pos.y, ColorUtil.getColorStyle(360));
                    }
                    if (particlehead.get() && particlemode.is("Орбизы")) {
                        RenderUtil.Render2D.drawCircle((float) p.pos.x, (float) p.pos.y, 0, 360, 2, 1f, false, Color.BLACK.getRGB());
                        RenderUtil.Render2D.drawRoundCircle((float) p.pos.x, (float) p.pos.y, 4.0f, ColorUtil.getColorStyle(255 * p.alpha * size));
                    }
                }
            } else {
                particles.clear();
            }
        }
    }
    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min; // ?????????? ????????? ????? ? ????????? ?? min ?? max
    }
    private void drawItemStack(float x, float y, float offset) {
        List<ItemStack> stacks = new ArrayList<>(Arrays.asList(target.getHeldItemMainhand(), target.getHeldItemOffhand()));
        target.getArmorInventoryList().forEach(stacks::add);
        stacks.removeIf(w -> w.getItem() instanceof AirItem);
        Collections.reverse(stacks);
        final AtomicReference<Float> posX = new AtomicReference<>(x);

        stacks.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> HudUtil.drawItemStack(stack, posX.getAndAccumulate(offset, Float::sum), y, true, true, 0.6f));
    }








    private void renderTimer(MatrixStack matrixStack) {
        float posX = timerHUD.getX();
        float posY = timerHUD.getY();
        int circle_max = 70;
        float quotient = Managment.FUNCTION_MANAGER.timerFunction.maxViolation / Managment.FUNCTION_MANAGER.timerFunction.timerAmount.getValue().floatValue();
        float minimumValue = Math.min(Managment.FUNCTION_MANAGER.timerFunction.getViolation(), quotient);
        perc = AnimationMath.lerp(perc, (quotient - minimumValue) / quotient, 10.0F);
        RenderUtil.Render2D.drawRoundedCorner(posX,posY,80,20,3,delta_color);
        int backroundhpcolor = (new Color(44, 41, 42, 255)).getRGB();
        RenderUtil.Render2D.drawRoundedCorner(posX + 5,posY + 10,70,8,3,backroundhpcolor);
        RenderUtil.Render2D.drawRoundedCorner(posX + 5,posY + 10,(float)circle_max * perc,8,3, ColorUtil.getColorStyle(180));
        durman[16].drawCenteredString(matrixStack, "Timer", (double)(posX + 40.0F), (double)(posY + 2.0F), -1);


        timerHUD.setWidth(70);
        timerHUD.setHeight(20);
    }


    private LivingEntity getTarget(LivingEntity nullTarget) {
        LivingEntity target = nullTarget;

        // ????????? auraFunction

        if (Managment.FUNCTION_MANAGER.auraFunction.getTarget() instanceof LivingEntity) {
            target = (LivingEntity) Managment.FUNCTION_MANAGER.auraFunction.getTarget();
            tHudAnimation.setDirection(Direction.FORWARDS);
        } else if (Managment.FUNCTION_MANAGER.tpAura.getTarget() instanceof LivingEntity) {
            target = (LivingEntity) Managment.FUNCTION_MANAGER.tpAura.getTarget();
            tHudAnimation.setDirection(Direction.FORWARDS);
        }


        // ????????? ????? ????
        else if (mc.currentScreen instanceof ChatScreen) {
            target = mc.player;
            tHudAnimation.setDirection(Direction.FORWARDS);
        } else {
            tHudAnimation.setDirection(Direction.BACKWARDS);
        }

        return target;
    }

    private void updateFunctions() {
        for (Function function : Managment.FUNCTION_MANAGER.getFunctions()) {
            if (function.category == Type.Render) continue;

            functions.add(function);
        }

        functions.sort((f1, f2) -> Float.compare(small.getWidth(f2.name), small.getWidth(f1.name)));
    }
    private void ArmorRender(final EventRender renderEvent,MatrixStack stack) {
        int count = 0;
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == Items.TOTEM_OF_UNDYING) {
                count++;
            }
        }
        float xPos = renderEvent.scaledResolution.scaledWidth() / 2f + 90;
        float yPos = renderEvent.scaledResolution.scaledHeight() + 35;

        boolean totemInInv = mc.player.inventory.mainInventory.stream().map(ItemStack::getItem).toList().contains(Items.TOTEM_OF_UNDYING);
        int off = totemInInv ? +5 : 0;
        for (ItemStack s : mc.player.inventory.armorInventory) {
            NameTags.drawItemStack(s, xPos - off + 74 * (mc.gameSettings.guiScale / 2f), yPos - 56 * (mc.gameSettings.guiScale / 2f), null, false);
            off += 15;
        }
        if (totemInInv)
            NameTags.drawItemStack(new ItemStack(Items.TOTEM_OF_UNDYING), xPos - off + 73 * (mc.gameSettings.guiScale / 2f), yPos - 56 * (mc.gameSettings.guiScale / 2f), String.valueOf(count), false);


    }

    private class HeadParticle {
        private Vector3d pos;
        private final Vector3d end;
        private final long time;
        private float alpha;
        public int number; // ????? ???? ??? ???????? ?????????? ?????


        public HeadParticle(Vector3d pos) {
            this.pos = pos;
            this.end = pos.add(-ThreadLocalRandom.current().nextFloat(-40.0f, 40.0f), -ThreadLocalRandom.current().nextFloat(-40.0f, 40.0f), -ThreadLocalRandom.current().nextFloat(-40.0f, 40.0f));
            this.time = System.currentTimeMillis();
            this.number = generateRandomNumber(1, 9); // ?????????? ????????? ????? ??? ????????
        }

        public void update() {
            this.alpha = Mathf.lerp(this.alpha, 1.0f, 15.0f);
            this.pos = Mathf.fast(this.pos, this.end, 2.5f);
        }
    }

    private String repairString(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (c >= 65281 && c <= 65374) {
                sb.append((char) (c - 65248));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private class StaffPlayer {

        @Getter
        String name;
        @Getter
        ITextComponent prefix;
        @Getter
        Status status;

        private StaffPlayer(String name, ITextComponent prefix) {
            this.name = name;
            this.prefix = prefix;
            updateStatus();
        }

        private void updateStatus() {
            for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
                if (player.getNameClear().equals(name)) {
                    status = Status.NEAR;
                    return;
                }
            }

            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                if (info.getGameProfile().getName().equals(name)) {
                    if (info.getGameType() == GameType.SPECTATOR) {
                        status = Status.SPEC;
                        return;
                    }

                    status = Status.NONE;
                    return;
                }
            }

            status = Status.VANISHED;
            Managment.FUNCTION_MANAGER.griefHelper.staff.get(); {
                if (timerHelper.hasTimeElapsed(35000)) {
                    Managment.NOTIFICATION_MANAGER.add(TextFormatting.BLUE + "Модер в спеке", "StaffList", 3);
                    timerHelper.reset();
                }
            }
        }
    }

    public enum Status {
        NONE("§2[on]"),
        NEAR("§6[near]"),
        SPEC("§e[gm3]"),
        VANISHED("§c[vanish]");

        @Getter
        final String string;

        Status(String string) {
            this.string = string;
        }
    }
}