package ru.levinov.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.shader.Framebuffer;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.render.*;

@FunctionAnnotation(name = "Custom Hand", type = Type.Render)
public class CustomHand extends Function {

    public static Framebuffer handBuffer = new Framebuffer(1,1, true,false);

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {
            if (e.isRender2D() && mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                OutlineUtils.registerRenderCall(() -> {
                    handBuffer.bindFramebufferTexture();
                    ShaderUtil.drawQuads();
                });
                BloomHelper.registerRenderCall(() -> {
                    handBuffer.bindFramebufferTexture();
                    ShaderUtil.drawQuads();
                });
                BloomHelper.drawC(10, 1f, true, ColorUtil.getColorStyle(0), 3);
                OutlineUtils.draw(1, ColorUtil.getColorStyle(0));

                GaussianBlur.startBlur();
                handBuffer.bindFramebufferTexture();
                ShaderUtil.drawQuads();
                GaussianBlur.endBlur(10,3);


                GlStateManager.popMatrix();
            }
        }
    }
}
