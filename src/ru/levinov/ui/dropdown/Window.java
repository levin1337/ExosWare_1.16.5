package ru.levinov.ui.dropdown;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.Type;
import ru.levinov.ui.dropdown.objects.ModuleObject;
import ru.levinov.ui.dropdown.objects.Object;
import ru.levinov.ui.dropdown.theme.ThemeDrawing;
import ru.levinov.util.animations.Animation;
import ru.levinov.util.animations.Direction;
import ru.levinov.util.animations.impl.EaseBackIn;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.*;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ru.levinov.util.IMinecraft.mc;

public class Window extends Screen {

    private Vector2f position = new Vector2f(0, 0);

    public static Vector2f size = new Vector2f(500, 400);

    public static int dark = new Color(18, 19, 25).getRGB();
    public static int medium = new Color(18, 19, 25).brighter().getRGB();
    public static int light = new Color(129, 134, 153).getRGB();
    private final ThemeDrawing themeDrawing = new ThemeDrawing();
    private Type currentCategory;


    public static ArrayList<ModuleObject> objects = new ArrayList<>();

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
//        scrolling += (float) (delta * 30);
        for(Panel p:panels){
            p.onScroll(mouseX,mouseY,delta);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public Window(ITextComponent titleIn) {
        super(titleIn);
        scrolling = 0;
        for (Function function : Managment.FUNCTION_MANAGER.getFunctions()) {
            objects.add(new ModuleObject(function));
        }
        size = new Vector2f(450, 350);
        position = new Vector2f(mc.getMainWindow().scaledWidth() / 2f, mc.getMainWindow().scaledHeight() / 2f);
        float offset = 150;
        float width = 120;
        for (Type type : new Type[] {Type.Combat, Type.Movement,Type.Player ,Type.Render,Type.Util,Type.Misc}) {
            panels.add(new Panel(type, (mc.getMainWindow().scaledWidth() / 2f) + offset, mc.getMainWindow().scaledHeight() / 2f, width, 300));
            offset += width + 5;
        }

    }

    ArrayList<Panel> panels = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        panels.clear();
        size = new Vector2f(450, 350);
        float offset = 150;
        float width = 120;
        float height = 300;
        position = new Vector2f(mc.getMainWindow().scaledWidth() / 2f-(Type.values().length*width)/2f, (mc.getMainWindow().scaledHeight() / 2f)-height/2f);
        for (Type type : new Type[] {Type.Combat, Type.Movement,Type.Player ,Type.Render,Type.Util,Type.Misc}) {
            panels.add(new Panel(type,position.x+offset, position.y, width,height));
            offset+=width+5;
        }
    }

    public static float scrolling;
    public static float scrollingOut;

    public boolean searching;
    public static String searchText = "";
    public static boolean openAnimation=false;
    //    public static float animation;
    public Animation animation = new EaseBackIn(400, 1, 1.5f);

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    boolean opened;


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        GL11.glPushMatrix();
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        float bar = 100;
        mc.gameRenderer.setupOverlayRendering(2);

        if(openAnimation)animation.setDirection(Direction.FORWARDS);
        else animation.setDirection(Direction.BACKWARDS);
        GL11.glPushMatrix();

        for(Panel p:panels){
            p.render(mouseX,mouseY);
        }
        GL11.glPopMatrix();
        if(animation.getOutput()<0.1f&&!openAnimation){

            openAnimation = true;
        }
        // TODO: ÒÈÏÀ ÎÍÎ ÁÓÄÅÒ ÐÅÍÄÅÐÈÒÜ ÒÅÌÛ ÍÎ ÍÅ Â ÑÂÎÅÉ ÊÀÒÅÃÎÐÈÈ À ÂÅÇÄÅ ÄÀ ÅÑÒ ÆÅ
        // TODO:À ×ÒÎÁÛ Â ÑÂÎÅÉ ÍÀÄÎ ÒÈÏÀ         if (currentCategory == Type.Configs) { òóòà ðåíäåð}  ñäåëàòü äà

        //ÒÅÌÊÈ
        MatrixStack ms2 = new MatrixStack();
        RenderUtil.Render2D.drawRoundedCorner(440,100,45,15,5,-1);
        Fonts.durman[18].drawString(ms2,"Themes",442,104,Color.black.getRGB());

        if (opened) {
            themeDrawing.draw(matrixStack, mouseX, mouseY, position.x - 400, position.y, 5, 5);
        } else {

        }


        if (RenderUtil.isInRegion(mouseX,mouseY,440,100,45,15)) {
            opened = !opened;
        }



        scrollingOut = AnimationMath.fast(scrollingOut, scrolling, 15);
        StencilUtil.initStencilToWrite();
        RenderUtil.Render2D.drawRoundedCorner(position.x, position.y, size.x, size.y, new Vector4f(8.5f, 8.5f, 8.5f, 8.5f), -1);
        StencilUtil.readStencilBuffer(0);
        StencilUtil.uninitStencilBuffer();
        mc.gameRenderer.setupOverlayRendering();
        GL11.glPopMatrix();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searching && searchText.length() < 13) {
            searchText += codePoint;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for(Panel p:panels){
            p.onKey(keyCode,scanCode,modifiers);
        }
        if(keyCode == 256){
            mc.displayGuiScreen(null);
            openAnimation = false;

        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(Panel p:panels){
            p.onRelease(mouseX,mouseY,button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        super.onClose();
        searching = false;
        openAnimation=false;
        for (ModuleObject m : objects) {
            m.exit();
            m.function.expanded = false;
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        //TODO:  À ÒÓÒÀ ÌÛ ÒÈÏÀ ÑÄÅËÀËÈ ×ÒÎÁÛ ÎÍÎ ÒÀÊÆÅ ÂÈÇÄÅ ÐÎÁÎÒÀËÎ ÄÀ       À ÝÒÎ ÄËß ÒÎÃÎ ×ÒÎÁÛ ÐÎÁÎÒÀËÎ ÒÎËÜÊÎ Â ÒÅÌÅ ÄÀ if (currentCategory == Type.Theme) {}
        themeDrawing.click((int) mouseX, (int) mouseY, button);

        float bar = 100;

        float len = 0;
        for (Type type : Type.values()) {
            len = type.ordinal() * 20;

            if (RenderUtil.isInRegion(mouseX, mouseY, position.x + 5f, position.y + 55 + len, bar - 10, 15)) {
                currentCategory = type;
                searching = false;
            }
        }

        for (ModuleObject m : objects) {
            if (searching || !searchText.isEmpty()) {
                if (!searchText.isEmpty())
                    if (!m.function.name.toLowerCase().contains(searchText.toLowerCase())) continue;
                m.mouseClicked((int) mouseX, (int) mouseY, button);
            } else {
                if (m.function.category == currentCategory)
                    m.mouseClicked((int) mouseX, (int) mouseY, button);
            }

        }




        for(Panel p:panels){
            p.onClick(mouseX,mouseY,button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
