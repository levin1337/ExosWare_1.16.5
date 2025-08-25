package ru.levinov.ui.dropdownGUI;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL11;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.Type;
import ru.levinov.ui.dropdownGUI.objects.ModuleObject;
import ru.levinov.ui.dropdownGUI.objects.Object;
import ru.levinov.ui.midnight.StyleManager;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.*;
import ru.levinov.util.render.animation.AnimationMath;

public class Panel {
    Type typeList;
    float x;
    float y;
    float width;
    float height;
    float scrolling;
    float scrollingOut;
    float animationProgress;
    ArrayList<ModuleObject> moduleObjects = new ArrayList();
    Type current;
    private boolean isOpen;
    int firstColor = ColorUtil.getColorStyle(360);
    int secondColor = ColorUtil.getColorStyle(90);
    int firstColor2;
    int secondColor2;

    public Panel(Type typeList, float x, float y, float width, float height) {
        this.firstColor2 = StyleManager.getCurrentStyle().getColor(180);
        this.secondColor2 = StyleManager.getCurrentStyle().getColor(360);
        this.typeList = typeList;
        this.x = x + 73.0F;
        this.y = y + 19.0F;
        this.width = width - 25.0F;
        this.height = height - 85.0F;
        this.animationProgress = 0.0F;
        this.isOpen = false;

        for(Function m2 : Managment.FUNCTION_MANAGER.getFunctions().stream().filter((m) -> m.category == typeList).toList()) {
            this.moduleObjects.add(new ModuleObject(m2));
        }

    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
        MatrixStack ms = new MatrixStack();
        this.scrollingOut = AnimationMath.fast(this.scrollingOut, this.scrolling, 15);
        GaussianBlur.startBlur();
        RenderUtil.Render2D.drawRoundedRect(this.x + 5.0F, this.y, this.width - 9.0F, this.height - 1.0F + 4.0F + 31.0F, 8.0F, ColorUtil.rgba(16, 16, 16, 180));
        GaussianBlur.endBlur(5,1);
        RenderUtil.Render2D.drawRoundedRect(this.x + 5.0F, this.y, this.width - 9.0F, this.height - 1.0F + 4.0F + 31.0F, 8.0F, ColorUtil.rgba(16, 16, 16, 180));
        Fonts.msBold[18].drawCenteredString(ms, typeList.name(), (double)(this.x + this.width / 2.0F), (double)(this.y + 7.0F), this.firstColor2);
        Fonts.icons1[23].drawCenteredString(ms, typeList.image, (double)(this.x + this.width / 2.0F - 35), (double)(this.y + 7.0F), this.firstColor2);

        float offset = -4.0F;
        float off = 11.0F;

        float originalWidth = this.width - 1.0F;
        float originalHeight = 15.0F;
        Iterator max2 = moduleObjects.iterator();
        for (ModuleObject m : moduleObjects) {
            if (Window.searching) {
                String moduleName = m.module.name.toLowerCase();
                String moduleName2 = m.module.keywords.toLowerCase();
                if (!moduleName.contains(Window.searchText.toLowerCase()) && !moduleName2.contains(Window.searchText.toLowerCase())) {
                    continue;
                }
                if (!max2.hasNext()) {
                    float max22 = off - 37.0F;
                    scrolling = max22 < this.height - 6.0F ? 0.0F : MathHelper.clamp(this.scrolling, -(max22 - (this.height - 16.0F)), 0.0F);
                    return;
                }
            }
            SmartScissor.push();
            SmartScissor.setFromComponentCoordinates((int)this.x, (int)this.y + 18, (int)this.width, (int)this.height + 12);
            m.width = originalWidth;
            m.height = originalHeight;
            m.x = this.x + 1.0F;
            m.y = this.y + off + offset + this.scrollingOut + 12.5F;
            float totalHeight = 0.0F;

            for(ru.levinov.ui.dropdownGUI.objects.Object object1 : m.object) {
                if (object1.setting != null && object1.setting.visible()) {
                    totalHeight += object1.height;
                }
            }

            float moduleHeight = m.module.expanded ? Math.max(m.height, totalHeight) : m.height;
            float nextModuleY = m.y + moduleHeight + 21.0F;
            if (this.moduleObjects.indexOf(m) < this.moduleObjects.size() - 1) {
                ModuleObject nextModule = (ModuleObject)this.moduleObjects.get(this.moduleObjects.indexOf(m) + 1);
                float nextModuleYEnd = nextModule.y + 20.0F;
                float spaceBetweenModules = nextModuleYEnd - nextModuleY;
                if (spaceBetweenModules > 0.0F) {
                    moduleHeight += spaceBetweenModules;
                }
            } else if (m.module.expanded) {
                float remainingSpace = this.y + this.height - (m.y + moduleHeight + 21.0F);
                moduleHeight += remainingSpace + 51.0F;
            }

            if (Window.searching) {
                if (m.module.expanded) {
                    if (m.module.state) {
                        RenderUtil.Render2D.drawRoundedRect(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, moduleHeight, 3.0F, (new Color(101, 101, 101, 26)).getRGB());
                        RenderUtil.Render2D.drawRoundOutline(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, moduleHeight, 3.0F, 0.0F, ColorUtil.rgba(25, 26, 33, 0), new Vector4i(ColorUtil.getColorStyle(0), (new Color(73, 73, 73, 180)).getRGB(), (new Color(73, 73, 73, 180)).getRGB(), ColorUtil.getColorStyle(30)));
                    } else {
                        RenderUtil.Render2D.drawRoundedRect(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, m.height + totalHeight * 1.6F, 3.0F, (new Color(58, 58, 58, 26)).getRGB());
                        RenderUtil.Render2D.drawRoundOutline(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, m.height + totalHeight * 1.6F, 3.0F, 0.0F, ColorUtil.rgba(25, 26, 33, 0), new Vector4i((new Color(48, 48, 48, 128)).getRGB(), (new Color(48, 48, 48, 128)).getRGB(), (new Color(48, 48, 48, 128)).getRGB(), (new Color(48, 48, 48, 128)).getRGB()));
                    }
                } else if (m.module.state) {
                    RenderUtil.Render2D.drawRoundedRect(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, moduleHeight, 3.0F, (new Color(101, 101, 101, 26)).getRGB());
                    RenderUtil.Render2D.drawRoundOutline(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, moduleHeight, 3.0F, 0.0F, ColorUtil.rgba(25, 26, 33, 0), new Vector4i(ColorUtil.getColorStyle(0), (new Color(73, 73, 73, 180)).getRGB(), (new Color(73, 73, 73, 180)).getRGB(), ColorUtil.getColorStyle(30)));
                } else {
                    RenderUtil.Render2D.drawRoundedRect(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, m.height, 3.0F, (new Color(58, 58, 58, 26)).getRGB());
                    RenderUtil.Render2D.drawRoundOutline(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, m.height, 3.0F, 0.0F, ColorUtil.rgba(25, 26, 33, 0), new Vector4i((new Color(48, 48, 48, 128)).getRGB(), (new Color(48, 48, 48, 128)).getRGB(), (new Color(48, 48, 48, 128)).getRGB(), (new Color(48, 48, 48, 128)).getRGB()));
                }
            } else if (m.module.state) {
                RenderUtil.Render2D.drawRoundedRect(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, moduleHeight, 3.0F, (new Color(101, 101, 101, 26)).getRGB());
                RenderUtil.Render2D.drawRoundOutline(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, moduleHeight, 3.0F, 0.0F, ColorUtil.rgba(25, 26, 33, 0), new Vector4i(ColorUtil.getColorStyle(0), (new Color(73, 73, 73, 180)).getRGB(), (new Color(73, 73, 73, 180)).getRGB(), ColorUtil.getColorStyle(30)));
            } else {
                RenderUtil.Render2D.drawRoundedRect(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, moduleHeight, 3.0F, (new Color(58, 58, 58, 26)).getRGB());
                RenderUtil.Render2D.drawRoundOutline(m.x + 7.5F, m.y, m.width - 20.0F + 5.0F, moduleHeight, 3.0F, 0.0F, ColorUtil.rgba(25, 26, 33, 0), new Vector4i((new Color(48, 48, 48, 160)).getRGB(), (new Color(48, 48, 48, 160)).getRGB(), (new Color(48, 48, 48, 160)).getRGB(), (new Color(48, 48, 48, 160)).getRGB()));
            }

            java.lang.Object name = m.module.name;
            if (m.isBinding) {
                name = "Бинд клавиши...";
            }

            if (m.module.state) {
                Fonts.durman[14].drawString(ms, ClientUtil.gradient((String) name,ColorUtil.getColorStyle(360),ColorUtil.getColorStyle(90)), (double)(this.x + 13.0F), (double)(this.y + off + offset + this.scrollingOut + 18.6F), (new Color(255, 255, 255, 255)).getRGB());
            } else {
                Fonts.durman[14].drawString(ms, (String)name, (double)(this.x + 13.0F), (double)(this.y + off + offset + this.scrollingOut + 18.6F), ColorUtil.setAlpha((new Color(140, 140, 140, 128)).getRGB(), 255));
            }

            float size = 10.0F;
            m.expand_anim = AnimationMath.fast(m.expand_anim, m.module.expanded ? 1.0F : 0.0F, 18.0F);
            GL11.glPushMatrix();
            new MatrixStack();
            GL11.glTranslatef(-5.0F, 0.0F, 0.0F);
            if (m.module.state) {
                if (!m.module.settingList.isEmpty()) {
                    Fonts.durman[16].drawCenteredString(ms, ">", (double)(m.x + m.width - size + 2), (double)(m.y + 4.5F), this.firstColor2);
                }
            } else if (!m.module.settingList.isEmpty()) {
                    Fonts.durman[16].drawCenteredString(ms, ">", (double)(m.x + m.width - size + 2), (double)(m.y + 4.5F), ColorUtil.setAlpha((new Color(140, 140, 140, 128)).getRGB(), 255));
            }
            GL11.glPopMatrix();
            float yd = 6.0F;

            for(Object object1 : m.object) {
                object1.x = this.x;
                object1.y = this.y + yd + off + offset + this.scrollingOut + 25.0F;
                object1.width = this.width;
                object1.height = 10.0F;
                if (object1.setting != null && object1.setting.visible()) {
                    if ((double)m.expand_anim > (double)0.5F) {
                        object1.draw(ms, mouseX, mouseY);
                    }
                    off += (object1.height + 9.5F) * m.expand_anim;
                }
            }

            off += offset + 20.0F;
            SmartScissor.pop();
            StencilUtil.uninitStencilBuffer();
        }
    }

    public static void search(MatrixStack matrixStack) {
        RenderUtil.Render2D.drawRoundedRect((float)(IMinecraft.sr.scaledWidth() / 2 - 55), (float)IMinecraft.sr.scaledHeight() / 1.17F, 120.0F, 20.0F, 3.0F, ColorUtil.rgba(16, 16, 16, 200));
        Fonts.icons1[14].drawCenteredString(matrixStack, "I", (double)((float)(IMinecraft.sr.scaledWidth() / 2) - 45.0F), (double)((float)IMinecraft.sr.scaledHeight() / 1.17F + 9.5F), ColorUtil.rgba(200, 200, 200, 200));
    }

    public void onClick(double mouseX, double mouseY, int button) {
        Vec2i mo = ScaleMath.getMouse((int)mouseX, (int)mouseY);
        mouseX = (double)mo.getX();
        mouseY = (double)mo.getY();
        int zoneX = (int)this.x;
        int zoneY = (int)this.y + 18;
        int zoneWidth = (int)this.width;
        int zoneHeight = (int)this.height + 12;
        //Обработка открытия и кликов включени функции в чите
        if (RenderUtil.isInRegion(mouseX, mouseY, zoneX, zoneY, zoneWidth, zoneHeight)) {
            float offset = -4.0F;
            float off = 11.0F;
            for(ModuleObject m : this.moduleObjects) {
                m.mouseClicked((int)mouseX, (int)mouseY, button);
                if (RenderUtil.isInRegion(mouseX, mouseY, m.x + 8.0F, m.y, m.width - 20.0F + 4.0F, m.height) && button == 1) {
                    m.module.expanded = !m.module.expanded;
                    if (m.module.expanded && !isOpen) {
                        isOpen = true;
                    } else if (!m.module.expanded && isOpen) {
                        isOpen = false;
                    }
                }

                if (m.module.expanded) {
                    float yd = 5.0F;

                    for(ru.levinov.ui.dropdownGUI.objects.Object object1 : m.object) {
                        if (object1.setting != null && object1.setting.visible()) {
                            object1.y = this.y + yd + off + offset + this.scrollingOut + 25.0F;
                            off += object1.height + 5.0F;
                        }
                    }
                }
                off += offset + 20.0F;
            }
        }

    }

    public void onScroll(double mouseX, double mouseY, double delta) {
        Vec2i m = ScaleMath.getMouse((int)mouseX, (int)mouseY);
        if (RenderUtil.isInRegion((double)m.getX(), (double)m.getY(), this.x, this.y, this.width, this.height + 32.0F)) {
           scrolling += (float)(delta * (double)25.0F);
        }
    }

    public void onRelease(double mouseX, double mouseY, int button) {
        Vec2i mo = ScaleMath.getMouse((int)mouseX, (int)mouseY);
        mouseX = (double)mo.getX();
        mouseY = (double)mo.getY();
        for(ModuleObject m : moduleObjects) {
            for(Object o : m.object) {
                o.mouseReleased((int)mouseX, (int)mouseY, button);
            }
        }
    }
    public void onKey(int keyCode, int scanCode, int modifiers) {
        for(ModuleObject m : this.moduleObjects) {
            m.keyTyped(keyCode, scanCode, modifiers);
        }
    }
}
