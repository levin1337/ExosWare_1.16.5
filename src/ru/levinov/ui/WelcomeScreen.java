package ru.levinov.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import ru.levinov.util.animations.Direction;
import ru.levinov.util.animations.impl.EaseBackIn;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;

import java.awt.*;

public class WelcomeScreen extends Screen {

    public EaseBackIn animation = new EaseBackIn(1250, 1, 2F, Direction.BACKWARDS);

    public WelcomeScreen(ITextComponent titleIn) {
        super(titleIn);
        animation.setDirection(Direction.FORWARDS);
    }


    int hud_color = (new Color(0, 0, 0, 90)).getRGB();
    final int b_color = new Color(0, 0, 0, 150).getRGB();
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderUtil.Render2D.drawRect(0,0,width,height, hud_color);
        RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/logo2.png"), 435, 120f, 85, 85, -1);
        RenderUtil.Render2D.drawRoundedCorner(380,250,200,180,10,b_color);


        Fonts.msBold[20].drawCenteredString(matrixStack, "Добро пожаловать ", width / 2f, height / 2f - 5, -1);
        Fonts.msBold[20].drawCenteredString(matrixStack, "в ЕхоsWarе client", width / 2f, height / 2f + 15, -1);

        Fonts.msBold[17].drawCenteredString(matrixStack, "Помощь по клиенту - .help", width / 2f, height / 2f + 60, -1);


        Fonts.msBold[16].drawCenteredString(matrixStack, "YT - https://youtube.com/@levinov1337", width / 2f, height / 2f + 110, ColorUtil.getColorStyle(90));
        Fonts.msBold[16].drawCenteredString(matrixStack, "TG - https://t.me/exosware", width / 2f, height / 2f + 120, ColorUtil.getColorStyle(90));
        Fonts.msBold[16].drawCenteredString(matrixStack, "VK - https://vk.com/exoswareclient", width / 2f, height / 2f + 130, ColorUtil.getColorStyle(90));
    }
}
