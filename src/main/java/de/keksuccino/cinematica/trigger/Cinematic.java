package de.keksuccino.cinematica.trigger;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.utils.MiscUtils;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class Cinematic {

    protected final String identifier;

    public final Trigger parent;
    public final CinematicType type;
    public String sourcePath;
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

    public Cinematic(@Nullable String identifier, Trigger parent, CinematicType type, String cinematicSource, PropertiesSection conditionMeta) {
        if (identifier != null) {
            this.identifier = identifier;
        } else {
            this.identifier = MiscUtils.generateRandomUniqueId();
        }
        this.parent = parent;
        this.type = type;
        this.sourcePath = cinematicSource;
        this.conditionMeta = conditionMeta;
    }

    /**
     * Used to check if the condition meta of this cinematic meets all conditions of the trigger context.<br><br>
     *
     * Is only called when the parent {@link Trigger} is getting triggered.
     */
    public abstract boolean conditionsMet(PropertiesSection triggerContext);

    public boolean conditionsMetInternal(PropertiesSection triggerContext) {

        //TODO do default conditions here

        return false;

    }

    public PropertiesSection serializeToPropertiesSection() {

        PropertiesSection sec = new PropertiesSection("cinematic-object");

        sec.addEntry("cinematic_identifier", this.identifier);
        sec.addEntry("cinematic_source", this.sourcePath);
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

    public SerializedCinematic serialize() {
        return buildSerializedCinematic(serializeToPropertiesSection());
    }

    public static SerializedCinematic buildSerializedCinematic(PropertiesSection serializedObject) {

        String id = serializedObject.getEntryValue("cinematic_identifier");
        if (id == null) {
            id = MiscUtils.generateRandomUniqueId();
        }

        String typeString = serializedObject.getEntryValue("type");
        CinematicType cinType = CinematicType.CUTSCENE;
        if (typeString != null) {
            cinType = CinematicType.getByName(typeString);
            if (cinType == null) {
                cinType = CinematicType.CUTSCENE;
            }
        }

        SerializedCinematic sc = new SerializedCinematic(id, cinType);

        sc.sourcePath = serializedObject.getEntryValue("cinematic_source");

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

    public String getIdentifier() {
        return this.identifier;
    }

    public static class SerializedCinematic {

        public final String identifier;
        public final CinematicType type;
        public String sourcePath;
        public PropertiesSection conditionMeta;
        public boolean allowCutsceneSkip = true;
        public boolean oneTimeCinematic = false;
        public double triggerDelay = 0D;
        public boolean fadeInCutscene = true;
        public boolean fadeOutCutscene = true;
        public boolean stopWorldMusicOnAudio = false;

        public SerializedCinematic(@Nullable String identifier, CinematicType type) {
            if (identifier != null) {
                this.identifier = identifier;
            } else {
                this.identifier = MiscUtils.generateRandomUniqueId();
            }
            this.type = type;
        }

    }

}
