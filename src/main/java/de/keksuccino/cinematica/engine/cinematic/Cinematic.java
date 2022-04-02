package de.keksuccino.cinematica.engine.cinematic;

import de.keksuccino.cinematica.cutscene.CutScene;
import de.keksuccino.cinematica.cutscene.CutScenePauseMenu;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.ConditionFactoryRegistry;
import de.keksuccino.cinematica.utils.MiscUtils;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cinematic {

    protected final String identifier;

    public final CinematicType type;
    public String sourcePath;
    protected List<Condition> conditions = new ArrayList<>();

    /** The display name of this cinematic **/
    public String name = "";
    /** If this cinematic should only trigger one time **/
    public boolean oneTimeCinematic = false;
    /** If this cinematic should only trigger once per game session **/
    public boolean oncePerSessionCinematic = false;
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

    protected boolean oncePerSessionTriggered = false;

    public Cinematic(@Nullable String identifier, CinematicType type, String cinematicSource) {
        if (identifier != null) {
            this.identifier = identifier;
        } else {
            this.identifier = MiscUtils.generateRandomUniqueId();
        }
        this.type = type;
        this.sourcePath = cinematicSource;
    }

    /**
     * Used to check if this cinematic should trigger.<br><br>
     */
    public boolean conditionsMet() {
        Screen cur = Minecraft.getInstance().currentScreen;
        if (cur != null) {
            if ((cur instanceof IngameMenuScreen) || (cur instanceof CutScene) || (cur instanceof CutScenePauseMenu)) {
                return false;
            }
        }
        if (this.conditions.isEmpty()) {
            return false;
        }
        for (Condition c : this.conditions) {
            if (!c.conditionsMet()) {
                return false;
            }
        }
        if (this.oncePerSessionCinematic) {
            if (this.oncePerSessionTriggered == true) {
                return false;
            }
            this.oncePerSessionTriggered = true;
        } else {
            this.oncePerSessionTriggered = false;
        }
        return true;
    }

    public void addCondition(Condition c) {
        if (!this.conditions.contains(c)) {
            this.conditions.add(c);
            if (CinematicHandler.isInitialized()) {
                CinematicHandler.saveCinematics();
            }
        }
    }

    public void overrideConditions(List<Condition> conditionsList) {
        if (conditionsList != null) {
            this.conditions = conditionsList;
            if (CinematicHandler.isInitialized()) {
                CinematicHandler.saveCinematics();
            }
        }
    }

    public void removeCondition(Condition c) {
        if (this.conditions.contains(c)) {
            this.conditions.remove(c);
            if (CinematicHandler.isInitialized()) {
                CinematicHandler.saveCinematics();
            }
        }
    }

    public void removeConditionById(String identifier) {
        for (Condition con : this.conditions) {
            if (con.getIdentifier().equals(identifier)) {
                this.removeCondition(con);
                if (CinematicHandler.isInitialized()) {
                    CinematicHandler.saveCinematics();
                }
                break;
            }
        }
    }

    public void removeAllConditions() {
        this.conditions.clear();
        if (CinematicHandler.isInitialized()) {
            CinematicHandler.saveCinematics();
        }
    }

    public List<Condition> getConditions() {
        return this.conditions;
    }

    public PropertiesSection serializeToPropertiesSection() {

        PropertiesSection sec = new PropertiesSection("cinematic-object");

        sec.addEntry("cinematic_identifier", this.identifier);
        sec.addEntry("cinematic_source", this.sourcePath);
        if (this.name != null) {
            sec.addEntry("name", this.name);
        } else {
            sec.addEntry("name", "");
        }
        sec.addEntry("allow_skip", "" + this.allowCutsceneSkip);
        sec.addEntry("type", this.type.getName());
        sec.addEntry("one_time_cinematic", "" + this.oneTimeCinematic);
        sec.addEntry("once_per_session_cinematic", "" + this.oncePerSessionCinematic);
        sec.addEntry("trigger_delay", "" + this.triggerDelay);
        sec.addEntry("fade_in", "" + this.fadeInCutscene);
        sec.addEntry("fade_out", "" + this.fadeOutCutscene);
        sec.addEntry("stop_world_music", "" + this.stopWorldMusicOnAudio);

        String conString = "";
        for (Condition c : this.conditions) {
            for (Map.Entry<String, String> m : c.conditionMeta.getEntries().entrySet()) {
                sec.addEntry("condition-meta:" + c.getIdentifier() + ":" + m.getKey(), m.getValue());
            }
            conString += c.parent.getIdentifier() + ":" + c.getIdentifier() + ";";
        }
        sec.addEntry("conditions", conString);

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

        String nameString = serializedObject.getEntryValue("name");
        if ((nameString != null) && !nameString.replace(" ", "").equals("")) {
            sc.name = nameString;
        }

        String allowSkipString = serializedObject.getEntryValue("allow_skip");
        if ((allowSkipString != null) && allowSkipString.equals("false")) {
            sc.allowCutsceneSkip = false;
        }

        String oneTimeString = serializedObject.getEntryValue("one_time_cinematic");
        if ((oneTimeString != null) && oneTimeString.equals("true")) {
            sc.oneTimeCinematic = true;
        }

        String oncePerSessionString = serializedObject.getEntryValue("once_per_session_cinematic");
        if ((oncePerSessionString != null) && oncePerSessionString.equals("true")) {
            sc.oncePerSessionCinematic = true;
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

        String conString = serializedObject.getEntryValue("conditions");
        if ((conString != null) && conString.contains(";")) {
            String[] cons = conString.split("[;]");
            for (String s : cons) {
                if (s.contains(":")) {

                    String factoryIdentifier = s.split("[:]", 2)[0];
                    String conIdentifier = s.split("[:]", 2)[1];

                    Condition.SerializedCondition sCon = new Condition.SerializedCondition(conIdentifier, factoryIdentifier);
                    PropertiesSection sec = new PropertiesSection("condition-meta");
                    for (Map.Entry<String, String> m : serializedObject.getEntries().entrySet()) {
                        String conMetaPrefix = "condition-meta:" + conIdentifier + ":";
                        if (m.getKey().startsWith(conMetaPrefix)) {
                            String key = m.getKey().split("[:]", 3)[2];
                            if ((key != null) && key.length() > 0) {
                                sec.addEntry(key, m.getValue());
                            }
                        }
                    }

                    sCon.conditionMeta = sec;
                    sc.conditions.add(sCon);

                }
            }
        }

        return sc;

    }

    public static Cinematic createCinematicFromSerializedCinematic(Cinematic.SerializedCinematic serialized) {

        Cinematic c = new Cinematic(serialized.identifier, serialized.type, serialized.sourcePath);

        c.name = serialized.name;
        c.allowCutsceneSkip = serialized.allowCutsceneSkip;
        c.triggerDelay = serialized.triggerDelay;
        c.oneTimeCinematic = serialized.oneTimeCinematic;
        c.oncePerSessionCinematic = serialized.oncePerSessionCinematic;
        c.fadeInCutscene = serialized.fadeInCutscene;
        c.fadeOutCutscene = serialized.fadeOutCutscene;
        c.stopWorldMusicOnAudio = serialized.stopWorldMusicOnAudio;

        for (Condition.SerializedCondition serializedCon : serialized.conditions) {
            ConditionFactory cf = ConditionFactoryRegistry.getFactory(serializedCon.factoryIdentifier);
            if (cf != null) {
                Condition deserializedCon = cf.createConditionFromSerializedObject(serializedCon);
                if (deserializedCon != null) {
                    c.addCondition(deserializedCon);
                }
            }
        }

        return c;

    }

    public static Cinematic buildCinematicFromPropertiesSection(PropertiesSection serializedCinematic) {
        return createCinematicFromSerializedCinematic(buildSerializedCinematic(serializedCinematic));
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void saveChanges() {
        CinematicHandler.saveCinematics();
    }

    public static class SerializedCinematic {

        public final String identifier;
        public final CinematicType type;
        public String sourcePath;
        public List<Condition.SerializedCondition> conditions = new ArrayList<>();
        public String name = "";
        public boolean allowCutsceneSkip = true;
        public boolean oneTimeCinematic = false;
        public boolean oncePerSessionCinematic = false;
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
