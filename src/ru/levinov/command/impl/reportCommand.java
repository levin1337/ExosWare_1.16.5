package ru.levinov.command.impl;

import net.minecraft.util.text.TextFormatting;
import ru.levinov.command.Command;
import ru.levinov.command.Cmd;
import ru.levinov.managment.Managment;
import ru.levinov.util.ClientUtil;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Cmd(
        name = "report",
        description = "�������� ����."
)
public class reportCommand extends Command {
    public reportCommand() {
    }

    public void run(String[] args) throws Exception {
        if (args.length >= 2) {
            String var10000 = String.join(" ", args).substring(7);
            String message = "Reason: " + var10000 + "             USER: " + Managment.USER_PROFILE.getName();
            this.text(message);
        } else {
            this.error();
        }

    }

    public void text(String message) {
        String encodedText = "==";
        byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
        String decodedText = new String(decodedBytes);
        String webhookUrl = "" + decodedText;
        sendMessage(webhookUrl, message);
        Managment.NOTIFICATION_MANAGER.add(TextFormatting.BLUE + "������ ������� ���������", "REPORT", 3);
    }

    public void sendMessage(String webhookUrl, String message) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            String jsonPayload = "{\"content\" : \"" + message + "\"}";
            OutputStream os = conn.getOutputStream();

            try {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            } catch (Throwable var10) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                    }
                }

                throw var10;
            }

            if (os != null) {
                os.close();
            }

            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void error() {
        ClientUtil.sendMesage("" + TextFormatting.RED);
        ClientUtil.sendMesage(TextFormatting.RED + "������ � �������������:");
        ClientUtil.sendMesage(TextFormatting.GRAY + "����������� .report �����");
        ClientUtil.sendMesage(TextFormatting.GREEN + "������: .report ������� speed �� �����!");
    }
}
