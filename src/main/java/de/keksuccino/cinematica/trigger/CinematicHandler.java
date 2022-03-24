package de.keksuccino.cinematica.trigger;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.audio.AudioCinematicHandler;
import de.keksuccino.cinematica.cutscene.CutScene;
import de.keksuccino.cinematica.video.VideoHandler;
import de.keksuccino.cinematica.video.VideoRenderer;
import de.keksuccino.konkrete.properties.PropertiesSection;
import de.keksuccino.konkrete.properties.PropertiesSerializer;
import de.keksuccino.konkrete.properties.PropertiesSet;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CinematicHandler {

    public static final File ONE_TIME_CINEMATICS_FILE = new File(Cinematica.CINEMATICA_TEMP_DIR.getPath() + "/one-time-cinematics.properties");
    protected static List<String> triggeredOneTimeCinematics = new ArrayList<>();

    protected static List<Cinematic> cutsceneCinematicQueue = new ArrayList<>();
    protected static Cinematic currentCutscene = null;

    public static void init() {
        readFromOneTimeFile();
        MinecraftForge.EVENT_BUS.register(new CinematicHandler());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        try {

            if (Minecraft.getInstance().currentScreen == null) {
                currentCutscene = null;
            }

            if (!cutsceneCinematicQueue.isEmpty() && (currentCutscene == null)) {

                currentCutscene = cutsceneCinematicQueue.get(0);
                cutsceneCinematicQueue.remove(currentCutscene);

                VideoRenderer r = VideoHandler.getRenderer(currentCutscene.sourcePath);
                if (r != null) {
                    AudioCinematicHandler.stopAll();
                    CutScene scene = new CutScene(r, currentCutscene.fadeInCutscene, currentCutscene.fadeOutCutscene);
                    scene.allowSkip = currentCutscene.allowCutsceneSkip;
                    Minecraft.getInstance().displayGuiScreen(scene);
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
            writeToOneTimeFile();
        }
    }

    public static void removeFromTriggeredOneTimeCinematics(Cinematic cinematic) {
        if (triggeredOneTimeCinematics.contains(cinematic.identifier)) {
            triggeredOneTimeCinematics.remove(cinematic.identifier);
            writeToOneTimeFile();
        }
    }

    public static void clearTriggeredOneTimeCinematics() {
        triggeredOneTimeCinematics.clear();
        writeToOneTimeFile();
    }

    public static List<String> getTriggeredOneTimeCinematics() {
        List<String> l = new ArrayList<>();
        l.addAll(triggeredOneTimeCinematics);
        return l;
    }

    public static boolean isTriggeredOneTimeCinematic(Cinematic cinematic) {
        return triggeredOneTimeCinematics.contains(cinematic.identifier);
    }

    protected static void readFromOneTimeFile() {
        try {

            if (!ONE_TIME_CINEMATICS_FILE.isFile()) {
                writeToOneTimeFile();
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

    protected static void writeToOneTimeFile() {
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

}
