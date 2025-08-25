package ru.levinov.server;

import com.sun.net.httpserver.HttpServer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionManager;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.HudUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class WebServer {
    static String query;
    public static void startServer() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            HttpServer server = HttpServer.create(new InetSocketAddress(ipAddress, 3030), 0);
            server.createContext("/", exchange -> {
                if ("POST".equals(exchange.getRequestMethod())) {
                    query = exchange.getRequestURI().getQuery();
                    new HtmlCommand();
                    exchange.getResponseHeaders().add("Location", "/");
                    exchange.sendResponseHeaders(302, -1);
                    return;
                }
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html>");
                html.append("<html><head>");
                html.append("<meta charset='UTF-8'>");
                html.append("<title>ExosWare Web ����������</title>");
                html.append("<style>");
                html.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
                html.append("body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #1a1a1a; color: #fff; line-height: 1.6; }");
                html.append(".container { max-width: 800px; margin: 0 auto; padding: 20px; position: relative; }");
                html.append("h1 { text-align: center; color: #00ff88; margin: 20px 0; font-size: 2.5em; text-transform: uppercase; }");
                html.append(".modules-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 15px; }");
                html.append(".module { background: #2a2a2a; border-radius: 10px; padding: 20px; transition: all 0.3s ease; }");
                html.append(".module:hover { transform: translateY(-5px); box-shadow: 0 5px 15px rgba(0,0,0,0.3); }");
                html.append(".enabled { border-left: 4px solid #00ff88; }");
                html.append(".disabled { border-left: 4px solid #ff4444; }");
                html.append(".module-name { font-size: 1.2em; font-weight: bold; margin-bottom: 10px; }");
                html.append(".status { font-size: 0.9em; opacity: 0.8; margin-bottom: 15px; }");
                html.append("button { width: 100%; padding: 10px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; transition: all 0.2s ease; margin-bottom: 10px; }"); // �������� margin-bottom
                html.append(".enabled button { background: #ff4444; color: white; }");
                html.append(".disabled button { background: #00ff88; color: black; }");
                html.append("button:hover { opacity: 0.9; transform: scale(1.02); }");
                html.append("@media (max-width: 600px) { .modules-grid { grid-template-columns: 1fr; } }");
                html.append(".inventory-box { background: #2a2a2a; border-radius: 10px; padding: 20px; margin-top: 20px; }");
                html.append(".inventory-header { font-size: 1.5em; margin-bottom: 10px; }");
                html.append(".inventory-item { margin: 5px 0; }");

                html.append(".profile-square {\n" +
                        "    width: 150px; /* ������ �������� */\n" +
                        "    height: 150px; /* ������ �������� */\n" +
                        "    border: 2px solid #000; /* ������ ����� */\n" +
                        "    background-color: #0000ff; /* ���� ���� */\n" +
                        "    display: flex; /* ���������� flexbox ��� ������������� ������ */\n" +
                        "    align-items: center; /* ���������� �� ��������� */\n" +
                        "    justify-content: center; /* ���������� �� ����������� */\n" +
                        "    margin: 10px; /* ������ ������ �������� */\n" +
                        "    border-radius: 10px; /* ������������ ���� (�� �������) */\n" +
                        "}");


                html.append("</style></head>");
                html.append("<body><div class='container'>");


                //�������
                html.append("<h1>Web-���������� ExosWare</h1>");
                html.append("<h2>���������</h2>");
                for (int i = 0; i < 36; ++i) {
                    ItemStack slot = Minecraft.getInstance().player.inventory.getStackInSlot(i);
                    // ���������, ���� ���������� ��������� ������ ����
                    if (slot.getCount() > 0) {
                        html.append("<div class='inventory-item'>")
                                .append(slot.getDisplayName().getString())
                                .append(" (")
                                .append(slot.getCount())
                                .append(" ��.)</div>");
                    }
                }
                html.append("<h2>�������</h2>");

                html.append("<div class='profile-square'>")
                        .append("��������: ").append(Minecraft.getInstance().player.getHealth())
                        .append("<br>")
                        .append("���: ").append(Minecraft.getInstance().player.getName().getString())
                        .append("<br>")
                        .append("FPS: ").append(Minecraft.debugFPS)
                        .append("<br>")
                        .append("PING: ").append(HudUtil.calculatePing())
                        .append("</div>");

                html.append("<h2>�������</h2>");

                html.append("<form method='POST' action='/?sendGreeting'>");
                html.append("<button type='submit'>�������� (������ ����)</button>");
                html.append("</form>");

                html.append("<form method='POST' action='/?spawn'>");
                html.append("<button type='submit'>�������� �� /spawn</button>");
                html.append("</form>");

                html.append("<form method='POST' action='/?contact'>");
                html.append("<button type='submit'>�������� /contact � ��� ������</button>");
                html.append("</form>");

                //�����
                html.append("<h2>������</h2>"); // ���� ����� �������� � "����", ��������� ��� ����
                for (Function module : FunctionManager.functions) {
                    html.append("<div class='module ").append(module.state ? "enabled" : "disabled").append("'>");
                    html.append("<div class='module-name'>").append(module.name).append("</div>");
                    html.append("<div class='status'>������: ").append(module.isState() ? "��������" : "���������").append("</div>");
                    html.append("<form method='POST' action='/?toggle=").append(module.name).append("'>");
                    html.append("<button type='submit'>").append(module.isState() ? "���������" : "��������").append("</button>");
                    html.append("</form></div>");
                }

                html.append("</div></body></html>");
                String response = html.toString();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }

            });
            server.setExecutor(null);
            server.start();
            ClientUtil.sendMesage("���� ��� �������: https:/" + server.getAddress());
        } catch (IOException e) {
        //    System.out.println("Failed to start web server: " + e.getMessage());
        }
    }
}