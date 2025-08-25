package ru.levinov.ui.alt;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector4i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import ru.levinov.managment.Managment;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.*;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.levinov.util.IMinecraft.mc;

public class AltManager extends Screen {


    public AltManager() {
        super(new StringTextComponent(""));

    }
    int width = Minecraft.getInstance().getMainWindow().getWidth();
    int height = Minecraft.getInstance().getMainWindow().getHeight();
    private TextFieldWidget proxy;
    public ArrayList<Account> accounts = new ArrayList<>();


    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            accounts.add(new Account(proxy.getText()));
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        if (RenderUtil.isInRegion(mouseX, mouseY, 345 / 2f, 664 / 2f, 249 / 2f, 46 / 2f)) {
            Set<Integer> digits = new HashSet<>();
            Random random = new Random();
            while (digits.size() < 4) {
                int digit = random.nextInt(10);
                digits.add(digit);
            }
            StringBuilder digitString = new StringBuilder();
            for (int digit : digits) {
                digitString.append(digit);
            }
            String accountName = "exosware" + digitString.toString();
            accounts.add(new Account(accountName));
            AltConfig.updateFile();
        }
        if (RenderUtil.isInRegion(mouseX, mouseY, 345 / 2f, 723 / 2f, 249 / 2f, 46 / 2f)) {
                accounts.add(new Account(proxy.getText()));
                AltConfig.updateFile();
        }


        float altX = 778 / 2f, altY = 298 / 2f;
        float iter = scrollAn;
        Iterator<Account> iterator = accounts.iterator();
        while (iterator.hasNext()) {
            Account account = iterator.next();
            float panWidth = 197 / 2f;
            float acX = altX + 15 + (iter * (panWidth + 10));

            if (RenderUtil.isInRegion(mouseX, mouseY, acX, 442 / 2f, panWidth, 261 / 2f)) {
                if (button == 0) {
                    mc.session = new Session(account.accountName, "", "", "mojang");
                } else {
                    iterator.remove(); // Безопасное удаление элемента
                    AltConfig.updateFile();
                }
            }

            iter++;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);


        proxy = new TextFieldWidget(this.font, height / 4, width / 5, 212, 20, new TranslationTextComponent("Ваш прокси"));
        proxy.setMaxStringLength(16);
        children.add(proxy);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public float scroll;
    public float scrollAn;

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scroll += delta * 1;
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    int hud_color = (new Color(0, 0, 0, 120)).getRGB();
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderBackground(matrixStack);
        proxy.render(matrixStack, mouseX, mouseY, partialTicks);
        scrollAn = AnimationMath.lerp(scrollAn, scroll, 5);
        mc.gameRenderer.setupOverlayRendering(2);

        float x = (220 / 2f), y = (327 / 2f);

        Fonts.durman[16].drawCenteredString(matrixStack, "Вы успешно зашли под никнеймом: " + mc.getSession().getUsername(), x + ((502 / 2f) / 2f), y + 481 / 2f + 10, ColorUtil.rgba(142, 145, 157, 255));
        Fonts.msSemiBold[20].drawString(matrixStack, "Менеджер аккаунтов", x + 21, y + 15, -1);

        RenderUtil.Render2D.drawRoundedRect(height / 3f + 10, width / 2.57f, 249 / 2f, 46 / 2f, 4, hud_color);
        RenderUtil.Render2D.drawRoundedRect(height / 3f + 10, width / 2.37f, 249 / 2f, 46 / 2f, 4, hud_color);

        Fonts.msSemiBold[19].drawCenteredString(matrixStack, "RandomName", height / 2.05f, width / 2.51f, -1);
        Fonts.msSemiBold[19].drawCenteredString(matrixStack, "Join Account", height / 2.05f, width / 2.32f, -1);



        float altX = 778 / 2f, altY = 298 / 2f;
        RenderUtil.Render2D.drawRoundedRect(778 / 2f, -10, width / 1.85f, height + 100, 5, hud_color);


        Fonts.msSemiBold[20].drawString(matrixStack, "Список аккаунтов", altX + 15, altY + 15, -1);
        float iter = scrollAn;
        float size = 0;

        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(778 / 2f, 298 / 2f, 923 / 2f, 539 / 2f);
        for (Account account : accounts) {
            float panelWidth = 197 / 2f;
            float accountX = altX + 15 + (iter * (panelWidth + 10));
            float accountY = 442 / 2f;

            if (account.accountName.equalsIgnoreCase(mc.session.getUsername())) {
                RenderUtil.Render2D.drawShadowyestfps(accountX, accountY, panelWidth, 261 / 2f, 25, ColorUtil.getColorStyle(90));
            }

            SmartScissor.push();
            SmartScissor.setFromComponentCoordinates(accountX + 5, accountY, panelWidth - 8, 261 / 2f);

            RenderUtil.Render2D.drawRoundedRect(accountX, accountY, panelWidth, 261 / 2f, 8, Color.gray.getRGB());

            Date dateAdded = new Date(account.dateAdded);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(dateAdded);

            Fonts.msSemiBold[16].drawCenteredString(matrixStack, account.accountName, accountX + panelWidth / 2f, 629 / 2f, -1);

            Fonts.msSemiBold[14].drawCenteredString(matrixStack, formattedDate, accountX + panelWidth / 2f, 629 / 2f + 13, -1);

            SmartScissor.unset();
            SmartScissor.pop();

            float skinX = accountX + 28;
            float skinY = accountY + 10;
            RenderUtil.Render2D.drawRoundedRect(skinX, skinY, 85 / 2f, 85 / 2f, 1, Color.BLACK.getRGB());

            mc.getTextureManager().bindTexture(account.skin);
            AbstractGui.drawScaledCustomSizeModalRect(skinX, skinY, 8F, 8F, 8F, 8F, 85 / 2f, 85 / 2f, 64, 64);

            iter++;
            size++;
        }
        scroll = MathHelper.clamp(scroll, size > 4 ? -size + 4 : 0, 0);
        SmartScissor.unset();
        SmartScissor.pop();
        mc.gameRenderer.setupOverlayRendering();

        Fonts.msSemiBold[16].drawCenteredString(matrixStack, "[Enter] - Войти в аккаунт", height / 2.05f, width / 3.27f, -1);
        Fonts.msSemiBold[16].drawCenteredString(matrixStack, "[ПКМ] - Удалить аккаунт", height / 2.05f, width / 3.13f, -1);
        Fonts.msSemiBold[16].drawCenteredString(matrixStack, "[ЛКМ] - Войти в аккаунт в листе", height / 2.05f, width / 3f, -1);
    }
}
