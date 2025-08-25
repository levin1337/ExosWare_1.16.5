package ru.levinov.util;

import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;
import org.lwjgl.glfw.GLFW;
import ru.levinov.Launch;
import ru.levinov.managment.Managment;
import ru.levinov.modules.impl.combat.Aura;
import ru.levinov.util.math.KeyMappings;
import ru.levinov.util.misc.HudUtil;
import ru.levinov.util.render.ColorUtil;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClientUtil implements IMinecraft {

    public static User me;
    public static ServerData serverData;
    public static boolean legitMode = false;
    private static boolean pvpMode;
    private static UUID uuid;

    public static String getKey(int integer) {
        if (integer < 0) {
            return switch (integer) {
                case -100 -> I18n.format("key.mouse.left");
                case -99 -> I18n.format("key.mouse.right");
                case -98 -> I18n.format("key.mouse.middle");
                default -> "MOUSE" + (integer + 101);
            };
        } else {
            return (GLFW.glfwGetKeyName(integer, -1) == null ? KeyMappings.reverseKeyMap.get(integer) : GLFW.glfwGetKeyName(integer, -1)) ;
        }
    }

    static IPCClient client = new IPCClient(1216825211895287868L);

    public static void startRPC() {
        client.setListener(new IPCListener(){
            @Override
            public void onPacketReceived(IPCClient client, Packet packet) {
                IPCListener.super.onPacketReceived(client, packet);
            }

            @Override
            public void onReady(IPCClient client)
            {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setDetails("").setStartTimestamp(OffsetDateTime.now()).setLargeImage("https://i.gifer.com/3OoPr.gif","https://t.me/exosware");
                builder.setDetails("name: " + Managment.USER_PROFILE.getName());
                builder.setState("role: user");
                builder.setSmallImage("https://i.imgur.com/CgRvwzj.jpeg");


                client.sendRichPresence(builder.build());
            }
        });
        try {
            client.connect();
        } catch (NoDiscordClientException e) {
          //  System.out.println("DiscordRPC: " + e.getMessage());
        }
    }

    public static void updateBossInfo(SUpdateBossInfoPacket packet) {
        if (packet.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
            if (StringUtils.stripControlCodes(packet.getName().getString()).toLowerCase().contains("pvp")) {
                pvpMode = true;
                uuid = packet.getUniqueId();
            }
        } else if (packet.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE) {
            if (packet.getUniqueId().equals(uuid))
                pvpMode = false;
        }
    }

    public static void downloadFile(String fileUrl, String savePath) throws Exception {
        URL url = new URL(fileUrl);
        URLConnection connection = url.openConnection();
        InputStream inputStream = connection.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(savePath);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();
    }

    public static boolean isPvP() {
        return pvpMode;
    }

    public static void stopRPC() {
        if (client.getStatus() == PipeStatus.CONNECTED)
            client.close();
    }

    public static boolean isConnectedToServer(String ip) {
        return mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null && mc.getCurrentServerData().serverIP.contains(ip);
    }
    public static String hash64(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        String decode = new String(bytes);
        return decode;
    }
    private List<String> getOnlinePlayers() {
        return mc.player.connection.getPlayerInfoMap().stream().map(NetworkPlayerInfo::getGameProfile).map(GameProfile::getName).collect(Collectors.toList());
    }

    public static void sendMesage(String message) {
        if (mc.player == null) return;
        mc.player.sendMessage(gradient(Launch.commandClient, ColorUtil.getColorStyle(30), ColorUtil.getColorStyle(255)).append(new StringTextComponent(TextFormatting.DARK_GRAY + " --> " + TextFormatting.RESET + message)), Util.DUMMY_UUID);
    }

    public static StringTextComponent gradient(String message, int first, int end) {
        StringTextComponent text = new StringTextComponent("");
        for (int i = 0; i < message.length(); i++) {
            text.append(new StringTextComponent(String.valueOf(message.charAt(i))).setStyle(Style.EMPTY.setColor(new Color(ColorUtil.interpolateColor(first, end, (float) i / message.length())))));
        }
        return text;
    }

    static String decodedText;
    public static String base64Util(String hash) {
        byte[] decodedBytes = Base64.getDecoder().decode(hash);
        decodedText = new String(decodedBytes);
        return decodedText;
    }
    public static void openURL(String url) {
        Util.getOSType().openURI(URI.create(url));
    }

    public static ITextComponent replace(ITextComponent original, String find, String replaceWith) {
        if (original == null || find == null || replaceWith == null) {
            return original;
        }
        String originalText = original.getString();
        String replacedText = originalText.replace(find, replaceWith);
        return new StringTextComponent(replacedText);
    }
}
