package de.keksuccino.cinematica.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VanillaAudioHandler {

    private static final Logger LOGGER = LogManager.getLogger("cinematica/VanillaAudioHandler");

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
                if (Minecraft.getInstance().getMusicManager() != null) {
//                    Cinematica.LOGGER.info("############# SUPPRESS WORLD MUSIC!");
                    Minecraft.getInstance().getMusicManager().stopPlaying();
                }
            } else {
                if (Minecraft.getInstance().getMusicManager() != null) {
//                    Cinematica.LOGGER.info("############# UN-SUPPRESS WORLD MUSIC!");
                    Minecraft.getInstance().getMusicManager().stopPlaying();
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
        if (!doFadeOut && (Minecraft.getInstance().getSoundManager() != null)) {
            doFadeOut = true;
            suppressWorldMusic = true;
            lastFadeOutTick = 0L;
            fadeOutVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
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

    public static Map<SoundInstance, ChannelAccess.ChannelHandle> getCurrentlyPlayingSoundsOfCategory(SoundSource category) {
        Map<SoundInstance, ChannelAccess.ChannelHandle> sounds = new HashMap<>();
        try {
            Map<SoundInstance, ChannelAccess.ChannelHandle> playingSounds = getCurrentlyPlayingSounds();
            if (playingSounds != null) {
                for (Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> m : playingSounds.entrySet()) {
                    if (m.getKey().getSource() == category) {
                        sounds.put(m.getKey(), m.getValue());
                    }
                }
            } else {
                LOGGER.error("ERROR: Unable to get currently playing sounds!");
                Minecraft.getInstance().getSoundManager().pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sounds;
    }

    @Nullable
    public static Map<SoundInstance, ChannelAccess.ChannelHandle> getCurrentlyPlayingSounds() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundEngine.class, "f_120226_"); //instanceToChannel
            SoundEngine e = getSoundEngine();
            if (e != null) {
                return (Map<SoundInstance, ChannelAccess.ChannelHandle>) f.get(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static SoundEngine getSoundEngine() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundManager.class, "f_120349_"); //soundEngine
            return (SoundEngine) f.get(Minecraft.getInstance().getSoundManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setTimeUntilNextBackgroundMusic(int timeUntilNextMusic) {
        try {
            Field f = ObfuscationReflectionHelper.findField(MusicManager.class, "f_120180_"); //nextSongDelay
            f.set(Minecraft.getInstance().getMusicManager(), timeUntilNextMusic);
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
                    Minecraft.getInstance().options.setSoundCategoryVolume(SoundSource.MUSIC, fadeOutVolume);
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
                Minecraft.getInstance().options.setSoundCategoryVolume(SoundSource.MUSIC, cachedFadeOutMusicVolume);
//                Cinematica.LOGGER.info("################## ORIGINAL MUSIC VOLUME RESTORED TO: " + cachedFadeOutMusicVolume);
                doFadeOut = false;
            }

        } else if (unsuppressedWhileFading) {
            setSuppressWorldMusic(false);
            unsuppressedWhileFading = false;
        }

    }

}
