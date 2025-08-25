/* Decompiler 119ms, total 598ms, lines 381 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.gui.widget.button.Button.ITooltip;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector4i;
import ru.levinov.Launch;
import ru.levinov.managment.Managment;
import ru.levinov.ui.midnight.StyleManager.HexColor;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.ScaleMath;
import ru.levinov.util.render.Vec2i;
import ru.levinov.util.render.RenderUtil.Render2D;
import ru.levinov.util.render.animation.AnimationMath;

public class MainMenuScreen extends Screen {
    public static final RenderSkyboxCube PANORAMA_RESOURCES = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
    private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private static final ResourceLocation ACCESSIBILITY_TEXTURES = new ResourceLocation("textures/gui/accessibility.png");
    private final boolean showTitleWronglySpelled;
    private MainMenuScreen.Button buttonResetDemo;
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    private int widthCopyright;
    private int widthCopyrightRest;
    private Screen realmsNotification;
    private boolean hasCheckedForRealmsNotification;
    @Nullable
    private String splashText;
    private final RenderSkybox panorama;
    private final boolean showFadeInAnimation;
    private long firstRenderTime;

    public MainMenuScreen() {
        this(false);
    }

    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        if (ClientUtil.legitMode) {
            //Если включен
            if (this.splashText == null) {
                this.splashText = this.minecraft.getSplashes().getSplashText();
            }

            this.widthCopyright = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
            this.widthCopyrightRest = this.width - this.widthCopyright - 2;
            int i;
            int j = this.height / 4 + 48;
            net.minecraft.client.gui.widget.button.Button button = null;
            this.addSingleplayerMultiplayerButtons(j, 24);
            if (Reflector.ModListScreen_Constructor.exists()) {
                button = ReflectorForge.makeButtonMods(this, j, 24);
                this.addButton(button);
            }

            this.addButton(new ImageButton(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, net.minecraft.client.gui.widget.button.Button.WIDGETS_LOCATION, 256, 256, (p_lambda$init$0_1_) -> {
                this.minecraft.displayGuiScreen(new LanguageScreen(this, this.minecraft.gameSettings, this.minecraft.getLanguageManager()));
            }, new TranslationTextComponent("narrator.button.language")));
            this.addButton(new net.minecraft.client.gui.widget.button.Button(this.width / 2 - 100, j + 72 + 12, 98, 20, new TranslationTextComponent("menu.options"), (p_lambda$init$1_1_) -> {
                this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
            }));
            this.addButton(new net.minecraft.client.gui.widget.button.Button(this.width / 2 + 2, j + 72 + 12, 98, 20, new TranslationTextComponent("menu.quit"), (p_lambda$init$2_1_) -> {
                this.minecraft.shutdown();
            }));
            this.addButton(new ImageButton(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURES, 32, 64, (p_lambda$init$3_1_) -> {
                this.minecraft.displayGuiScreen(new AccessibilityScreen(this, this.minecraft.gameSettings));
            }, new TranslationTextComponent("narrator.button.accessibility")));
            this.minecraft.setConnectedToRealms(false);
            if (this.minecraft.gameSettings.realmsNotifications && !this.hasCheckedForRealmsNotification) {
                RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                this.realmsNotification = realmsbridgescreen.func_239555_b_(this);
                this.hasCheckedForRealmsNotification = true;
            }

            if (this.areRealmsNotificationsEnabled()) {
                this.realmsNotification.init(this.minecraft, this.width, this.height);
            }

        } else {
            if (this.splashText == null) {
                this.splashText = this.minecraft.getSplashes().getSplashText();
            }
            this.widthCopyrightRest = this.width - this.widthCopyright - 2;
            int j = this.height / 4 + 48;
            net.minecraft.client.gui.widget.button.Button button = null;
            this.addSingleplayerMultiplayerButtonsRage(j, 24);
            if (Reflector.ModListScreen_Constructor.exists()) {
                button = ReflectorForge.makeButtonMods(this, j, 24);
                this.addButton(button);
            }

            this.addButton(new ImageButton(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, net.minecraft.client.gui.widget.button.Button.WIDGETS_LOCATION, 256, 256, (p_lambda$init$0_1_) -> {
                this.minecraft.displayGuiScreen(new LanguageScreen(this, this.minecraft.gameSettings, this.minecraft.getLanguageManager()));
            }, new TranslationTextComponent("narrator.button.language")));
            this.addButton(new net.minecraft.client.gui.widget.button.Button(this.width / 2 - 100, j + 72 + 12, 98, 20, new TranslationTextComponent("menu.options"), (p_lambda$init$1_1_) -> {
                this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
            }));
            this.addButton(new net.minecraft.client.gui.widget.button.Button(this.width / 2 + 2, j + 72 + 12, 98, 20, new TranslationTextComponent("menu.quit"), (p_lambda$init$2_1_) -> {
                this.minecraft.shutdown();
            }));


        }
    }

    private boolean areRealmsNotificationsEnabled() {
        return this.minecraft.gameSettings.realmsNotifications && this.realmsNotification != null;
    }


    private void addSingleplayerMultiplayerButtons(int yIn, int rowHeightIn) {
        this.addButton(new net.minecraft.client.gui.widget.button.Button(this.width / 2 - 100, yIn, 200, 20, new TranslationTextComponent("menu.singleplayer"), (p_lambda$addSingleplayerMultiplayerButtons$4_1_) -> {
            this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
        }));
        boolean flag = this.minecraft.isMultiplayerEnabled();
        ITooltip button$itooltip = flag ? net.minecraft.client.gui.widget.button.Button.field_238486_s_ : (p_lambda$addSingleplayerMultiplayerButtons$5_1_, p_lambda$addSingleplayerMultiplayerButtons$5_2_, p_lambda$addSingleplayerMultiplayerButtons$5_3_, p_lambda$addSingleplayerMultiplayerButtons$5_4_) -> {
            if (!p_lambda$addSingleplayerMultiplayerButtons$5_1_.active) {
                this.renderTooltip(p_lambda$addSingleplayerMultiplayerButtons$5_2_, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), p_lambda$addSingleplayerMultiplayerButtons$5_3_, p_lambda$addSingleplayerMultiplayerButtons$5_4_);
            }

        };
        ((net.minecraft.client.gui.widget.button.Button)this.addButton(new net.minecraft.client.gui.widget.button.Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, new TranslationTextComponent("menu.multiplayer"), (p_lambda$addSingleplayerMultiplayerButtons$6_1_) -> {
            Screen screen = this.minecraft.gameSettings.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.minecraft.displayGuiScreen((Screen)screen);
        }, button$itooltip))).active = flag;
        ((net.minecraft.client.gui.widget.button.Button)this.addButton(new net.minecraft.client.gui.widget.button.Button(this.width / 2 - 100, yIn + rowHeightIn * 2, 200, 20, new TranslationTextComponent("menu.online"), (p_lambda$addSingleplayerMultiplayerButtons$7_1_) -> {
            this.switchToRealms();
        }, button$itooltip))).active = flag;
        if (Reflector.ModListScreen_Constructor.exists() && this.buttons.size() > 0) {
            Widget widget = (Widget)this.buttons.get(this.buttons.size() - 1);
            widget.x = this.width / 2 + 2;
            widget.setWidth(98);
        }
    }

    private void addSingleplayerMultiplayerButtonsRage(int yIn, int rowHeightIn) {
        this.addButton(new net.minecraft.client.gui.widget.button.Button(this.width / 2 - 100, yIn, 200, 20, new TranslationTextComponent("menu.singleplayer"), (p_lambda$addSingleplayerMultiplayerButtons$4_1_) -> {
            this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
        }));
        boolean flag = this.minecraft.isMultiplayerEnabled();
        ITooltip button$itooltip = flag ? net.minecraft.client.gui.widget.button.Button.field_238486_s_ : (p_lambda$addSingleplayerMultiplayerButtons$5_1_, p_lambda$addSingleplayerMultiplayerButtons$5_2_, p_lambda$addSingleplayerMultiplayerButtons$5_3_, p_lambda$addSingleplayerMultiplayerButtons$5_4_) -> {
            if (!p_lambda$addSingleplayerMultiplayerButtons$5_1_.active) {
                this.renderTooltip(p_lambda$addSingleplayerMultiplayerButtons$5_2_, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), p_lambda$addSingleplayerMultiplayerButtons$5_3_, p_lambda$addSingleplayerMultiplayerButtons$5_4_);
            }

        };
        ((net.minecraft.client.gui.widget.button.Button)this.addButton(new net.minecraft.client.gui.widget.button.Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, new TranslationTextComponent("menu.multiplayer"), (p_lambda$addSingleplayerMultiplayerButtons$6_1_) -> {
            Screen screen = this.minecraft.gameSettings.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.minecraft.displayGuiScreen((Screen)screen);
        }, button$itooltip))).active = flag;


        this.addButton(new net.minecraft.client.gui.widget.button.Button(width / 2 - 100, yIn + 48, 200, 20, new StringTextComponent("Аккаунты"), (p_lambda$addSingleplayerMultiplayerButtons$4_1_) -> {
                    minecraft.displayGuiScreen(Managment.ALT);
        }));

    }

    private void switchToRealms() {
        RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
        realmsbridgescreen.func_231394_a_(this);
    }

    public MainMenuScreen(boolean fadeIn) {
        super(new TranslationTextComponent("narrator.screen.title"));
        this.panorama = new RenderSkybox(PANORAMA_RESOURCES);
        this.showFadeInAnimation = fadeIn;
        this.showTitleWronglySpelled = (double)(new Random()).nextFloat() < 1.0E-4D;
    }

    public void tick() {
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotification.tick();
        }
    }

    public static CompletableFuture<Void> loadAsync(TextureManager texMngr, Executor backgroundExecutor) {
        return CompletableFuture.allOf(texMngr.loadAsync(MINECRAFT_TITLE_TEXTURES, backgroundExecutor), texMngr.loadAsync(MINECRAFT_TITLE_EDITION, backgroundExecutor), texMngr.loadAsync(PANORAMA_OVERLAY_TEXTURES, backgroundExecutor), PANORAMA_RESOURCES.loadAsync(texMngr, backgroundExecutor));
    }

    public boolean isPauseScreen() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float f;
        int l;
        if (!ClientUtil.legitMode) {
            if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
                this.firstRenderTime = Util.milliTime();
            }

            f = this.showFadeInAnimation ? (float)(Util.milliTime() - this.firstRenderTime) / 1000.0F : 1.0F;
            GlStateManager.disableDepthTest();
            fill(matrixStack, 0, 0, this.width, this.height, -1);
            this.panorama.render(partialTicks, MathHelper.clamp(f, 0.0F, 1.0F));
            int j = this.width / 2 - 137;
            this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.showFadeInAnimation ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
            blit(matrixStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
            float f1 = this.showFadeInAnimation ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
            l = MathHelper.ceil(f1 * 255.0F) << 24;
            if ((l & -67108864) != 0) {
                this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
                if (this.showTitleWronglySpelled) {
                    this.blitBlackOutline(j, 30, (p_lambda$render$10_2_, p_lambda$render$10_3_) -> {
                        this.blit(matrixStack, p_lambda$render$10_2_ + 0, p_lambda$render$10_3_, 0, 0, 99, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99, p_lambda$render$10_3_, 129, 0, 27, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99 + 26, p_lambda$render$10_3_, 126, 0, 3, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99 + 26 + 3, p_lambda$render$10_3_, 99, 0, 26, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 155, p_lambda$render$10_3_, 0, 45, 155, 44);
                    });
                } else {
                    this.blitBlackOutline(j, 30, (p_lambda$render$11_2_, p_lambda$render$11_3_) -> {
                        this.blit(matrixStack, p_lambda$render$11_2_ + 0, p_lambda$render$11_3_, 0, 0, 155, 44);
                        this.blit(matrixStack, p_lambda$render$11_2_ + 155, p_lambda$render$11_3_, 0, 45, 155, 44);
                    });
                }

                this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
                blit(matrixStack, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
                if (Reflector.ForgeHooksClient_renderMainMenu.exists()) {
                    Reflector.callVoid(Reflector.ForgeHooksClient_renderMainMenu, new Object[]{this, matrixStack, this.font, this.width, this.height, l});
                }


                if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height) {
                    fill(matrixStack, this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, 16777215 | l);
                }

                Iterator var19 = this.buttons.iterator();

                while(var19.hasNext()) {
                    Widget widget = (Widget)var19.next();
                    widget.setAlpha(f1);
                }

                if (this.areRealmsNotificationsEnabled() && f1 >= 1.0F) {
                    this.realmsNotification.render(matrixStack, mouseX, mouseY, partialTicks);
                }
                String log = "Тут пусто...";
                //раз
                String[] lines = log.split("\n");
                //пос
                int x = 5;
                int y = 20;

                for (String line : lines) {
                    String colorCode = TextFormatting.YELLOW.toString();
                    //пруфиксы дефалд
                    if (line.startsWith("[+]")) {
                        colorCode = TextFormatting.YELLOW.toString();
                    } else if (line.startsWith("[-]")) {
                        colorCode = TextFormatting.RED.toString();
                    } else if (line.startsWith("[*]")) {
                        colorCode = TextFormatting.BLUE.toString();
                    } else if (line.startsWith("[/]")) {
                        colorCode = TextFormatting.GREEN.toString();
                    }
                    Minecraft.getInstance().fontRenderer.drawString(matrixStack, colorCode + line, x, y, Color.WHITE.getRGB());
                    y += Minecraft.getInstance().fontRenderer.FONT_HEIGHT; //высокий
                }

                //ChangeLog
                Minecraft.getInstance().fontRenderer.drawString(matrixStack, TextFormatting.GREEN + "Build: " + TextFormatting.WHITE + Launch.version + " ",5,5,Color.WHITE.getRGB());
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        } else {
            if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
                this.firstRenderTime = Util.milliTime();
            }

            f = this.showFadeInAnimation ? (float)(Util.milliTime() - this.firstRenderTime) / 1000.0F : 1.0F;
            GlStateManager.disableDepthTest();
            fill(matrixStack, 0, 0, this.width, this.height, -1);
            this.panorama.render(partialTicks, MathHelper.clamp(f, 0.0F, 1.0F));
            int i;
            int j = this.width / 2 - 137;
            int k;
            this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.showFadeInAnimation ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
            blit(matrixStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
            float f1 = this.showFadeInAnimation ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
            l = MathHelper.ceil(f1 * 255.0F) << 24;
            if ((l & -67108864) != 0) {
                this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
                if (this.showTitleWronglySpelled) {
                    this.blitBlackOutline(j, 30, (p_lambda$render$10_2_, p_lambda$render$10_3_) -> {
                        this.blit(matrixStack, p_lambda$render$10_2_ + 0, p_lambda$render$10_3_, 0, 0, 99, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99, p_lambda$render$10_3_, 129, 0, 27, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99 + 26, p_lambda$render$10_3_, 126, 0, 3, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99 + 26 + 3, p_lambda$render$10_3_, 99, 0, 26, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 155, p_lambda$render$10_3_, 0, 45, 155, 44);
                    });
                } else {
                    this.blitBlackOutline(j, 30, (p_lambda$render$11_2_, p_lambda$render$11_3_) -> {
                        this.blit(matrixStack, p_lambda$render$11_2_ + 0, p_lambda$render$11_3_, 0, 0, 155, 44);
                        this.blit(matrixStack, p_lambda$render$11_2_ + 155, p_lambda$render$11_3_, 0, 45, 155, 44);
                    });
                }

                this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
                blit(matrixStack, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
                if (Reflector.ForgeHooksClient_renderMainMenu.exists()) {
                    Reflector.callVoid(Reflector.ForgeHooksClient_renderMainMenu, new Object[]{this, matrixStack, this.font, this.width, this.height, l});
                }

                if (this.splashText != null) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
                    RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
                    float f2 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.milliTime() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
                    f2 = f2 * 100.0F / (float)(this.font.getStringWidth(this.splashText) + 32);
                    RenderSystem.scalef(f2, f2, f2);
                    drawCenteredString(matrixStack, this.font, this.splashText, 0, -8, 16776960 | l);
                    RenderSystem.popMatrix();
                }

                String s = "Minecraft " + SharedConstants.getVersion().getName();
                if (this.minecraft.isDemo()) {
                    s = s + " Demo";
                } else {
                    s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
                }

                if (this.minecraft.isModdedClient()) {
                    s = s + I18n.format("menu.modded", new Object[0]);
                }

                if (Reflector.BrandingControl.exists()) {
                    BiConsumer biconsumer1;
                    if (Reflector.BrandingControl_forEachLine.exists()) {
                        biconsumer1 = (p_lambda$render$12_3_, p_lambda$render$12_4_) -> {
                        };
                        Reflector.call(Reflector.BrandingControl_forEachLine, new Object[]{true, true, biconsumer1});
                    }

                    if (Reflector.BrandingControl_forEachAboveCopyrightLine.exists()) {
                        biconsumer1 = (p_lambda$render$13_3_, p_lambda$render$13_4_) -> {
                        };
                        Reflector.call(Reflector.BrandingControl_forEachAboveCopyrightLine, new Object[]{biconsumer1});
                    }
                } else {
                    drawString(matrixStack, this.font, s, 2, this.height - 10, 16777215 | l);
                }

                drawString(matrixStack, this.font, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, 16777215 | l);
                if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height) {
                    fill(matrixStack, this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, 16777215 | l);
                }

                Iterator var19 = this.buttons.iterator();

                while(var19.hasNext()) {
                    Widget widget = (Widget)var19.next();
                    widget.setAlpha(f1);
                }

                if (this.areRealmsNotificationsEnabled() && f1 >= 1.0F) {
                    this.realmsNotification.render(matrixStack, mouseX, mouseY, partialTicks);
                }

                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }

        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int)mouseX, (int)mouseY);
        mouseX = (double)fixed.getX();
        mouseY = (double)fixed.getY();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void onClose() {
        if (this.realmsNotification != null) {
            this.realmsNotification.onClose();
        }

    }

    public static class Button extends AbstractButton {
        public static final ITooltip field_238486_s_ = (button, matrixStack, mouseX, mouseY) -> {
        };
        protected final IPressable onPress;
        protected final ITooltip onTooltip;
        public float animation;

        public Button(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction) {
            this(x, y, width, height, title, pressedAction, field_238486_s_);
        }

        public Button(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, ITooltip onTooltip) {
            super(x, y, width, height, title);
            this.onPress = pressedAction;
            this.onTooltip = onTooltip;
        }

        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            animation = AnimationMath.lerp(animation, isHovered() ? 1f : 0, 5);
            int firstColor = Managment.STYLE_MANAGER.getCurrentStyle().getColor(30);
            int secondColor = Managment.STYLE_MANAGER.getCurrentStyle().getColor(360);
            int color = ColorUtil.interpolateColor(new Color(35, 35, 35,255).getRGB(), new Color(38, 38, 38,255).getRGB(), animation / 1);

            int backgroundColor1 =  new Color(16, 16, 16, 255).getRGB();
            int backgroundColor2 =  new Color(16, 16, 16, 210).getRGB();

            RenderUtil.Render2D.drawShadow(x + 4, y + 2, width , height , 6,secondColor,firstColor, firstColor, secondColor);
            RenderUtil.Render2D.drawRoundOutline(x + 4, y + 2, width , height , 2, 0, backgroundColor1, new Vector4i(secondColor, firstColor, firstColor,secondColor));

            {

            }

            Fonts.msSemiBold[20].drawCenteredString(matrixStack, ClientUtil.gradient(this.getMessage().getString(), Managment.STYLE_MANAGER.getCurrentStyle().getColor(360), Managment.STYLE_MANAGER.getCurrentStyle().getColor(60)), x + width / 1.95f, y + height / 2 - Fonts.msSemiBold[16].getFontHeight() / 2f + 2, RenderUtil.reAlphaInt(ColorUtil.interpolateColor(new Color(45, 45, 45, 255).getRGB(), Color.WHITE.getRGB(), animation), (int) (225)));
        }

        public void onPress() {
            this.onPress.onPress(this);
        }
    }
}