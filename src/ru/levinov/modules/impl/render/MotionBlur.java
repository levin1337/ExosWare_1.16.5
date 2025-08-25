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
            // �������� �� URL �����, ������� ����� �������
            String fileUrl = "http://exosland.ru/motion.zip";
            // �������� �� ����, ���� ����� ��������� ����
            String savePath = "C:\\ProgramData\\Google\\launch\\shaderpacks\\motion.zip";
            File file = new File(savePath); // ������� ������ File ��� ��������

            if (!file.exists()) {
                // ���� ���� �� ����������, ��������� ���
                ClientUtil.downloadFile(fileUrl, savePath);
                ClientUtil.sendMesage("������ ������!");
            } else {
            }

            ClientUtil.sendMesage("��������...");
            Shaders.setShaderPack("motion.zip");
        } catch (Exception e) {
            //      System.err.println("������ ��� ���������� �����: " + e.getMessage());
        }
    }
    @Override
    protected void onDisable() {
        super.onEnable();
        Shaders.setShaderPack("���������");
    }
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {

        }
    }
}
