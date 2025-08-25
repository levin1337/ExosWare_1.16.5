package ru.levinov.managment.notification;

import com.mojang.blaze3d.matrix.MatrixStack;
import ru.levinov.modules.impl.player.ItemScroller;
import ru.levinov.modules.impl.render.HUD2;
import ru.levinov.util.IMinecraft;
import ru.levinov.util.animations.Animation;
import ru.levinov.util.animations.Direction;
import ru.levinov.util.animations.impl.DecelerateAnimation;
import ru.levinov.util.font.Fonts;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.levinov.util.font.Fonts.durman;
import static ru.levinov.util.font.Fonts.hudicon;
import static ru.levinov.util.render.ColorUtil.rgba;

public class NotificationManager {

    private final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList();

    public void add(String text, String content, int time) {
        this.notifications.add(new Notification(text, content, time));
    }

    public void draw(MatrixStack stack) {
        int yOffset = 0;
        for (Notification notification : this.notifications) {
            if (System.currentTimeMillis() - notification.getTime() > (long)notification.time2 * 1000L) {
                notification.animation.setDirection(Direction.BACKWARDS);
            } else {
                notification.yAnimation.setDirection(Direction.FORWARDS);
                notification.animation.setDirection(Direction.FORWARDS);
            }
            notification.alpha = (float)notification.animation.getOutput();
            if (System.currentTimeMillis() - notification.getTime() > (long)notification.time2 * 1000L) {
                notification.yAnimation.setDirection(Direction.BACKWARDS);
            }
            if (notification.yAnimation.finished(Direction.BACKWARDS)) {
                this.notifications.remove(notification);
                continue;
            }
            float x = (float) IMinecraft.mc.getMainWindow().scaledWidth() - (Fonts.gilroyBold[14].getWidth(notification.getText()) + 8.0f) - 10.0f;
            float y = IMinecraft.mc.getMainWindow().scaledHeight() - 100;
            notification.yAnimation.setEndPoint(yOffset);
            notification.yAnimation.setDuration(300);
            notification.setX(x);
            notification.setY(AnimationMath.fast(notification.getY(), y -= (float)((double)notification.draw(stack) * notification.yAnimation.getOutput()), 15.0f));
            ++yOffset;
        }
    }

    private class Notification {
        private float x;
        private float y = 25;
        private String text;
        private String content;
        private long time = System.currentTimeMillis();
        public Animation animation = new DecelerateAnimation(500, 1.0, Direction.FORWARDS);
        public Animation yAnimation = new DecelerateAnimation(500, 1.0, Direction.FORWARDS);
        float alpha;
        int time2 = 0;

        public Notification(String text, String content, int time) {
            this.text = text;
            this.content = content;
            this.time2 = time;
        }
        final int b_color = new Color(0, 0, 0, 128).getRGB();
        public float draw(MatrixStack stack) {
            float width2 = Fonts.gilroyBold[14].getWidth(this.text) + 50.0f;
            RenderUtil.Render2D.drawRoundedCorner(x - 35.8f, y - 1f, width2 + 2, 17, 3, HUD2.delta_color);

            hudicon[18].drawString(stack, "W", x - 30.0f , y + 6.5f, Color.WHITE.getRGB());
            durman[16].drawString(stack,text, (double)(x - 16.0f), (double)(y + 5f), RenderUtil.reAlphaInt(-1, (int)(255.0f * this.alpha)));
            return 24.0f;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }

        public String getText() {
            return this.text;
        }

        public String getContent() {
            return this.content;
        }

        public long getTime() {
            return this.time;
        }
    }
}
