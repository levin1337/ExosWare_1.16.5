package ru.protect;

import java.io.*;
import java.net.*;

//“”та всЄ вырезал

public class CheckConnect {
    public static void internet() {
        try {
            InetAddress address = InetAddress.getByName("");
            if (address.isReachable(15000)) {
            } else {
                System.exit(-1);
            }
        } catch (UnknownHostException var1) {
        } catch (IOException var2) {
        }

    }
    //‘улл
    public static void globalWhiteList() {
    }




    public static void port() {
        try {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isLaunchedFromLauncher() {
        return false;
    }
}
