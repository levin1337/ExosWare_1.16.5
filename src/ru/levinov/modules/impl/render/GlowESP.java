package ru.levinov.modules.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.PlayerEntity;
import ru.levinov.events.Event;
import ru.levinov.events.impl.player.EventModelRender;
import ru.levinov.events.impl.player.EventUpdate;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.OutlineUtils;
import ru.levinov.util.render.ShaderUtil;

@FunctionAnnotation(name = "GlowESP", type = Type.Render)
public class GlowESP extends Function {
    public final ModeSetting mode = new ModeSetting("ћод", "Ёффект", "Ўейдер", "Ёффект");


    public GlowESP() {
        addSettings(mode);
    }

    public static Framebuffer framebuffer = new Framebuffer(1, 1, true, false);

    @Override
    public void onEvent(Event event) {
        if (mode.is("Ўейдер")) {
            if (event instanceof EventModelRender e) {
                framebuffer.bindFramebuffer(false);
                e.render();
                framebuffer.unbindFramebuffer();
                mc.getFramebuffer().bindFramebuffer(true);
            }

            if (event instanceof EventRender e) {
                if (e.isRender2D()) {
                    GlStateManager.enableBlend();

                    OutlineUtils.registerRenderCall(() -> {
                        framebuffer.bindFramebufferTexture();
                        ShaderUtil.drawQuads();
                    });

                    BloomHelper.registerRenderCallHand(() -> {
                        framebuffer.bindFramebufferTexture();
                        ShaderUtil.drawQuads();
                    });


                    OutlineUtils.draw(1, -1);
                    BloomHelper.drawC(10, 1, true, -1, 2);
                    OutlineUtils.setupBuffer(framebuffer);

                    mc.getFramebuffer().bindFramebuffer(true);
                }
            }
        }
        if (mode.is("Ёффект")) {
            if (event instanceof EventUpdate e) {
                for (PlayerEntity player : mc.world.getPlayers())
                    if (!player.equals(mc.player)) {
                        gloweffect(player);
                    }
            }
        }
    }

    private void gloweffect(PlayerEntity player) {
        player.setGlowing(true);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!player.equals(mc.player)) {
                removeGlowEffect(player);
            }
        }
    }

    private void removeGlowEffect(PlayerEntity player) {
        player.setGlowing(false);
    }
}
