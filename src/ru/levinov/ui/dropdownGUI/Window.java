package ru.levinov.ui.dropdownGUI;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.Type;
import ru.levinov.ui.dropdownGUI.objects.ModuleObject;
import ru.levinov.util.animations.Animation;
import ru.levinov.util.animations.Direction;
import ru.levinov.util.animations.impl.EaseBackIn;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.font.styled.StyledFont;
import ru.levinov.util.render.*;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;
import java.util.ArrayList;

import static ru.levinov.util.IMinecraft.mc;
import static ru.levinov.util.IMinecraft.sr;


public class Window extends Screen {

    private Vector2f position = new Vector2f(0, 0);

    public static Vector2f size = new Vector2f(500, 400);

    public static int dark = new Color(18, 19, 25).getRGB();
    public static int medium = new Color(18, 19, 25).brighter().getRGB();
    public static int light = new Color(129, 134, 153).getRGB();
    private Type currentCategory;

    public static ArrayList<ModuleObject> objects = new ArrayList<>();

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for(Panel p:panels){
            p.onScroll(mouseX,mouseY,delta);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public Window(ITextComponent titleIn) {
        super(titleIn);
        scrolling = 0;
        for (Function module : Managment.FUNCTION_MANAGER.getFunctions()) {
            objects.add(new ModuleObject(module));
        }
        size = new Vector2f(450, 350);
        position = new Vector2f(mc.getMainWindow().scaledWidth() / 2f, mc.getMainWindow().scaledHeight() / 2f);
        float offset = 0;
        float width = 120;
        for (Type typeList : new Type[] {Type.Combat, Type.Movement,Type.Player ,Type.Render,Type.Util,Type.Misc}) {
            panels.add(new Panel(typeList,(mc.getMainWindow().scaledWidth() / 2f)+offset, mc.getMainWindow().scaledHeight() / 2f,width,300));
            offset+=width+3;
        }
    }

    ArrayList<Panel> panels = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        panels.clear();
        size = new Vector2f(450, 350);
        float offset = 0;
        float width = 150;
        float height = 350;
        position = new Vector2f(mc.getMainWindow().scaledWidth() / 2f - 450, (mc.getMainWindow().scaledHeight() / 2f)-height/2f);
        for (Type typeList : new Type[] {Type.Combat, Type.Movement,Type.Player ,Type.Render,Type.Util,Type.Misc}) {
            panels.add(new Panel(typeList,position.x + offset, position.y, width,height));
            offset+=width-20;
        }
    }

    public static float scrolling;
    public static float scrollingOut;

    public static boolean searching;
    public static String searchText = "";
    public static boolean openAnimation=false;
    public Animation animation = new EaseBackIn(400, 1, 1.5f);

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        MatrixStack ms = new MatrixStack();

        GL11.glPushMatrix();
        mc.gameRenderer.setupOverlayRendering(2);

        Vec2i fixed = ScaleMath.getMouse(mouseX, mouseY);
        int scaledMouseX = fixed.getX();
        int scaledMouseY = fixed.getY();

        if (openAnimation) {
            animation.setDirection(Direction.FORWARDS);
        } else {
            animation.setDirection(Direction.BACKWARDS);
        }

        for (Panel p : panels) {
            p.render(matrixStack, scaledMouseX, scaledMouseY);
        }
        Panel.search(matrixStack);
        StyledFont newcode = Fonts.durman[16];

        if (!searching && searchText.isEmpty()) {
            newcode.drawCenteredString(ms, "Поиск...", sr.scaledWidth() / 2 + 4, sr.scaledHeight() / 1.17f + 8, ColorUtil.rgba(200, 200, 200, 200));
        } else {
            newcode.drawString(ms, searchText + (searching ? (System.currentTimeMillis() % 1000 > 500 ? "_" : "") : ""), sr.scaledWidth() / 2 - 30f, sr.scaledHeight() / 1.17f + 8, ColorUtil.rgba(200, 200, 200, 200));
        }
        scrollingOut = AnimationMath.fast(scrollingOut, scrolling, 18);

        StencilUtil.initStencilToWrite();
        RenderUtil.Render2D.drawRoundedCorner(position.x, position.y, size.x, size.y, new Vector4f(8.5f, 8.5f, 8.5f, 8.5f), -1);
        StencilUtil.readStencilBuffer(0);
        StencilUtil.uninitStencilBuffer();

        if (animation.getOutput() < 0.1f && !openAnimation) {
            openAnimation = true;
        }
        GL11.glPopMatrix();
    }


    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searching && searchText.length() < 16) {
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

        if (keyCode == 259 && searching && !searchText.isEmpty()) {
            searchText = searchText.substring(0, searchText.length() - 1);
        }

        if (keyCode == 257) {
            // Обработка нажатия Enter для выбора функции
            if (searching && !searchText.isEmpty()) {
                for (ModuleObject m : objects) {
                    // Проверка как по имени модуля, так и по ключевым словам
                    if (m.module.name.toLowerCase().contains(searchText.toLowerCase()) ||
                            matchesKeywords(m.module, searchText)) {
                        m.mouseClicked((int) (sr.scaledWidth() / 2), (int) (sr.scaledHeight() / 1.17f), 0); // Замените на нужные координаты
                        break; // Выход из цикла после выбора первой найденной функции
                    }
                }
            }
            searchText = "";
            searching = false;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean matchesKeywords(Function module, String searchText) {
        String lowerSearchText = searchText.toLowerCase();
        // Разделяем строку keywords на отдельные слова, предполагая, что они разделены запятыми
        String[] keywordArray = module.keywords.split(","); // Измените разделитель, если нужно

        for (String keyword : keywordArray) {
            if (keyword.trim().toLowerCase().contains(lowerSearchText)) {
                return true;
            }
        }
        return false;
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
        searchText = "";
        openAnimation = false;
        for (ModuleObject m : objects) {
            m.exit();
        }
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        for (Type typeList : new Type[] {Type.Combat, Type.Movement,Type.Player ,Type.Render,Type.Util,Type.Misc}) {
            if (RenderUtil.isInRegion(mouseX, mouseY, sr.scaledWidth() / 2 - 55, sr.scaledHeight() / 1.17f, 110, 13)) {
                currentCategory = typeList;
                searching = false;
            }
        }

        for (ModuleObject m : objects) {
            if (searching || !searchText.isEmpty()) {
                if (!searchText.isEmpty() && m.module.name.toLowerCase().contains(searchText.toLowerCase())) {
                    m.mouseClicked((int) mouseX, (int) mouseY, button);
                }
            } else {
                if (m.module.category == currentCategory) {
                    m.mouseClicked((int) mouseX, (int) mouseY, button);
                }
            }
        }

        //ПОИСК НА ЭКРАНЕ ВЫБИОР
        if (RenderUtil.isInRegion(mouseX, mouseY, sr.scaledWidth() / 2 - 55, sr.scaledHeight() / 1.17f, 120, 20)) {
            searching = !searching;
        } else {

        }

        for (Panel p : panels) {
            p.onClick(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}