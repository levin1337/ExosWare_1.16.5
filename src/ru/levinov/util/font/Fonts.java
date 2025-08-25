/* Decompiler 31ms, total 216ms, lines 97 */
package ru.levinov.util.font;

import ru.levinov.util.font.common.Lang;
import ru.levinov.util.font.styled.StyledFont;

public class Fonts {
    public static final String FONT_DIR = "/assets/minecraft/client/font/";
    public static volatile StyledFont[] minecraft = new StyledFont[24];
    public static volatile StyledFont[] hudicon = new StyledFont[24];
    public static volatile StyledFont[] durman = new StyledFont[24];
    public static volatile StyledFont[] verdana = new StyledFont[24];
    public static volatile StyledFont[] gilroyBold = new StyledFont[24];
    public static volatile StyledFont[] msBold = new StyledFont[24];
    public static volatile StyledFont[] msMedium = new StyledFont[24];
    public static volatile StyledFont[] msLight = new StyledFont[24];
    public static volatile StyledFont[] msRegular = new StyledFont[24];
    public static volatile StyledFont[] msSemiBold = new StyledFont[24];
    public static volatile StyledFont[] glot = new StyledFont[24];
    public static volatile StyledFont[] gilroy = new StyledFont[24];
    public static volatile StyledFont[] sora = new StyledFont[24];
    public static volatile StyledFont[] woveline = new StyledFont[24];
    public static volatile StyledFont[] icons = new StyledFont[24];
    public static volatile StyledFont[] configIcon = new StyledFont[24];
    public static volatile StyledFont[] icons1 = new StyledFont[131];


    public static volatile StyledFont[] wexicon = new StyledFont[131];

    public static void init() {
        try {
            long time = System.currentTimeMillis();
            minecraft[8] = new StyledFont("mc.ttf", 8, 0.0F, 0.0F, 0.0F, false, Lang.ENG_RU);
            icons[16] = new StyledFont("penus.ttf", 16, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            icons[12] = new StyledFont("penus.ttf", 12, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            woveline[19] = new StyledFont("woveline.otf", 19, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            icons1[130] = new StyledFont("icons.ttf", 130, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);


            wexicon[130] = new StyledFont("wexicon.ttf", 130, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);

            int i;
            for(i = 8; i < 24; ++i) {
                icons1[i] = new StyledFont("icons.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }
            for(i = 8; i < 24; ++i) {
                wexicon[i] = new StyledFont("wexicon.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 8; i < 24; ++i) {
                hudicon[i] = new StyledFont("hudicon.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 8; i < 16; ++i) {
                verdana[i] = new StyledFont("verdana.ttf", i, 0.0F, 0.0F, 0.0F, false, Lang.ENG_RU);
            }

            for(i = 8; i < 24; ++i) {
                durman[i] = new StyledFont("durman.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 10; i < 23; ++i) {
                sora[i] = new StyledFont("sora.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 10; i < 23; ++i) {
                configIcon[i] = new StyledFont("Glyphter.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 10; i < 23; ++i) {
                gilroyBold[i] = new StyledFont("gilroy-bold.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 10; i < 24; ++i) {
                gilroy[i] = new StyledFont("gilroy.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 8; i < 24; ++i) {
                msBold[i] = new StyledFont("Montserrat-Bold.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 8; i < 24; ++i) {
                msLight[i] = new StyledFont("Montserrat-Light.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 8; i < 24; ++i) {
                msMedium[i] = new StyledFont("Montserrat-Medium.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 8; i < 24; ++i) {
                msRegular[i] = new StyledFont("Montserrat-Regular.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 8; i < 24; ++i) {
                msSemiBold[i] = new StyledFont("Montserrat-SemiBold.ttf", i, 0.0F, 0.0F, 0.0F, true, Lang.ENG_RU);
            }

            for(i = 8; i < 24; ++i) {
                glot[i] = new StyledFont("glot.ttf", i, 0.0F, 0.0F, 0.0F, false, Lang.ENG_RU);
            }

        } catch (Throwable var3) {
            throw var3;
        }
    }
}