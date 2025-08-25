package ru.levinov.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import ru.levinov.managment.Managment;
import ru.levinov.modules.impl.util.UnHookFunction;
import ru.levinov.ui.beta.component.impl.ColorComponent;
import ru.levinov.ui.beta.component.impl.ThemeComponent;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.IMinecraft;

import java.util.Deque;
import java.util.List;

public class unHookUI extends Screen implements IMinecraft {
    public boolean textOpen;
    public UnHookFunction unHookFunction;
    private boolean message1Sent = false;
    private boolean message2Sent = false;
    private boolean message3Sent = false;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine<ITextComponent>> chatLines = Lists.newArrayList();
    private final List<ChatLine<IReorderingProcessor>> drawnChatLines = Lists.newArrayList();
    private final Deque<ITextComponent> field_238489_i_ = Queues.newArrayDeque();
;

    public unHookUI(ITextComponent titleIn) {
        super(titleIn);
        unHookFunction = Managment.FUNCTION_MANAGER.unhook;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        handleTimers();
    }

    private void handleTimers() {
        long elapsedTime = unHookFunction.timerUtil.getTime();
        if (elapsedTime >= 1000 && !message1Sent) {
            ClientUtil.sendMesage("Чит будет скрыт через 3 секунды."); // Сообщение в чате
            message1Sent = true;
        }

        if (elapsedTime >= 2000 && !message2Sent) {
            ClientUtil.sendMesage("Чит будет скрыт через 2 секунды."); // Сообщение в чате
            message2Sent = true;
        }


        if (elapsedTime >= 3000 && !message3Sent) {
            ClientUtil.sendMesage("Чит будет скрыт через 1 секунду."); // Сообщение в чате
            message3Sent = true;
        }

        if (elapsedTime >= 4000) {
            ClientUtil.legitMode = true;
            unHookFunction.onUnhook();
            NewChatGui.clearChatMessages(true);
            unHookFunction.timerUtil.reset();
            mc.displayGuiScreen(null);
       //     ClientUtil.sendMesage("Чит скрыт, вернуть на клавишу - " + unHookFunction.unHookKey.getType().name()); // Сообщение в чате

        }
    }
}
