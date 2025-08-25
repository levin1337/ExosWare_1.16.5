package ru.levinov.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.Type;
import ru.levinov.ui.clickgui.configs.ConfigDrawing;
import ru.levinov.ui.clickgui.objects.ModuleObject;
import ru.levinov.ui.clickgui.objects.Object;
import ru.levinov.ui.clickgui.objects.sets.BindObject;
import ru.levinov.ui.clickgui.theme.ThemeDrawing;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.GaussianBlur;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.ScaleMath;
import ru.levinov.util.render.SmartScissor;
import ru.levinov.util.render.Vec2i;
import ru.levinov.util.render.RenderUtil.Render2D;
import ru.levinov.util.render.animation.AnimationMath;

public class Window extends Screen {
    private Vector2f position = new Vector2f(0.0F, 0.0F);
    public static Vector2f size = new Vector2f(500.0F, 400.0F);
    public static int dark = (new Color(18, 19, 25)).getRGB();
    public static int medium = (new Color(18, 19, 25)).brighter().getRGB();
    public static int light = (new Color(129, 134, 153)).getRGB();
    private Type currentCategory;
    private ThemeDrawing themeDrawing = new ThemeDrawing();
    private ConfigDrawing configDrawing = new ConfigDrawing();
    private ArrayList<ModuleObject> objects = new ArrayList();
    int t_color;
    public static float scrolling;
    public static float scrollingOut;
    public boolean searching;
    private String searchText;

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scrolling += (float)(delta * 30.0D);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public Window(ITextComponent titleIn) {
        super(titleIn);
        this.t_color = Color.WHITE.getRGB();
        this.searchText = "";
        scrolling = 0.0F;
        Iterator var2 = Managment.FUNCTION_MANAGER.getFunctions().iterator();

        while(var2.hasNext()) {
            Function function = (Function)var2.next();
            this.objects.add(new ModuleObject(function));
        }

    }

    protected void init() {
        super.init();
        size = new Vector2f(600.0F, 400.0F);
        this.position = new Vector2f((float)IMinecraft.mc.getMainWindow().scaledWidth() / 5.5F - size.x / 3.5F, (float)IMinecraft.mc.getMainWindow().scaledHeight() / 2.0F - size.y / 2.0F);
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        Vec2i fixed = ScaleMath.getMouse(mouseX, mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        if (Managment.FUNCTION_MANAGER.clickGui.glow.get()) {
            RenderUtil.Render2D.drawShadowyestfps(-100,500, Minecraft.getInstance().getMainWindow().scaledWidth(),15,150,ColorUtil.rgba(0,0,255,200));
        }
        if (Managment.FUNCTION_MANAGER.clickGui.blur.get()) {
            GaussianBlur.startBlur();
            RenderUtil.Render2D.drawRect(0.0F, 0.0F, (float)this.width, (float)this.height, -1);
            GaussianBlur.endBlur(Managment.FUNCTION_MANAGER.clickGui.blurVal.getValue().floatValue(), 1.0F);
        }

        float bar = 100.0F;
        IMinecraft.mc.gameRenderer.setupOverlayRendering(2);
        Render2D.drawRoundedCorner(this.position.x + bar, this.position.y, size.x - bar, size.y + 20.0F, new Vector4f(0.0F, 20.0F, 0.0F, 20.0F), dark);
        IMinecraft.mc.getTextureManager().bindTexture(new ResourceLocation("client/images/ui/menu.jpg"));
        Render2D.drawRoundedCorner(this.position.x + 100.0F, this.position.y - 50.0F, bar + 400.0F, size.y - 350.0F, new Vector4f(8.0F, 0.0F, 8.0F, 0.0F), RenderUtil.reAlphaInt(medium, 255));
        int black = (new Color(0, 0, 0, 255)).getRGB();
        int white = (new Color(255, 255, 255, 255)).getRGB();
        Render2D.drawRoundedCorner(this.position.x + 110.0F, this.position.y - 35.0F, bar - 15.0F, 17.0F, 5.0F, black);
        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates((double)(this.position.x + 110.0F), (double)(this.position.y - 35.0F), (double)(bar - 15.0F), 17.0D);
        if (!this.searching && this.searchText.isEmpty()) {
            Fonts.gilroy[14].drawString(matrixStack, "Ïîèñê", (double)(this.position.x + 110.0F + 30.0F), (double)(this.position.y - 28.0F), white);
        } else {
            Fonts.gilroy[14].drawString(matrixStack, this.searchText + (this.searching ? (System.currentTimeMillis() % 1000L > 500L ? "_" : "") : ""), (double)(this.position.x + 110.0F + 30.0F), (double)(this.position.y - 28.0F), white);
        }

        SmartScissor.unset();
        SmartScissor.pop();
        Fonts.icons1[12].drawString(matrixStack, "I", (double)(this.position.x + 120.0F), (double)(this.position.y - 27.0F), white);
        Render2D.drawRoundedCorner(this.position.x + 100.0F, this.position.y - 18.0F, 98.0F, 18.0F, new Vector4f(0.0F, 0.0F, 8.0F, 0.0F), RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0.0F), 150));
        Fonts.durman[17].drawCenteredString(matrixStack, "Åõîswàrå Alpha", (double)(this.position.x + bar + 53.0F), (double)(this.position.y - 12.0F), white);
        float len = 0.0F;
        Type[] var10 = Type.values();
        int var11 = var10.length;

        for(int var12 = 0; var12 < var11; ++var12) {
            Type type = var10[var12];
            len = (float)(type.ordinal() * 30);
            type.anim = (double)AnimationMath.fast((float)type.anim, type == this.currentCategory ? 1.0F : 0.0F, 1000.0F);
            if (type.anim > 0.0D) {
                Render2D.drawGradientRound(this.position.x + 210.0F + len, this.position.y - 42.0F, bar - 70.0F, 35.0F, 5.0F, RenderUtil.reAlphaInt(ColorUtil.getColorStyle(0.0F), (int)(100.0D * type.anim)), RenderUtil.reAlphaInt(ColorUtil.getColorStyle(90.0F), (int)(100.0D * type.anim)), RenderUtil.reAlphaInt(ColorUtil.getColorStyle(180.0F), (int)(100.0D * type.anim)), RenderUtil.reAlphaInt(ColorUtil.getColorStyle(360.0F), (int)(100.0D * type.anim)));
            }

            Fonts.icons1[20].drawString(matrixStack, type.image, (double)(this.position.x + 220.0F + len), (double)(this.position.y - 33.0F), white);
            Fonts.sora[12].drawString(matrixStack, "COMBAT", (double)(this.position.x + 210.5F), (double)(this.position.y - 15.0F), white);
            Fonts.sora[12].drawString(matrixStack, "MOVE", (double)(this.position.x + 245.5F), (double)(this.position.y - 15.0F), white);
            Fonts.sora[12].drawString(matrixStack, "RENDER", (double)(this.position.x + 271.5F), (double)(this.position.y - 15.0F), white);
            Fonts.sora[12].drawString(matrixStack, "PLAYER", (double)(this.position.x + 303.0F), (double)(this.position.y - 15.0F), white);
            Fonts.sora[12].drawString(matrixStack, "UTIL", (double)(this.position.x + 338.5F), (double)(this.position.y - 15.0F), white);
            Fonts.sora[12].drawString(matrixStack, "MISC", (double)(this.position.x + 366.5F), (double)(this.position.y - 15.0F), white);
            Fonts.sora[12].drawString(matrixStack, "SCRIPT", (double)(this.position.x + 393.5F), (double)(this.position.y - 15.0F), white);
            Fonts.sora[12].drawString(matrixStack, "THEME", (double)(this.position.x + 424.5F), (double)(this.position.y - 15.0F), white);
            Fonts.sora[12].drawString(matrixStack, "CFG", (double)(this.position.x + 458.5F), (double)(this.position.y - 15.0F), white);
        }

        if (ClientUtil.me != null) {
            GlStateManager.color4f(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager.bindTexture(Render2D.downloadImage(ClientUtil.me.getAvatarUrl()));
            Render2D.drawTexture(this.position.x + 553.0F, this.position.y + size.y - 447.0F, 25.0F, 25.0F, 5.0F, 1.0F);
        } else {
            IMinecraft.mc.getTextureManager().bindTexture(new ResourceLocation("client/images/logo.png"));
            Render2D.drawTexture(this.position.x + 553.0F, this.position.y + size.y - 447.0F, 25.0F, 25.0F, 5.0F, 1.0F);
        }

        Fonts.gilroyBold[15].drawString(matrixStack, Managment.USER_PROFILE.getName(), (double)(this.position.x + 553.0F), (double)(this.position.y + size.y - 416.0F), -1);
        if (this.currentCategory == Type.Theme) {
            this.themeDrawing.draw(matrixStack, mouseX, mouseY, this.position.x + 100.0F, this.position.y, size.x - bar, size.y);
            BloomHelper.drawfps(10, 3.0F, false);
        }

        if (this.currentCategory == Type.Configs) {
            this.configDrawing.draw(matrixStack, mouseX, mouseY, this.position.x + 100.0F, this.position.y, size.x - bar, size.y + 5.0F);
            BloomHelper.drawfps(10, 2.5F, false);
        }

        if (this.currentCategory != Type.Theme && this.currentCategory != Type.Configs) {
            this.drawObjects(matrixStack, mouseX, mouseY);
        }

        scrollingOut = AnimationMath.fast(scrollingOut, scrolling, 15.0F);

        Iterator var16 = this.objects.iterator();

        while(true) {
            Object object;
            do {
                if (!var16.hasNext()) {
                    IMinecraft.mc.gameRenderer.setupOverlayRendering();
                    return;
                }

                object = (Object)var16.next();
            } while(!(object instanceof ModuleObject));

            ModuleObject m = (ModuleObject)object;
            m.bindWindow.render(matrixStack, mouseX, mouseY);
            Iterator var19 = m.object.iterator();

            while(var19.hasNext()) {
                Object object1 = (Object)var19.next();
                if (object1 instanceof BindObject) {
                    BindObject no = (BindObject)object1;
                    no.bindWindow.render(matrixStack, mouseX, mouseY);
                }
            }
        }
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (this.currentCategory == Type.Configs) {
            this.configDrawing.charTyped(codePoint);
        }

        if (codePoint != ' ') {
            if (this.configDrawing.searching && this.configDrawing.search.length() < 15) {
                ConfigDrawing var10000 = this.configDrawing;
                var10000.search = var10000.search + codePoint;
            }

            if (this.searching && this.searchText.length() < 10) {
                this.searchText = this.searchText + codePoint;
            }
        }

        return super.charTyped(codePoint, modifiers);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Iterator var4 = this.objects.iterator();

        while(var4.hasNext()) {
            ModuleObject m = (ModuleObject)var4.next();
            if (m.function.category == this.currentCategory) {
                m.keyTyped(keyCode, scanCode, modifiers);
            }
        }

        if (this.currentCategory == Type.Configs) {
            this.configDrawing.keyTyped(keyCode);
        }

        if (this.configDrawing.searching) {
            if (keyCode == 259 && !this.configDrawing.search.isEmpty()) {
                this.configDrawing.search = this.configDrawing.search.substring(0, this.configDrawing.search.length() - 1);
            }

            if (keyCode == 257) {
                this.configDrawing.searching = false;
            }
        }

        if (this.searching) {
            if (keyCode == 259 && !this.searchText.isEmpty()) {
                this.searchText = this.searchText.substring(0, this.searchText.length() - 1);
            }

            if (keyCode == 257) {
                this.searching = false;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_UP) {
            position.y -= 11;
        }

        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            position.y += 11;
        }

        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            position.x += 11;
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            position.x -= 11;
        }


        var4 = this.objects.iterator();

        while(true) {
            Object object;
            do {
                if (!var4.hasNext()) {
                    return super.keyPressed(keyCode, scanCode, modifiers);
                }

                object = (Object)var4.next();
            } while(!(object instanceof ModuleObject));

            ModuleObject m = (ModuleObject)object;
            m.bindWindow.keyPress(keyCode);
            Iterator var7 = m.object.iterator();

            while(var7.hasNext()) {
                Object object1 = (Object)var7.next();
                if (object1 instanceof BindObject) {
                    BindObject no = (BindObject)object1;
                    no.bindWindow.keyPress(keyCode);
                    if (no.bindWindow.binding && keyCode == 256) {
                        return false;
                    }
                }
            }
        }

    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int)mouseX, (int)mouseY);
        mouseX = (double)fixed.getX();
        mouseY = (double)fixed.getY();
        Iterator var7 = this.objects.iterator();

        while(true) {
            while(var7.hasNext()) {
                ModuleObject m = (ModuleObject)var7.next();
                if (!this.searching && this.searchText.isEmpty()) {
                    if (m.function.category == this.currentCategory) {
                        m.mouseReleased((int)mouseX, (int)mouseY, button);
                    }
                } else if (this.searchText.isEmpty() || m.function.name.toLowerCase().contains(this.searchText.toLowerCase())) {
                    m.mouseReleased((int)mouseX, (int)mouseY, button);
                }
            }

            var7 = this.objects.iterator();

            while(true) {
                Object object;
                do {
                    if (!var7.hasNext()) {
                        return super.mouseReleased(mouseX, mouseY, button);
                    }

                    object = (Object)var7.next();
                } while(!(object instanceof ModuleObject));

                ModuleObject m = (ModuleObject)object;
                m.bindWindow.mouseUn();
                Iterator var10 = m.object.iterator();

                while(var10.hasNext()) {
                    Object object1 = (Object)var10.next();
                    if (object1 instanceof BindObject) {
                        BindObject no = (BindObject)object1;
                        no.bindWindow.mouseUn();
                    }
                }
            }
        }
    }

    public void onClose() {
        super.onClose();
        this.searching = false;
        Iterator var1 = this.objects.iterator();

        while(var1.hasNext()) {
            ModuleObject m = (ModuleObject)var1.next();
            m.exit();
        }

    }

    public void drawObjects(MatrixStack stack, int mouseX, int mouseY) {
        Vec2i fixed = ScaleMath.getMouse(mouseX, mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        List<ModuleObject> first = this.objects.stream().filter((moduleObject) -> {
            return !this.searchText.isEmpty() || moduleObject.function.category == this.currentCategory;
        }).filter((moduleObject) -> {
            return this.objects.indexOf(moduleObject) % 2 == 0;
        }).toList();
        List<ModuleObject> second = this.objects.stream().filter((moduleObject) -> {
            return !this.searchText.isEmpty() || moduleObject.function.category == this.currentCategory;
        }).filter((moduleObject) -> {
            return this.objects.indexOf(moduleObject) % 2 != 0;
        }).toList();
        ru.levinov.util.render.RenderUtil.SmartScissor.push();
        ru.levinov.util.render.RenderUtil.SmartScissor.setFromComponentCoordinates((double)this.position.x, (double)this.position.y, (double)size.x, (double)(size.y - 1.0F));
        float offset = scrollingOut;
        float sizePanel = 0.0F;
        Iterator var9 = first.iterator();

        while(true) {
            ModuleObject object;
            do {
                if (!var9.hasNext()) {
                    float sizePanel1 = 0.0F;
                    offset = scrollingOut;
                    Iterator var15 = second.iterator();

                    while(true) {
                        do {
                            if (!var15.hasNext()) {
                                float max = Math.max(sizePanel, sizePanel1);
                                if (max < size.y) {
                                    scrolling = 0.0F;
                                } else {
                                    scrolling = MathHelper.clamp(scrolling, -(max - size.y), 0.0F);
                                }

                                ru.levinov.util.render.RenderUtil.SmartScissor.unset();
                                ru.levinov.util.render.RenderUtil.SmartScissor.pop();
                                return;
                            }

                            object = (ModuleObject)var15.next();
                        } while(!this.searchText.isEmpty() && !object.function.name.toLowerCase().contains(this.searchText.toLowerCase()));

                        object.x = this.position.x + 360.0F;
                        object.y = this.position.y + 10.0F + offset;
                        object.width = 160.0F;
                        object.height = 22.0F;
                        Iterator var18 = object.object.iterator();

                        while(var18.hasNext()) {
                            Object object1 = (Object)var18.next();
                            if (object1.setting.visible()) {
                                object.height += object1.height;
                            }
                        }

                        object.height += 3.0F;
                        if (object.y - object.height - 50.0F <= size.y) {
                            object.draw(stack, mouseX, mouseY);
                        }

                        offset += object.height += 5.0F;
                        sizePanel1 += object.height += 5.0F;
                    }
                }

                object = (ModuleObject)var9.next();
            } while(!this.searchText.isEmpty() && !object.function.name.toLowerCase().contains(this.searchText.toLowerCase()));

            object.x = this.position.x + 160.0F;
            object.y = this.position.y + 10.0F + offset;
            object.width = 160.0F;
            object.height = 25.0F;
            Iterator var11 = object.object.iterator();

            while(var11.hasNext()) {
                Object object1 = (Object)var11.next();
                if (object1.setting.visible()) {
                    object.height += object1.height;
                }
            }

            object.height += 3.0F;
            if (object.y - object.height - 50.0F <= size.y) {
                object.draw(stack, mouseX, mouseY);
            }

            offset += object.height += 5.0F;
            sizePanel += object.height += 5.0F;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int)mouseX, (int)mouseY);
        mouseX = (double)fixed.getX();
        mouseY = (double)fixed.getY();
        if (this.currentCategory == Type.Theme) {
            this.themeDrawing.click((int)mouseX, (int)mouseY, button);
        }

        if (this.currentCategory == Type.Configs) {
            this.configDrawing.click((int)mouseX, (int)mouseY, button);
        }

        float bar = 100.0F;
        float len = 0.0F;
        Type[] var9 = Type.values();
        int var10 = var9.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            Type type = var9[var11];
            len += (float)(type.ordinal() + 25);
            if (RenderUtil.isInRegion(mouseX, mouseY, this.position.x + 185.0F + len, this.position.y - 45.0F, 30.0F, 30.0F)) {
                this.currentCategory = type;
                this.searching = false;
            }
        }

        Iterator var15 = this.objects.iterator();

        while(true) {
            while(var15.hasNext()) {
                ModuleObject m = (ModuleObject)var15.next();
                if (!this.searching && this.searchText.isEmpty()) {
                    if (m.function.category == this.currentCategory) {
                        m.mouseClicked((int)mouseX, (int)mouseY, button);
                    }
                } else if (this.searchText.isEmpty() || m.function.name.toLowerCase().contains(this.searchText.toLowerCase())) {
                    m.mouseClicked((int)mouseX, (int)mouseY, button);
                }
            }

            var15 = this.objects.iterator();


            while(true) {
                Object object;
                do {
                    if (!var15.hasNext()) {
                        if (RenderUtil.isInRegion(mouseX, mouseY, this.position.x + 110.0F + 30.0F, this.position.y - 35.0F, 50.0F, 15.0F)) {
                            this.searching = !this.searching;
                        }

                        return super.mouseClicked(mouseX, mouseY, button);
                    }

                    object = (Object)var15.next();
                } while(!(object instanceof ModuleObject));

                ModuleObject m = (ModuleObject)object;
                m.bindWindow.mouseClick((int)mouseX, (int)mouseY, button);
                Iterator var19 = m.object.iterator();

                while(var19.hasNext()) {
                    Object object1 = (Object)var19.next();
                    if (object1 instanceof BindObject) {
                        BindObject no = (BindObject)object1;
                        no.bindWindow.mouseClick((int)mouseX, (int)mouseY, button);
                    }
                }
            }
        }
    }
}