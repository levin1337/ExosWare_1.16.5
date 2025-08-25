/* Decompiler 41ms, total 673ms, lines 153 */
package ru.levinov.ui.clickgui.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joml.Vector4i;
import ru.levinov.managment.config.Config;
import ru.levinov.managment.config.ConfigManager;
import ru.levinov.managment.Managment;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.font.styled.StyledFont;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.ColorUtil;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.RenderUtil.Render2D;
import ru.levinov.util.render.animation.AnimationMath;

public class ConfigObject {
    public float x;
    public float y;
    public float width;
    public float height;
    public String cfg;
    public String staticF;
    private String author;
    private String formattedDate;
    public float animationx;
    public float animationy;
    public float animationz;
    public TimerUtil timerUtil = new TimerUtil();
    boolean nameChange;
    int clicked;

    public ConfigObject(String cfg) {
        this.staticF = cfg;
        this.cfg = cfg;
        this.author = "Unknown.";
        Config config = Managment.CONFIG_MANAGER.findConfig(this.cfg);
        JsonElement element = ConfigManager.compressAndWrite(config.getFile());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.formattedDate = "2000-01-01";
        if (element != null && !element.isJsonNull() && element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("Others")) {
                JsonObject others = object.getAsJsonObject("Others");
                if (others.has("author")) {
                    this.author = others.get("author").getAsString();
                }

                if (others.has("time")) {
                    this.formattedDate = dateFormat.format(new Date(others.get("time").getAsLong()));
                }
            }
        } else {
            this.author = "Unknown.";
        }

    }

    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        if (this.timerUtil.hasTimeElapsed(1000L)) {
            this.clicked = 0;
        }

        Render2D.drawRoundOutline(this.x, this.y, this.width, this.height, 5.0F, 0.0F, ColorUtil.rgba(25, 26, 33, 0), new Vector4i(ColorUtil.rgba(38, 40, 59, 255)));
        StyledFont var10000 = Fonts.msSemiBold[20];
        String var10002 = this.cfg;
        var10000.drawString(stack, var10002 + (this.nameChange ? (System.currentTimeMillis() % 1000L > 500L ? "" : "_") : ""), (double)(this.x + 8.0F), (double)(this.y + 8.0F), -1);
        float offset = 20.0F;
        this.animationx = AnimationMath.fast(this.animationx, RenderUtil.isInRegion((double)mouseX, (double)mouseY, this.x + this.width - offset * 3.0F, this.y + this.height - offset, 14.5F, 14.5F) ? 1.0F : 0.0F, 10.0F);
        this.animationy = AnimationMath.fast(this.animationy, RenderUtil.isInRegion((double)mouseX, (double)mouseY, this.x + this.width - offset * 2.0F, this.y + this.height - offset, 14.5F, 14.5F) ? 1.0F : 0.0F, 10.0F);
        this.animationz = AnimationMath.fast(this.animationz, RenderUtil.isInRegion((double)mouseX, (double)mouseY, this.x + this.width - offset * 1.0F, this.y + this.height - offset, 14.5F, 14.5F) ? 1.0F : 0.0F, 10.0F);
        Render2D.drawRoundedRect(this.x + this.width - offset, this.y + this.height - offset, 14.5F, 14.5F, 4.0F, ColorUtil.rgba(0.0D, 0.0D, 0.0D, 84.1500015258789D));
        Render2D.drawRoundedRect(this.x + this.width - offset * 2.0F, this.y + this.height - offset, 14.5F, 14.5F, 4.0F, ColorUtil.rgba(0.0D, 0.0D, 0.0D, 84.1500015258789D));
        Render2D.drawRoundedRect(this.x + this.width - offset * 3.0F, this.y + this.height - offset, 14.5F, 14.5F, 4.0F, ColorUtil.rgba(0.0D, 0.0D, 0.0D, 84.1500015258789D));
        Fonts.configIcon[16].drawString(stack, "J", (double)(this.x + this.width - offset * 3.0F + 3.0F), (double)(this.y + this.height - offset + 6.0F), -1);
        Fonts.configIcon[16].drawString(stack, "M", (double)(this.x + this.width - offset * 2.0F + 3.5F), (double)(this.y + this.height - offset + 6.0F), -1);
        Fonts.configIcon[16].drawString(stack, "I", (double)(this.x + this.width - offset * 1.0F + 3.0F), (double)(this.y + this.height - offset + 6.0F), -1);
        BloomHelper.registerRenderCall(() -> {
            Fonts.configIcon[16].drawString(stack, "J", (double)(this.x + this.width - offset * 3.0F + 3.0F), (double)(this.y + this.height - offset + 6.0F), RenderUtil.reAlphaInt(-1, (int)(255.0F * this.animationx)));
            Fonts.configIcon[16].drawString(stack, "M", (double)(this.x + this.width - offset * 2.0F + 3.5F), (double)(this.y + this.height - offset + 6.0F), RenderUtil.reAlphaInt(-1, (int)(255.0F * this.animationy)));
            Fonts.configIcon[16].drawString(stack, "I", (double)(this.x + this.width - offset * 1.0F + 3.0F), (double)(this.y + this.height - offset + 6.0F), RenderUtil.reAlphaInt(-1, (int)(255.0F * this.animationz)));
        });
        Fonts.msMedium[12].drawString(stack, "Author: ", (double)(this.x + 6.0F), (double)(this.y + this.height - 10.0F), ColorUtil.rgba(161, 164, 177, 255));
        Fonts.msMedium[12].drawString(stack, this.author, (double)(this.x + 6.0F + Fonts.msMedium[12].getWidth("Author: ")), (double)(this.y + this.height - 10.0F), ColorUtil.rgba(255, 255, 255, 255));
        Fonts.msMedium[12].drawString(stack, "Created: ", (double)(this.x + 6.0F), (double)(this.y + this.height - 20.0F), ColorUtil.rgba(161, 164, 177, 255));
        Fonts.msMedium[12].drawString(stack, this.formattedDate, (double)(this.x + 6.0F + Fonts.msMedium[12].getWidth("Created: ")), (double)(this.y + this.height - 20.0F), ColorUtil.rgba(255, 255, 255, 255));
    }

    public void charTyped(char chars) {
        if (this.nameChange && this.cfg.length() < 15) {
            this.cfg = this.cfg + chars;
        }

    }

    public void keyTyped(int key) {
        if (key == 257 && this.nameChange) {
            this.nameChange = false;
            Config cfg = Managment.CONFIG_MANAGER.findConfig(this.staticF);
            if (cfg != null) {
                cfg.getFile().renameTo(new File(ConfigManager.CONFIG_DIR, this.cfg + ".cfg"));
                this.staticF = this.cfg;
            }
        }

        if (key == 259 && this.nameChange && !this.cfg.isEmpty()) {
            this.cfg = this.cfg.substring(0, this.cfg.length() - 1);
        }

    }

    public void click(int mx, int my) {
        float offset = 20.0F;
        if (RenderUtil.isInRegion((double)mx, (double)my, this.x + this.width - offset * 3.0F, this.y + this.height - offset, 14.5F, 14.5F)) {
            try {
                Files.delete(Managment.CONFIG_MANAGER.findConfig(this.cfg).getFile().toPath());
            } catch (IOException var5) {
            }

            ConfigDrawing.configDrawing.objects.remove(this);
        }

        if (RenderUtil.isInRegion((double)mx, (double)my, this.x, this.y, this.width, this.height)) {
            this.timerUtil.reset();
            ++this.clicked;
            if (this.clicked >= 2) {
                this.nameChange = true;
                ConfigDrawing.configDrawing.searching = false;
            }
        }

        if (RenderUtil.isInRegion((double)mx, (double)my, this.x + this.width - offset * 2.0F, this.y + this.height - offset, 14.5F, 14.5F)) {
            Managment.CONFIG_MANAGER.saveConfiguration(this.cfg);
            ClientUtil.sendMesage("Сохранил конфиг " + this.cfg);
        }

        if (RenderUtil.isInRegion((double)mx, (double)my, this.x + this.width - offset * 1.0F, this.y + this.height - offset, 14.5F, 14.5F)) {
            Managment.CONFIG_MANAGER.loadConfiguration(this.cfg, false);
            ClientUtil.sendMesage("Загрузил конфиг " + this.cfg);
        }

    }
}