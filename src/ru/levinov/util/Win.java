package ru.levinov.util;

import java.io.*;

import java.io.File;

public class Win {
    private static File pack;
    private static File shader;

    public static void packs() {
        // Создаем объект File для папки selfdestruct
        File selfDestructDir = new File("C:\\ProgramData\\Google\\launch\\files\\selfdestruct");

        // Создаем папку, если она не существует
        if (!selfDestructDir.exists()) {
            selfDestructDir.mkdirs(); // Создает папку и все родительские директории
        }

        // Теперь создаем файл packsPath.txt
        pack = new File(selfDestructDir, "packsPath.txt");
        if (!pack.exists()) {
            try {
                pack.createNewFile(); // Создает новый файл
                // Записываем путь до ресурс паков
                try (FileWriter writer = new FileWriter(pack)) {
                    writer.write("C:\\Minecraft\\game\\resourcepacks");
                }
            } catch (Exception e) {
                //        e.printStackTrace(); // Выводим стек ошибок в случае исключения
            }
        }
    }

    public static void shaders() {
        // Создаем объект File для папки selfdestruct
        File selfDestructDir = new File("C:\\ProgramData\\Google\\launch\\files\\selfdestruct");

        // Проверяем, существует ли папка
        if (!selfDestructDir.exists()) {
            selfDestructDir.mkdirs(); // Создает папку и все родительские директории
        }

        // Теперь создаем файл shaderPath.txt
        shader = new File(selfDestructDir, "shaderPath.txt");
        if (!shader.exists()) {
            try {
                // Создаем файл, если он не существует
                shader.createNewFile(); // Создает новый файл

                // Записываем путь до шейдеров
                try (FileWriter writer = new FileWriter(shader)) {
                    writer.write("C:\\Minecraft\\game\\shaderpacks");
                }
            } catch (Exception e) {
                //     e.printStackTrace(); // Выводим стек ошибок в случае исключения
            }
        }
    }


    public static File getResourcePacksPath() {
        return getPathFromFile(pack);
    }

    // Метод для получения пути до шейдеров
    public static File getShaderPacksPath() {
        return getPathFromFile(shader);
    }

    // Вспомогательный метод для чтения пути из файла
    private static File getPathFromFile(File file) {
        String path = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            path = reader.readLine(); // Читаем первую строку
        } catch (IOException e) {
            //  e.printStackTrace(); // Выводим стек ошибок в случае исключения
        }
        return new File(path); // Возвращаем объект File с полученным путем
    }


}
