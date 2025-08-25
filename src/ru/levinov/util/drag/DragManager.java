package ru.levinov.util.drag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class DragManager {
    public static HashMap<String, Dragging> draggables = new HashMap<>();

    public static final File DRAG_DATA = new File(Minecraft.getInstance().gameDir, "\\files\\elements.ew");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public static void save() {

        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
        }
        try {
            Files.writeString(DRAG_DATA.toPath(), GSON.toJson(draggables.values()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void load() {
        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
            return;
        }
        Dragging[] draggings;

        try {
            draggings = GSON.fromJson(Files.readString(DRAG_DATA.toPath()), Dragging[].class);

        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        for (Dragging dragging : draggings) {
            if (dragging == null) return;
            Dragging currentDrag = draggables.get(dragging.getName());
            if (currentDrag == null) continue;
            currentDrag.setX(dragging.getX());
            currentDrag.setY(dragging.getY());
            draggables.put(dragging.getName(), currentDrag);
        }
    }

    public static void reset() {
        float off = 10; // Начальное смещение
        for (Dragging dragging : draggables.values()) {
            dragging.setX(dragging.getDefaultX() + 10); // Добавляем смещение к X
            dragging.setY(dragging.getDefaultY() + off); // Добавляем смещение к Y
            off += dragging.getHeight() + 15; // Увеличиваем смещение для следующего элемента
        }
        save(); // Сохраняем новые позиции после сброса
    }

}