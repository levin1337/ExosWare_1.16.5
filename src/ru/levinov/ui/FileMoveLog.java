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
            // ������� ���������� ����������, ���� ��� �� ����������
            Files.createDirectories(Path.of(destinationFolderPath));

            // �������� ����� �� �������� ���������� � �������
            Files.walk(Path.of(sourceFolderPath))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            Path destinationPath = Path.of(destinationFolderPath, filePath.getFileName().toString());
                            Files.copy(filePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                    //        System.err.println("������ ��� ����������� �����: " + filePath + " -> " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
         //   System.err.println("������ ��� ��������� �����: " + e.getMessage());
        }
    }
}
