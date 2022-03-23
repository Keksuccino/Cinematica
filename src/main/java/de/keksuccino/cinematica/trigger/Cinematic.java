package de.keksuccino.cinematica.trigger;

import de.keksuccino.cinematica.utils.MiscUtils;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import java.util.Map;

public abstract class Cinematic {

    protected String identifier = MiscUtils.generateRandomUniqueId();

    public final Trigger parent;
    public final CinematicType type;
    public String cinematicSource;
    public PropertiesSection conditionMeta;

    /** If this cinematic should only trigger one time **/
    public boolean oneTimeCinematic = false;
    /** The delay in seconds before this cinematic starts playing **/
    public double triggerDelay = 0D;
    /** If cutscenes should be skip-able or not **/
    public boolean allowCutsceneSkip = true;
    /** If cutscenes should fade-in **/
    public boolean fadeInCutscene = true;
    /** If cutscenes should fade-out **/
    public boolean fadeOutCutscene = true;
    /** If world music should stop while audio cinematics are playing **/
    public boolean stopWorldMusicOnAudio = false;

    public Cinematic(Trigger parent, CinematicType type, String cinematicSource, PropertiesSection conditionMeta) {
        this.parent = parent;
        this.type = type;
        this.cinematicSource = cinematicSource;
        this.conditionMeta = conditionMeta;
    }

    /**
     * Used to check if the condition meta of this cinematic meets all conditions of the parent condition meta.<br><br>
     *
     * Is only called when the parent {@link Trigger} is getting triggered.
     */
    public abstract boolean conditionsMet(PropertiesSection parentConditionMeta);

    public PropertiesSection serializeToPropertiesSection() {

        PropertiesSection sec = new PropertiesSection("cinematic-object");

        sec.addEntry("cinematic_identifier", this.identifier);
        sec.addEntry("cinematic_source", this.cinematicSource);
        sec.addEntry("allow_skip", "" + this.allowCutsceneSkip);
        sec.addEntry("type", this.type.getName());
        sec.addEntry("one_time_cinematic", "" + this.oneTimeCinematic);
        sec.addEntry("trigger_delay", "" + this.triggerDelay);
        sec.addEntry("fade_in", "" + this.fadeInCutscene);
        sec.addEntry("fade_out", "" + this.fadeOutCutscene);
        sec.addEntry("stop_world_music", "" + this.stopWorldMusicOnAudio);

        for (Map.Entry<String, String> m : this.conditionMeta.getEntries().entrySet()) {
            sec.addEntry("condition_meta_value:" + m.getKey(), m.getValue());
        }

        return sec;

    }

    public String getIdentifier() {
        return this.identifier;
    }

    public static SerializedCinematic buildSerializedCinematic(PropertiesSection serializedObject) {

        SerializedCinematic sc = new SerializedCinematic();

        String id = serializedObject.getEntryValue("cinematic_identifier");
        if (id != null) {
            sc.identifier = id;
        } else {
            sc.identifier = MiscUtils.generateRandomUniqueId();
        }

        sc.cinematicSource = serializedObject.getEntryValue("cinematic_source");

        String typeString = serializedObject.getEntryValue("type");
        if (typeString != null) {
            sc.type = CinematicType.getByName(typeString);
            if (sc.type == null) {
                sc.type = CinematicType.CUTSCENE;
            }
        }

        String allowSkipString = serializedObject.getEntryValue("allow_skip");
        if ((allowSkipString != null) && allowSkipString.equals("false")) {
            sc.allowCutsceneSkip = false;
        }

        String oneTimeString = serializedObject.getEntryValue("one_time_cinematic");
        if ((oneTimeString != null) && oneTimeString.equals("true")) {
            sc.oneTimeCinematic = true;
        }

        String triggerDelayString = serializedObject.getEntryValue("trigger_delay");
        if ((triggerDelayString != null) && MathUtils.isDouble(triggerDelayString)) {
            sc.triggerDelay = Double.parseDouble(triggerDelayString);
        }

        String fadeInString = serializedObject.getEntryValue("fade_in");
        if ((fadeInString != null) && fadeInString.equals("false")) {
            sc.fadeInCutscene = false;
        }

        String fadeOutString = serializedObject.getEntryValue("fade_out");
        if ((fadeOutString != null) && fadeOutString.equals("false")) {
            sc.fadeOutCutscene = false;
        }

        String stopWorldMusicString = serializedObject.getEntryValue("stop_world_music");
        if ((stopWorldMusicString != null) && stopWorldMusicString.equals("true")) {
            sc.stopWorldMusicOnAudio = true;
        }

        PropertiesSection conMeta = new PropertiesSection("condition-meta");
        for (Map.Entry<String, String> m : serializedObject.getEntries().entrySet()) {
            if (m.getKey().startsWith("condition_meta_value:")) {
                String key = m.getKey().split(":", 2)[1];
                conMeta.addEntry(key, m.getValue());
            }
        }
        sc.conditionMeta = conMeta;

        return sc;

    }

    public static class SerializedCinematic {

        public String identifier;
        public CinematicType type;
        public String cinematicSource;
        public PropertiesSection conditionMeta;
        public boolean allowCutsceneSkip = true;
        public boolean oneTimeCinematic = false;
        public double triggerDelay = 0D;
        public boolean fadeInCutscene = true;
        public boolean fadeOutCutscene = true;
        public boolean stopWorldMusicOnAudio = false;

    }

}
