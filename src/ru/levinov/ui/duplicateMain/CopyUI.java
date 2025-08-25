package ru.levinov.ui.duplicateMain;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import ru.levinov.managment.Managment;
import ru.levinov.ui.proxy.ProxyConnection;
import ru.levinov.util.font.Fonts;

public class CopyUI extends Screen {


    private TextFieldWidget proxy;
    public static TextFieldWidget porthack;
    public static TextFieldWidget portservera;


    public CopyUI() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        super.init();

        float[] center = {width / 2f, height / 2f};

        this.addButton(new Button((int) center[0] + 5, (int) center[1] + 30, 100, 20, new TranslationTextComponent("Назад"), (ppp) -> Minecraft.getInstance().displayGuiScreen(new MultiplayerScreen(null))));

        this.proxy = new TextFieldWidget(this.font, (int) center[0] - 100, (int) center[1] - 10, 200, 20, new TranslationTextComponent("Ваш прокси"));
        this.proxy.setMaxStringLength(32);
        this.children.add(this.proxy);


        porthack = new TextFieldWidget(this.font, (int) center[0] - 170, (int) center[1] - 35, 60, 20, new TranslationTextComponent("Ваш порт"));
        porthack.setMaxStringLength(32);
        children.add(porthack);

        portservera = new TextFieldWidget(this.font, (int) center[0] - 170, (int) center[1] + 15, 60, 20, new TranslationTextComponent("Ваш порт"));
        portservera.setMaxStringLength(32);
        children.add(portservera);


        this.addButton(new Button((int) center[0] - 100 - 5, (int) center[1] + 30, 100, 20, new TranslationTextComponent("Готово / Сброс"), (ppp) -> {
            String targetHost = proxy.getText();
            int targetPort = Integer.parseInt(portservera.getText());
            int localPort = Integer.parseInt(porthack.getText());

            try {
                new CopyProxy(targetHost, targetPort).start(localPort);
            } catch (InterruptedException e) {
                //  throw new RuntimeException(e);
            }
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        float[] center = {width / 2f, height / 2f};


        Fonts.durman[16].drawCenteredString(matrixStack, "Айпи сервера, пример: mc.funtime.su", center[0], center[1] + 60, -1);


        Fonts.durman[16].drawCenteredString(matrixStack, "Порт сервера", center[0] - 140, center[1] + 5 , -1);

        Fonts.durman[16].drawCenteredString(matrixStack, "Порт дубликата", center[0] - 140, center[1] - 45, -1);


        proxy.render(matrixStack, mouseX, mouseY, partialTicks);
        porthack.render(matrixStack, mouseX, mouseY, partialTicks);
        portservera.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();

        proxy.tick();
    }
}









//конект мега бупасс
    /*
                String targetHost = "funtime.su";
            int targetPort = 25565;
            int localPort = 25565;

            try {
                new MinecraftProxy(targetHost, targetPort).start(localPort);
            } catch (InterruptedException e) {
              //  throw new RuntimeException(e);
            }
     */
