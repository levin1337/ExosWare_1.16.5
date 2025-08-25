package ru.levinov.modules.impl.render;

import net.minecraft.util.ResourceLocation;
import net.optifine.shaders.Shaders;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.util.ClientUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@FunctionAnnotation(name = "MotionBlur", type = Type.Render)
public class MotionBlur extends Function {


    @Override
    protected void onEnable() {
        super.onEnable();
        try {
            // Замените на URL файла, который нужно скачать
            String fileUrl = "http://exosland.ru/motion.zip";
            // Замените на путь, куда нужно сохранить файл
            String savePath = "C:\\ProgramData\\Google\\launch\\shaderpacks\\motion.zip";
            File file = new File(savePath); // Создаем объект File для проверки

            if (!file.exists()) {
                // Если файл не существует, скачиваем его
                ClientUtil.downloadFile(fileUrl, savePath);
                ClientUtil.sendMesage("Шейдер скачан!");
            } else {
            }

            ClientUtil.sendMesage("Загружаю...");
            Shaders.setShaderPack("motion.zip");
        } catch (Exception e) {
            //      System.err.println("Ошибка при скачивании файла: " + e.getMessage());
        }
    }
    @Override
    protected void onDisable() {
        super.onEnable();
        Shaders.setShaderPack("Отключены");
    }
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {

        }
    }
}
