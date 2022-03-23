package de.keksuccino.cinematica.audio;

import de.keksuccino.auudio.audio.AudioChannel;
import de.keksuccino.auudio.audio.AudioClip;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioCinematicHandler {

    protected static Map<String, AudioClip> audios = new HashMap<>();

    protected static List<AudioClip> unfinishedAudioCache = new ArrayList<>();

    @Nullable
    public static AudioClip getAudio(String source) {
        File f = new File(source);
        if (f.isFile() && (f.getPath().toLowerCase().endsWith(".mp3") || f.getPath().toLowerCase().endsWith(".wav"))) {
            if (!audios.containsKey(source)) {
                AudioClip c = new AudioClip(source, AudioClip.AudioSource.LOCAL);
                c.setAudioChannel(AudioChannel.MASTER);
                audios.put(source, c);
                AudioCinematicVolumeHandler.updateVolume();
            }
            return audios.get(source);
        }
        return null;
    }

    public static void removeAudioFromCache(String source) {
        AudioClip c = audios.remove(source);
        if (c != null) {
            c.destroy();
        }
    }

    public static List<AudioClip> getCachedAudios() {
        List<AudioClip> l = new ArrayList<>();
        l.addAll(audios.values());
        return l;
    }

    public static void pauseAll() {
        for (AudioClip c : audios.values()) {
            if (c.isPlaying()) {
                if (!unfinishedAudioCache.contains(c)) {
                    unfinishedAudioCache.add(c);
                    c.pause();
                }
            }
        }
    }

    public static void stopAll() {
        for (AudioClip c : audios.values()) {
            c.stop();
        }
        unfinishedAudioCache.clear();
    }

    public static void restartAll() {
        for (AudioClip c : audios.values()) {
            c.restart();
        }
    }

    public static void resumeUnfinishedAudios() {
        for (AudioClip c : unfinishedAudioCache) {
            try {
                if (c.isAudioReady()) {
                    c.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        unfinishedAudioCache.clear();
    }

    public static void clearUnfinishedAudioCache() {
        unfinishedAudioCache.clear();
    }

}
