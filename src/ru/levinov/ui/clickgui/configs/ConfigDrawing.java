/* Decompiler 67ms, total 775ms, lines 185 */
package ru.levinov.ui.clickgui.configs;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector4i;
import ru.levinov.managment.config.ConfigManager;
import ru.levinov.managment.Managment;
import ru.levinov.ui.clickgui.Window;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.OutlineUtils;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.SmartScissor;
import ru.levinov.util.render.RenderUtil.Render2D;

public class ConfigDrawing {
    public static ConfigDrawing configDrawing = new ConfigDrawing();
    public CopyOnWriteArrayList<ConfigObject> objects = new CopyOnWriteArrayList();
    public TimerUtil refresh = new TimerUtil();
    float x;
    float y;
    float width;
    float height;
    public String search = "";
    public boolean searching;

    public ConfigDrawing() {
        this.objects.clear();
        Iterator var1 = Managment.CONFIG_MANAGER.getAllConfigurations().iterator();

        while(var1.hasNext()) {
            String cfg = (String)var1.next();
            this.objects.add(new ConfigObject(cfg));
        }

        configDrawing = this;
    }

    public void draw(MatrixStack stack, int mouseX, int mouseY, float x, float y, float width, float height) {
        if (this.refresh.hasTimeElapsed(5000L)) {
            Iterator var8 = Managment.CONFIG_MANAGER.getAllConfigurations().iterator();

            while(var8.hasNext()) {
                String cfg = (String)var8.next();
                if (!this.objects.stream().map((o) -> {
                    return o.staticF;
                }).toList().contains(cfg)) {
                    this.objects.add(new ConfigObject(cfg));
                }
            }

            this.objects.removeIf((objectx) -> {
                return Managment.CONFIG_MANAGER.findConfig(objectx.staticF) == null;
            });
            this.refresh.reset();
        }

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        float offsetX = 10.0F;
        float offsetY = 50.0F + Window.scrollingOut;
        Render2D.drawRoundedCorner(x + 1.0F, y, width - 2.0F, 33.0F, new Vector4f(0.0F, 0.0F, 0.0F, 0.0F), new Vector4i(ColorUtil.rgba(63, 72, 103, 25), ColorUtil.rgba(19, 23, 39, 25), ColorUtil.rgba(63, 72, 103, 25), ColorUtil.rgba(19, 23, 39, 25)));
        OutlineUtils.registerRenderCall(() -> {
            Render2D.drawRoundedCorner(x + 1.0F, y, width - 2.0F, 33.0F, new Vector4f(0.0F, 0.0F, 0.0F, 0.0F), ColorUtil.rgba(38, 40, 59, 255));
        });
        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates((double)x, (double)(y + 33.0F), (double)width, (double)(height - 33.0F));
        float size = 0.0F;
        Iterator var11 = this.objects.iterator();

        ConfigObject object;
        while(var11.hasNext()) {
            object = (ConfigObject)var11.next();
            if (object.cfg.toLowerCase().contains(this.search.toLowerCase())) {
                object.x = x + offsetX + 70.0F;
                object.y = y + offsetY;
                object.width = 156.5F;
                object.height = 48.5F;
                Render2D.drawRoundOutline(object.x, object.y, object.width, object.height, 5.0F, 0.0F, ColorUtil.rgba(25, 26, 33, 255), new Vector4i(ColorUtil.rgba(38, 40, 59, 255), ColorUtil.rgba(38, 40, 59, 255), ColorUtil.rgba(38, 40, 59, 255), ColorUtil.rgba(38, 40, 59, 255)));
                offsetX += object.width + 16.0F;
                if (offsetX > 350.0F - object.width + 16.0F) {
                    size += 55.5F;
                    offsetX = 10.0F;
                    offsetY += 55.0F;
                }
            }
        }

        if (size < 342.0F) {
            Window.scrolling = 0.0F;
            Window.scrollingOut = 0.0F;
        } else {
            Window.scrolling = MathHelper.clamp(Window.scrolling, -(size - 350.0F), 0.0F);
        }

        var11 = this.objects.iterator();

        while(var11.hasNext()) {
            object = (ConfigObject)var11.next();
            if (object.cfg.toLowerCase().contains(this.search.toLowerCase())) {
                object.draw(stack, mouseX, mouseY);
            }
        }

        SmartScissor.unset();
        SmartScissor.pop();
        Fonts.configIcon[22].drawString(stack, "H", (double)(x + 15.0F), (double)(y + 13.0F), -1);
        Fonts.configIcon[22].drawString(stack, "L", (double)(x + 35.0F), (double)(y + 13.0F), -1);
        Render2D.drawRoundedRect(x + 150.5F + 30.0F, y + 10.0F, 150.5F, 14.5F, 4.0F, ColorUtil.rgba(0, 0, 0, 128));
        if (!this.searching && this.search.isEmpty()) {
            Fonts.msMedium[14].drawCenteredString(stack, "Поиск конфига", (double)(x + 150.5F + 30.0F + 75.25F), (double)(y + 15.0F), ColorUtil.rgba(121, 123, 134, 255));
        }

        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates((double)(x + 150.5F + 30.0F), (double)(y + 10.0F), 150.5D, 14.5D);
        if (this.searching || !this.search.isEmpty()) {
            Fonts.msMedium[15].drawString(stack, this.search + (this.searching ? (System.currentTimeMillis() % 1000L > 500L ? "" : "_") : ""), (double)(x + 150.5F + 35.0F), (double)(y + 14.0F), -1);
        }

        SmartScissor.unset();
        SmartScissor.pop();
    }

    public void click(int mouseX, int mouseY, int button) {
        Iterator var4 = this.objects.iterator();

        while(var4.hasNext()) {
            ConfigObject object = (ConfigObject)var4.next();
            object.click(mouseX, mouseY);
        }

        if (RenderUtil.isInRegion((double)mouseX, (double)mouseY, this.x + 15.0F, this.y + 10.0F, 11.0F, 11.0F)) {
            Managment.CONFIG_MANAGER.saveConfiguration("cfg" + RandomStringUtils.randomAlphabetic(2));
            this.objects.clear();
            var4 = Managment.CONFIG_MANAGER.getAllConfigurations().iterator();

            while(var4.hasNext()) {
                String cfg = (String)var4.next();
                this.objects.add(new ConfigObject(cfg));
            }
        }

        if (RenderUtil.isInRegion((double)mouseX, (double)mouseY, this.x + 150.5F + 30.0F, this.y + 10.0F, 150.5F, 14.5F)) {
            this.searching = !this.searching;
        }

        if (RenderUtil.isInRegion((double)mouseX, (double)mouseY, this.x + 35.0F, this.y + 10.0F, 11.0F, 11.0F)) {
            try {
                Runtime.getRuntime().exec("explorer " + ConfigManager.CONFIG_DIR.getAbsolutePath());
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

    }

    public void charTyped(char chars) {
        Iterator var2 = this.objects.iterator();

        while(var2.hasNext()) {
            ConfigObject object = (ConfigObject)var2.next();
            object.charTyped(chars);
        }

    }

    public void keyTyped(int key) {
        Iterator var2 = this.objects.iterator();

        while(var2.hasNext()) {
            ConfigObject object = (ConfigObject)var2.next();
            object.keyTyped(key);
        }

    }
}