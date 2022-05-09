package de.keksuccino.cinematica.engine.cinematic;

import de.keksuccino.auudio.audio.AudioClip;
import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.audio.AudioCinematicHandler;
import de.keksuccino.cinematica.audio.VanillaAudioHandler;
import de.keksuccino.cinematica.cutscene.CutScene;
import de.keksuccino.cinematica.video.VideoHandler;
import de.keksuccino.cinematica.video.VideoRenderer;
import de.keksuccino.konkrete.properties.PropertiesSection;
import de.keksuccino.konkrete.properties.PropertiesSerializer;
import de.keksuccino.konkrete.properties.PropertiesSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class CinematicHandler {

    private static final Logger LOGGER = LogManager.getLogger("cinematica/CinematicHandler");

    protected static final File CINEMATICS_DIR = new File(Cinematica.MOD_DIRECTORY.getPath() + "/cinematics");
    protected static final File CINEMATICS_FILE = new File(CINEMATICS_DIR.getPath() + "/cinematics.properties");
    public static final File ONE_TIME_CINEMATICS_FILE = new File(Cinematica.CINEMATICA_INSTANCE_DATA_DIR.getPath() + "/one-time-cinematics.properties");

    protected static List<Cinematic> cinematics = new ArrayList<>();
    protected static Map<Cinematic, Long> triggeredCinematics = new HashMap<>();

    protected static List<String> triggeredOneTimeCinematics = new ArrayList<>();
    protected static List<Cinematic> cutsceneCinematicQueue = new ArrayList<>();
    protected static Cinematic currentCutscene = null;
    protected static Map<AudioClip, WorldMusicSuppressContext> activeAudioCinematicsThatStopWorldMusic = new HashMap<>();
    protected static Screen lastScreen = null;

    protected static boolean isInitialized = false;

    public static void init() {
        if (!isInitialized) {
            if (!CINEMATICS_DIR.isDirectory()) {
                CINEMATICS_DIR.mkdirs();
            }
            loadCinematics();
            loadOneTimeCinematics();
            MinecraftForge.EVENT_BUS.register(new CinematicHandler());
            isInitialized = true;
        }
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        try {

            for (Cinematic c : cinematics) {
                if (c.conditionsMet()) {
                    if (!triggeredCinematics.containsKey(c)) {
                        if (c.oneTimeCinematic) {
                            if (isTriggeredOneTimeCinematic(c)) {
                                continue;
                            } else {
                                addToTriggeredOneTimeCinematics(c);
                            }
                        }
                        triggeredCinematics.put(c, System.currentTimeMillis());
                    }
                }
            }

            this.handleTriggeredCinematics();

            this.handleSuppressWorldMusic();

            if (Minecraft.getInstance().screen == null) {
                currentCutscene = null;
            }

            if (!cutsceneCinematicQueue.isEmpty() && (currentCutscene == null)) {

                currentCutscene = cutsceneCinematicQueue.get(0);
                cutsceneCinematicQueue.remove(currentCutscene);

                VideoRenderer r = VideoHandler.getRenderer(currentCutscene.sourcePath);
                if (r != null) {
                    CutScene scene = new CutScene(r, currentCutscene.fadeInCutscene, currentCutscene.fadeOutCutscene);
                    scene.allowSkip = currentCutscene.allowCutsceneSkip;
                    Minecraft.getInstance().setScreen(scene);
                }

            }

            this.lastScreen = Minecraft.getInstance().screen;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected static void handleTriggeredCinematics() throws Exception {

        List<Cinematic> remove = new ArrayList<>();
        for (Map.Entry<Cinematic, Long> m : triggeredCinematics.entrySet()) {

            long start = m.getValue();
            long now = System.currentTimeMillis();
            long delay = (long) (m.getKey().triggerDelay * 1000);

            if ((start + delay) <= now) {

                remove.add(m.getKey());

                //Handle cutscene
                if (m.getKey().type == CinematicType.CUTSCENE) {
                    addToCutsceneQueue(m.getKey());
                }

                //Handle audio
                if (m.getKey().type == CinematicType.AUDIO) {
                    if ((Minecraft.getInstance().screen == null) || !Minecraft.getInstance().screen.getClass().getName().startsWith("de.keksuccino.cinematica.cutscene.")) {
                        AudioClip c = AudioCinematicHandler.getAudio(m.getKey().sourcePath);
                        if (c != null) {
                            if (m.getKey().stopWorldMusicOnAudio) {
                                if (!activeAudioCinematicsThatStopWorldMusic.containsKey(c)) {
                                    WorldMusicSuppressContext con = new WorldMusicSuppressContext();
                                    con.startTime = System.currentTimeMillis();
                                    activeAudioCinematicsThatStopWorldMusic.put(c, con);
                                }
                            }
                            c.stop();
                            c.play();
                        } else {
                            LOGGER.error("ERROR: Unable to start audio! File not found: " + m.getKey().sourcePath);
                        }
                    }
                }

            }

        }
        for (Cinematic c : remove) {
            triggeredCinematics.remove(c);
        }

    }

    protected static void handleSuppressWorldMusic() {

        if (!activeAudioCinematicsThatStopWorldMusic.isEmpty()) {

            Map<AudioClip, WorldMusicSuppressContext> clips = new HashMap<>();
            for (Map.Entry<AudioClip, WorldMusicSuppressContext> m : activeAudioCinematicsThatStopWorldMusic.entrySet()) {
                clips.put(m.getKey(), m.getValue());
            }
            for (Map.Entry<AudioClip, WorldMusicSuppressContext> m : clips.entrySet()) {
                if (!m.getValue().startedPlaying) {
                    if (m.getKey().playing()) {
                        m.getValue().startedPlaying = true;
                        VanillaAudioHandler.fadeOutAndSuppressWorldMusic();
                    }
                    long timeNow = System.currentTimeMillis();
                    if ((m.getValue().startTime + 10000) < timeNow) {
                        LOGGER.error("ERROR: Unable to handle world music for audio cinematic!");
                        activeAudioCinematicsThatStopWorldMusic.remove(m.getKey());
                    }
                } else {
                    if (!m.getKey().playing() && ((Minecraft.getInstance().screen == null) && (lastScreen == null))) {
                        activeAudioCinematicsThatStopWorldMusic.remove(m.getKey());
                    }
                }
            }

        } else if (VanillaAudioHandler.isSuppressWorldMusic() && (Minecraft.getInstance().screen == null)) {
            VanillaAudioHandler.setSuppressWorldMusic(false);
        }

    }

    public static void addCinematic(Cinematic cinematic) {
        if (!cinematics.contains(cinematic)) {
            cinematics.add(cinematic);
            saveCinematics();
        }
    }

    public static void removeCinematic(Cinematic cinematic) {
        if (cinematics.contains(cinematic)) {
            cinematics.remove(cinematic);
            saveCinematics();
        }
    }

    public static void clearCinematics() {
        cinematics.clear();
        saveCinematics();
    }

    public static List<Cinematic> getCinematics() {
        return cinematics;
    }

    public static void addToCutsceneQueue(Cinematic c) {
        if (c.type == CinematicType.CUTSCENE) {
            if (!cutsceneCinematicQueue.contains(c) && (currentCutscene != c)) {
                cutsceneCinematicQueue.add(c);
            }
        }
    }

    public static void removeFromCutsceneQueue(Cinematic c) {
        if (cutsceneCinematicQueue.contains(c)) {
            cutsceneCinematicQueue.remove(c);
        }
    }

    public static void addToTriggeredOneTimeCinematics(Cinematic cinematic) {
        if (!triggeredOneTimeCinematics.contains(cinematic.identifier)) {
            triggeredOneTimeCinematics.add(cinematic.identifier);
            saveOneTimeCinematics();
        }
    }

    public static void removeFromTriggeredOneTimeCinematics(Cinematic cinematic) {
        if (triggeredOneTimeCinematics.contains(cinematic.identifier)) {
            triggeredOneTimeCinematics.remove(cinematic.identifier);
            saveOneTimeCinematics();
        }
    }

    public static void clearTriggeredOneTimeCinematics() {
        for (Cinematic c : cinematics) {
            c.oncePerSessionTriggered = false;
        }
        triggeredOneTimeCinematics.clear();
        saveOneTimeCinematics();
    }

    public static List<String> getTriggeredOneTimeCinematics() {
        List<String> l = new ArrayList<>();
        l.addAll(triggeredOneTimeCinematics);
        return l;
    }

    public static boolean isTriggeredOneTimeCinematic(Cinematic cinematic) {
        return triggeredOneTimeCinematics.contains(cinematic.identifier);
    }

    protected static void loadOneTimeCinematics() {
        try {

            if (!ONE_TIME_CINEMATICS_FILE.isFile()) {
                saveOneTimeCinematics();
            }

            triggeredOneTimeCinematics.clear();

            PropertiesSet set = PropertiesSerializer.getProperties(ONE_TIME_CINEMATICS_FILE.getPath());
            if (set != null) {
                List<PropertiesSection> secs = set.getPropertiesOfType("cinematics");
                if (!secs.isEmpty()) {
                    PropertiesSection sec = secs.get(0);
                    for (Map.Entry<String, String> m : sec.getEntries().entrySet()) {
                        triggeredOneTimeCinematics.add(m.getKey());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void saveOneTimeCinematics() {
        try {

            if (!ONE_TIME_CINEMATICS_FILE.isFile()) {
                ONE_TIME_CINEMATICS_FILE.createNewFile();
            }

            PropertiesSet set = new PropertiesSet("one-time-cinematics");
            PropertiesSection sec = new PropertiesSection("cinematics");

            for (String s : triggeredOneTimeCinematics) {
                if (!sec.hasEntry(s)) {
                    sec.addEntry(s, "---");
                }
            }

            set.addProperties(sec);

            PropertiesSerializer.writeProperties(set, ONE_TIME_CINEMATICS_FILE.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void loadCinematics() {
        try {

            if (!CINEMATICS_FILE.isFile()) {
                CINEMATICS_FILE.createNewFile();
                PropertiesSerializer.writeProperties(new PropertiesSet("cinematica-cinematics"), CINEMATICS_FILE.getPath());
            }

            cinematics.clear();

            PropertiesSet set = PropertiesSerializer.getProperties(CINEMATICS_FILE.getPath());
            if (set != null) {
                for (PropertiesSection sec : set.getPropertiesOfType("cinematic-object")) {
                    Cinematic c = Cinematic.buildCinematicFromPropertiesSection(sec);
                    if (c != null) {
                        LOGGER.info("LOADING CINEMATIC: SOURCE: " + c.sourcePath + " | ID: " + c.getIdentifier() + " | NAME: " + c.name);
                        cinematics.add(c);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCinematics() {
        if (isInitialized) {
            try {

                if (!CINEMATICS_FILE.isFile()) {
                    CINEMATICS_FILE.createNewFile();
                }

                PropertiesSet set = new PropertiesSet("cinematica-cinematics");

                for (Cinematic c : cinematics) {
                    PropertiesSection sec = c.serializeToPropertiesSection();
                    set.addProperties(sec);
                }

                PropertiesSerializer.writeProperties(set, CINEMATICS_FILE.getPath());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.warn("WARNING: Tried to save cinematics before initializing the CinematicHandler!");
            for (StackTraceElement e : new Throwable().getStackTrace()) {
                LOGGER.warn("WARNING: " + e.toString());
            }
        }
    }

    public static void forceTriggerCinematic(Cinematic c, boolean ignoreOneTime) {
        if (cinematics.contains(c)) {
            if (!triggeredCinematics.containsKey(c)) {
                if (!ignoreOneTime) {
                    if (c.oneTimeCinematic) {
                        if (isTriggeredOneTimeCinematic(c)) {
                            return;
                        } else {
                            addToTriggeredOneTimeCinematics(c);
                        }
                    }
                    if (c.oncePerSessionCinematic) {
                        if (c.oncePerSessionTriggered) {
                            return;
                        } else {
                            c.oncePerSessionTriggered = true;
                        }
                    }
                }
                triggeredCinematics.put(c, System.currentTimeMillis());
            }
        }
    }

    public static class WorldMusicSuppressContext {
        public boolean startedPlaying = false;
        public long startTime = 0;
    }

}
