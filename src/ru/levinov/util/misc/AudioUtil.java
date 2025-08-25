package ru.levinov.util.misc;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import ru.levinov.util.IMinecraft;

import static ru.levinov.util.IMinecraft.mc;

public class AudioUtil implements IMinecraft {
    private static final Map<String, Clip> clips = new HashMap<>();

    public static synchronized void playSound(String string, float volume) {
        try {
            Clip clip = AudioSystem.getClip();
            InputStream inputStream = mc.getResourceManager().getResource(new ResourceLocation("client/sounds/" + string)).getInputStream();
            BufferedInputStream var4 = new BufferedInputStream(inputStream);
            AudioInputStream var5 = AudioSystem.getAudioInputStream(var4);
            clip.open(var5);
            clip.start();
            FloatControl var6 = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            var6.setValue(volume);
            clips.put(string, clip); // Store the clip for later stopping
        } catch (Exception var7) {
            var7.printStackTrace();
        }
    }



    public static void stopSound(String soundName) {
        Clip clip = clips.get(soundName);
        if (clip != null) {
            clip.stop();
            clips.remove(soundName); // Remove the clip from the map
        }
    }
}