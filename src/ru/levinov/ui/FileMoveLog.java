package ru.levinov.ui;

import java.io.IOException;
import java.nio.file.*;

public class FileMoveLog {
    public FileMoveLog() {
    }

    public static void LogsMove() {
        String sourceFolderPath = "C:\\ProgramData\\Google\\launch\\logs";
        String destinationFolderPath = "C:\\Minecraft\\game\\logs";

        try {
            // Создаем директорию назначения, если она не существует
            Files.createDirectories(Path.of(destinationFolderPath));

            // Копируем файлы из исходной директории в целевую
            Files.walk(Path.of(sourceFolderPath))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            Path destinationPath = Path.of(destinationFolderPath, filePath.getFileName().toString());
                            Files.copy(filePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                    //        System.err.println("Ошибка при копировании файла: " + filePath + " -> " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
         //   System.err.println("Ошибка при обработке папки: " + e.getMessage());
        }
    }
}
