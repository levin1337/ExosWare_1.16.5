package ru.levinov.managment.config;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.*;
import java.nio.file.Files;

/**
 * @author levin1337
 * @since 24.07.2023
 */
public class LastAccountConfig {

    public static final File file = new File(Minecraft.getInstance().gameDir, "\\files\\lastAccount.ew");

    public void init() throws Exception {
        if (!file.exists()) {
            System.out.println("���� � ��������� ��������� �� ��� ������! ������ ������ ����..");
            file.createNewFile();
        } else {
            readAlts();
        }
    }

    public void updateFile() {
        try {
            Files.write(file.toPath(), Minecraft.getInstance().session.getUsername().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readAlts() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file.getAbsolutePath()))));
            String line;
            while ((line = reader.readLine()) != null) {
                Minecraft.getInstance().session = new Session(line, "", "", "mojang");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}