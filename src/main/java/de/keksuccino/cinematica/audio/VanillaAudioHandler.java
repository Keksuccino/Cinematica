package de.keksuccino.cinematica.audio;

import de.keksuccino.cinematica.Cinematica;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VanillaAudioHandler {

    protected static volatile boolean suppressWorldMusic = false;

    protected static volatile boolean doFadeOut = false;
    protected static volatile boolean unsuppressedWhileFading = false;
    protected static List<Runnable> postFadingTaskList = new ArrayList<>();
    protected static long lastFadeOutTick = 0L;
    protected static float fadeOutVolume = 0.0F;
    protected static float cachedFadeOutMusicVolume = 0.0F;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new VanillaAudioHandler());
    }

    public static void setSuppressWorldMusic(boolean suppress) {
        setSuppressWorldMusic(suppress, false);
    }

    public static void setSuppressWorldMusic(boolean suppress, boolean forceSetWhileFading) {
        if (!doFadeOut || forceSetWhileFading) {
            suppressWorldMusic = suppress;
            if (suppress) {
                if (Minecraft.getInstance().getMusicTicker() != null) {
//                    Cinematica.LOGGER.info("############# SUPPRESS WORLD MUSIC!");
                    Minecraft.getInstance().getMusicTicker().stop();
                }
            } else {
                if (Minecraft.getInstance().getMusicTicker() != null) {
//                    Cinematica.LOGGER.info("############# UN-SUPPRESS WORLD MUSIC!");
                    Minecraft.getInstance().getMusicTicker().stop();
                    setTimeUntilNextBackgroundMusic(50);
                }
            }
        } else if (!suppress) {
//            Cinematica.LOGGER.info("############# TRIED TO UN-SUPPRESS WORLD MUSIC WHILE FADING!");
            unsuppressedWhileFading = true;
        }
    }

    public static void fadeOutAndSuppressWorldMusic() {
        fadeOutAndSuppressWorldMusic(null);
    }

    public static void fadeOutAndSuppressWorldMusic(@Nullable Runnable doAfterSuppress) {
        if (!doFadeOut && (Minecraft.getInstance().getSoundHandler() != null)) {
            doFadeOut = true;
            suppressWorldMusic = true;
            lastFadeOutTick = 0L;
            fadeOutVolume = Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.MUSIC);
            cachedFadeOutMusicVolume = fadeOutVolume;
            postFadingTaskList.clear();
        }
        if (doAfterSuppress != null) {
            postFadingTaskList.add(doAfterSuppress);
        }
    }

    public static boolean isCurrentlyFadingOut() {
        return doFadeOut;
    }

    public static boolean isSuppressWorldMusic() {
        return suppressWorldMusic;
    }

    public static Map<ISound, ChannelManager.Entry> getCurrentlyPlayingSoundsOfCategory(SoundCategory category) {
        Map<ISound, ChannelManager.Entry> sounds = new HashMap<>();
        try {
            Map<ISound, ChannelManager.Entry> playingSounds = getCurrentlyPlayingSounds();
            if (playingSounds != null) {
                for (Map.Entry<ISound, ChannelManager.Entry> m : playingSounds.entrySet()) {
                    if (m.getKey().getCategory() == category) {
                        sounds.put(m.getKey(), m.getValue());
                    }
                }
            } else {
                Cinematica.LOGGER.error("[CINEMATICA] ERROR: VanillaAudioHandler: Unable to get currently playing sounds!");
                Minecraft.getInstance().getSoundHandler().pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sounds;
    }

    @Nullable
    public static Map<ISound, ChannelManager.Entry> getCurrentlyPlayingSounds() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundEngine.class, "field_217942_m"); //playingSoundsChannel
            SoundEngine e = getSoundEngine();
            if (e != null) {
                return (Map<ISound, ChannelManager.Entry>) f.get(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static SoundEngine getSoundEngine() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundHandler.class, "field_147694_f"); //sndManager
            return (SoundEngine) f.get(Minecraft.getInstance().getSoundHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setTimeUntilNextBackgroundMusic(int timeUntilNextMusic) {
        try {
            Field f = ObfuscationReflectionHelper.findField(MusicTicker.class, "field_147676_d"); //timeUntilNextMusic
            f.set(Minecraft.getInstance().getMusicTicker(), timeUntilNextMusic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {

        if (doFadeOut) {

            if (fadeOutVolume > 0.0F) {
                long now = System.currentTimeMillis();
                if ((lastFadeOutTick + 100) <= now) {
                    fadeOutVolume = fadeOutVolume - 0.1F;
                    if (fadeOutVolume < 0.0F) {
                        fadeOutVolume = 0.0F;
                    }
                    Minecraft.getInstance().gameSettings.setSoundLevel(SoundCategory.MUSIC, fadeOutVolume);
//                    Cinematica.LOGGER.info("################## SET VOLUME TO: " + fadeOutVolume);
                    lastFadeOutTick = now;
                }
            } else {
//                Cinematica.LOGGER.info("################## FADED-OUT WORLD MUSIC!");
                setSuppressWorldMusic(true, true);
                if (!postFadingTaskList.isEmpty()) {
                    for (Runnable r : postFadingTaskList) {
                        r.run();
                    }
                }
                Minecraft.getInstance().gameSettings.setSoundLevel(SoundCategory.MUSIC, cachedFadeOutMusicVolume);
//                Cinematica.LOGGER.info("################## ORIGINAL MUSIC VOLUME RESTORED TO: " + cachedFadeOutMusicVolume);
                doFadeOut = false;
            }

        } else if (unsuppressedWhileFading) {
            setSuppressWorldMusic(false);
            unsuppressedWhileFading = false;
        }

    }

}
