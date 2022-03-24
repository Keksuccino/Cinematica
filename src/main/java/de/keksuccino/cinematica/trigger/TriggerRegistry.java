package de.keksuccino.cinematica.trigger;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.konkrete.input.CharacterFilter;
import de.keksuccino.konkrete.properties.PropertiesSection;
import de.keksuccino.konkrete.properties.PropertiesSerializer;
import de.keksuccino.konkrete.properties.PropertiesSet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriggerRegistry {

    public static final File TRIGGERS_DIR = new File(Cinematica.MOD_DIRECTORY.getPath() + "/triggers");

    protected static Map<String, Trigger> triggers = new HashMap<>();

    public static void init() {

        if (!TRIGGERS_DIR.isDirectory()) {
            TRIGGERS_DIR.mkdirs();
        }

        TriggerHandler.init();

    }

    public static void registerTrigger(Trigger trigger) {
        if (trigger.getIdentifier() != null) {
            if (isValidTriggerIdentifier(trigger.getIdentifier())) {
                if (triggers.containsKey(trigger.getIdentifier())) {
                    Cinematica.LOGGER.warn("[CINEMATICA] WARNING: There is already a trigger registered with this identifier: " + trigger.getIdentifier());
                    Cinematica.LOGGER.warn("[CINEMATICA] WARNING: Overriding registered trigger with new one!");
                }
                loadCinematicsForTrigger(trigger);
                triggers.put(trigger.getIdentifier(), trigger);
            } else {
                Cinematica.LOGGER.error("[CINEMATICA] ERROR: Unable to register trigger: " + trigger.getIdentifier());
                Cinematica.LOGGER.error("[CINEMATICA] ERROR: Trigger uses unallowed characters in its identifier! Only basic chars (a-z, 0-9, -_) are supported!");
            }
        } else {
            Cinematica.LOGGER.error("[CINEMATICA] ERROR: Unable to register trigger with NULLED identifier!");
        }
    }

    public static boolean isValidTriggerIdentifier(String identifier) {
        CharacterFilter f = CharacterFilter.getBasicFilenameCharacterFilter();
        if (identifier.equals(f.filterForAllowedChars(identifier))) {
            return true;
        }
        return false;
    }

    public static void unregisterTrigger(String identifier) {
        triggers.remove(identifier);
    }

    public static Trigger getTrigger(String identifier) {
        return triggers.get(identifier);
    }

    public static List<Trigger> getTriggers() {
        List<Trigger> l = new ArrayList<>();
        l.addAll(triggers.values());
        return l;
    }

    public static void loadCinematicsForTrigger(Trigger trigger) {
        try {
            File f = new File(TRIGGERS_DIR.getPath() + "/" + trigger.getIdentifier() + ".properties");
            if (!f.isFile()) {
                saveCinematicsForTrigger(trigger);
            }
            PropertiesSet set = PropertiesSerializer.getProperties(f.getPath());
            if (set != null) {
                for (PropertiesSection sec : set.getPropertiesOfType("cinematic-object")) {
                    Cinematic.SerializedCinematic sc = Cinematic.buildSerializedCinematic(sec);
                    if (sc != null) {
                        Cinematic c = trigger.createCinematicFromSerializedObject(sc);
                        if (c != null) {
                            trigger.addCinematic(c);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCinematicsForTrigger(Trigger trigger) {
        try {
            File f = new File(TRIGGERS_DIR.getPath() + "/" + trigger.getIdentifier() + ".properties");
            if (!f.isFile()) {
                f.createNewFile();
            }
            PropertiesSet set = new PropertiesSet("cinematica-trigger");
            for (Cinematic c : trigger.cinematics) {
                set.addProperties(c.serializeToPropertiesSection());
            }
            PropertiesSerializer.writeProperties(set, f.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
