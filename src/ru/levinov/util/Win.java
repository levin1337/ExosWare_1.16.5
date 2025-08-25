package ru.levinov.util;

import java.io.*;

import java.io.File;

public class Win {
    private static File pack;
    private static File shader;

    public static void packs() {
        // ������� ������ File ��� ����� selfdestruct
        File selfDestructDir = new File("C:\\ProgramData\\Google\\launch\\files\\selfdestruct");

        // ������� �����, ���� ��� �� ����������
        if (!selfDestructDir.exists()) {
            selfDestructDir.mkdirs(); // ������� ����� � ��� ������������ ����������
        }

        // ������ ������� ���� packsPath.txt
        pack = new File(selfDestructDir, "packsPath.txt");
        if (!pack.exists()) {
            try {
                pack.createNewFile(); // ������� ����� ����
                // ���������� ���� �� ������ �����
                try (FileWriter writer = new FileWriter(pack)) {
                    writer.write("C:\\Minecraft\\game\\resourcepacks");
                }
            } catch (Exception e) {
                //        e.printStackTrace(); // ������� ���� ������ � ������ ����������
            }
        }
    }

    public static void shaders() {
        // ������� ������ File ��� ����� selfdestruct
        File selfDestructDir = new File("C:\\ProgramData\\Google\\launch\\files\\selfdestruct");

        // ���������, ���������� �� �����
        if (!selfDestructDir.exists()) {
            selfDestructDir.mkdirs(); // ������� ����� � ��� ������������ ����������
        }

        // ������ ������� ���� shaderPath.txt
        shader = new File(selfDestructDir, "shaderPath.txt");
        if (!shader.exists()) {
            try {
                // ������� ����, ���� �� �� ����������
                shader.createNewFile(); // ������� ����� ����

                // ���������� ���� �� ��������
                try (FileWriter writer = new FileWriter(shader)) {
                    writer.write("C:\\Minecraft\\game\\shaderpacks");
                }
            } catch (Exception e) {
                //     e.printStackTrace(); // ������� ���� ������ � ������ ����������
            }
        }
    }


    public static File getResourcePacksPath() {
        return getPathFromFile(pack);
    }

    // ����� ��� ��������� ���� �� ��������
    public static File getShaderPacksPath() {
        return getPathFromFile(shader);
    }

    // ��������������� ����� ��� ������ ���� �� �����
    private static File getPathFromFile(File file) {
        String path = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            path = reader.readLine(); // ������ ������ ������
        } catch (IOException e) {
            //  e.printStackTrace(); // ������� ���� ������ � ������ ����������
        }
        return new File(path); // ���������� ������ File � ���������� �����
    }


}
