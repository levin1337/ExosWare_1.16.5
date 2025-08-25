package ru.levinov.ui.Spotify;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import ru.levinov.managment.Managment;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.misc.AudioUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.GaussianBlur;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;

public class Spotify extends Screen {
    public Spotify(ITextComponent titleIn) {
        super(titleIn);
    }
    public boolean openedAdd;


    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    int hud_color = (new Color(0, 0, 0, 90)).getRGB();
    int back_color = (new Color(0, 0, 0, 130)).getRGB();
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (Managment.FUNCTION_MANAGER.clickGui.blur.get()) {
            GaussianBlur.startBlur();
            RenderUtil.Render2D.drawRect(0, 0, width, height, -1);
            GaussianBlur.endBlur(Managment.FUNCTION_MANAGER.clickGui.blurVal.getValue().floatValue(), 1);
        }

        float width = 318 / 2F;
        float heigth = 339 / 2F;
        float x = this.width / 2f - width / 2f;
        float y = this.height / 2f - heigth / 2f;

        RenderUtil.Render2D.drawRoundedRect(x,y,width,heigth, 4, ColorUtil.rgba(25,25,25, 150));
        RenderUtil.Render2D.drawRect(x,y + 15,width,1, ColorUtil.getColorStyle((100 / heigth) * 20));


        Fonts.msSemiBold[16].drawString(matrixStack, "MiniBots | Beta",x + 10,y + 5, -1);
        RenderUtil.Render2D.drawImage(new ResourceLocation("client/images/cross.png"), x + width - 12, y + 5, 5, 5, -1);
        RenderUtil.Render2D.drawRoundedRect(x + 15, y + heigth - (27 / 2f) - 7, 114 / 2f, 27 / 2f, 2.5F, ColorUtil.rgba(0,0,0,128));
        Fonts.msSemiBold[14].drawCenteredString(matrixStack, "Menu", x + 15 + (114 / 2f) / 2f, y + heigth - (27 / 2f) - 2.5F, -1);
        RenderUtil.Render2D.drawRoundedRect(x + width - (114 / 2f) - 15, y + heigth - (27 / 2f) - 7, 114 / 2f, 27 / 2f, 2.5F, ColorUtil.rgba(0,0,0,128));
        Fonts.msSemiBold[14].drawCenteredString(matrixStack, "Delete", x + width - (114 / 2f) - 15 + (114 / 2f) / 2f, y + heigth - (27 / 2f) - 2.5F, -1);



        if (openedAdd) {


            RenderUtil.Render2D.drawRoundedRect(x + 10, y + 20, 114 / 2f, 27 / 2f, 3F, ColorUtil.rgba(0,0,0,128));
            Fonts.msSemiBold[14].drawCenteredString(matrixStack, "Server", x + 40, y + 25, -1);



            RenderUtil.Render2D.drawRoundedRect(x + 90, y + 20, 114 / 2f, 27 / 2f, 3F, ColorUtil.rgba(0,0,0,128));
            Fonts.msSemiBold[14].drawCenteredString(matrixStack, "NickName", x + 120, y + 25, -1);



            RenderUtil.Render2D.drawRoundedRect(x + 10, y + 120, 120 / 2f, 27 / 2f, 3F, ColorUtil.rgba(0,0,0,128));
            Fonts.msSemiBold[14].drawCenteredString(matrixStack, "RandomName", x + 40, y + 125, -1);



            RenderUtil.Render2D.drawRoundedRect(x + 90, y + 120, 120 / 2f, 27 / 2f, 3F, ColorUtil.rgba(0,0,0,128));
            Fonts.msSemiBold[14].drawCenteredString(matrixStack, "KickAll", x + 120, y + 125, -1);



        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float width = 318 / 2F;
        float heigth = 339 / 2F;
        float x = this.width / 2f - width / 2f;
        float y = this.height / 2f - heigth / 2f;

        //CLOSE
        if (RenderUtil.isInRegion(mouseX,mouseY, x + width - 12, y + 5, 5, 5)) {
            Minecraft.getInstance().player.closeScreen();
        }
        //add
        if (RenderUtil.isInRegion(mouseX,mouseY, x + 15, y + heigth - (27 / 2f) - 7, 114 / 2f, 27 / 2f)) {
            openedAdd = !openedAdd;
        }
        //Server
        if (RenderUtil.isInRegion(mouseX,mouseY, x + 10, y + 20, 114 / 2f, 27 / 2f)) {
            if (openedAdd) {
                IMinecraft.mc.displayGuiScreen(new MultiplayerScreen(this));
            }
        }
        //NickName
        if (RenderUtil.isInRegion(mouseX,mouseY, x + 90, y + 20, 114 / 2f, 27 / 2f)) {
            if (openedAdd) {
                IMinecraft.mc.displayGuiScreen(Managment.ALT);
            }
        }

        if (RenderUtil.isInRegion(mouseX,mouseY, x + 10, y + 120, 120 / 2f, 27 / 2f)) {
            if (openedAdd) {
                Minecraft.getInstance().player.sendChatMessage(".l rand");
            }
        }
        if (RenderUtil.isInRegion(mouseX,mouseY, x + 90, y + 120, 120 / 2f, 27 / 2f)) {
            if (openedAdd) {
                Minecraft.getInstance().player.connection.getNetworkManager().closeChannel(ClientUtil.gradient("KickALL", new Color(121, 208, 255).getRGB(), new Color(96, 133, 255).getRGB()));
            }
        }


        return super.mouseClicked(mouseX, mouseY, button);
    }
}
