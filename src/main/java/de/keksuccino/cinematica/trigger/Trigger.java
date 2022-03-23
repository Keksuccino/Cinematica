package de.keksuccino.cinematica.trigger;

import de.keksuccino.auudio.audio.AudioClip;
import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.audio.AudioCinematicHandler;
import de.keksuccino.cinematica.audio.VanillaAudioHandler;
import de.keksuccino.cinematica.gui.EditCinematicScreen;
import de.keksuccino.cinematica.ui.popup.CinematicaTextInputPopup;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Trigger {

    protected final String identifier;
    protected List<Cinematic> cinematics = new ArrayList<>();
    protected Map<Cinematic, Long> triggeredCinematics = new HashMap<>();

    protected Map<AudioClip, WorldMusicSuppressContext> activeAudioCinematicsThatStopWorldMusic = new HashMap<>();
    protected Screen lastScreen = null;

    public Trigger(String uniqueIdentifier) {
        this.identifier = uniqueIdentifier;
    }

    /**
     * Needs to be called when the trigger should get triggered.
     */
    public void trigger(PropertiesSection conditionMeta) {
        try {
            for (Cinematic c : this.cinematics) {
                if (c.conditionsMet(conditionMeta)) {
                    if (!triggeredCinematics.containsKey(c)) {
                        if (c.oneTimeCinematic) {
                            if (CinematicHandler.isTriggeredOneTimeCinematic(c)) {
                                continue;
                            } else {
                                CinematicHandler.addToTriggeredOneTimeCinematics(c);
                            }
                        }
                        triggeredCinematics.put(c, System.currentTimeMillis());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The ticker for this trigger.<br>
     * Is called every client tick.<br><br>
     *
     * If you override this, don't forget to call {@code super.tick()}!
     */
    public void tick() {
        try {

            this.handleTriggeredCinematics();

            this.handleSuppressWorldMusic();

            this.lastScreen = Minecraft.getInstance().currentScreen;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void handleTriggeredCinematics() throws Exception {

        List<Cinematic> remove = new ArrayList<>();
        for (Map.Entry<Cinematic, Long> m : triggeredCinematics.entrySet()) {

            long start = m.getValue();
            long now = System.currentTimeMillis();
            long delay = (long) (m.getKey().triggerDelay * 1000);

            if ((start + delay) <= now) {

                remove.add(m.getKey());

                //Handle cutscene
                if (m.getKey().type == CinematicType.CUTSCENE) {
                    CinematicHandler.addToCutsceneQueue(m.getKey());
                }

                //Handle audio
                if (m.getKey().type == CinematicType.AUDIO) {
                    if ((Minecraft.getInstance().currentScreen == null) || !Minecraft.getInstance().currentScreen.getClass().getName().startsWith("de.keksuccino.cinematica.cutscene.")) {
                        AudioClip c = AudioCinematicHandler.getAudio(m.getKey().cinematicSource);
                        if (c != null) {
                            if (m.getKey().stopWorldMusicOnAudio) {
                                if (!this.activeAudioCinematicsThatStopWorldMusic.containsKey(c)) {
                                    WorldMusicSuppressContext con = new WorldMusicSuppressContext();
                                    con.startTime = System.currentTimeMillis();
                                    this.activeAudioCinematicsThatStopWorldMusic.put(c, con);
                                }
                            }
                            if (!c.isAudioReady()) {
                                c.tryPlayWhenReady();
                            } else {
                                if (!c.isPlaying()) {
                                    c.stop();
                                    c.restart();
                                    c.play();
                                } else {
                                    c.restart();
                                }
                            }
                        } else {
                            Cinematica.LOGGER.error("[CINEMATICA] ERROR: Unable to start audio! File not found: " + m.getKey().cinematicSource);
                        }
                    }
                }

            }

        }
        for (Cinematic c : remove) {
            triggeredCinematics.remove(c);
        }

    }

    protected void handleSuppressWorldMusic() {

        if (!this.activeAudioCinematicsThatStopWorldMusic.isEmpty()) {

            Map<AudioClip, WorldMusicSuppressContext> clips = new HashMap<>();
            for (Map.Entry<AudioClip, WorldMusicSuppressContext> m : this.activeAudioCinematicsThatStopWorldMusic.entrySet()) {
                clips.put(m.getKey(), m.getValue());
            }
            for (Map.Entry<AudioClip, WorldMusicSuppressContext> m : clips.entrySet()) {
                if (!m.getValue().startedPlaying) {
                    if (m.getKey().isAudioReady() && m.getKey().isPlaying()) {
                        m.getValue().startedPlaying = true;
                        VanillaAudioHandler.fadeOutAndSuppressWorldMusic();
                    }
                    long timeNow = System.currentTimeMillis();
                    if ((m.getValue().startTime + 10000) < timeNow) {
                        Cinematica.LOGGER.error("[CINEMATICA] ERROR: Unable to handle world music for playing audio cinematic!");
                        this.activeAudioCinematicsThatStopWorldMusic.remove(m.getKey());
                    }
                } else {
                    if (!m.getKey().isPlaying() && ((Minecraft.getInstance().currentScreen == null) && (lastScreen == null))) {
                        this.activeAudioCinematicsThatStopWorldMusic.remove(m.getKey());
                    }
                }
            }

        } else if (VanillaAudioHandler.isSuppressWorldMusic() && (Minecraft.getInstance().currentScreen == null)) {
            VanillaAudioHandler.setSuppressWorldMusic(false);
        }

    }

    public void addCinematic(Cinematic child) {
        if (!this.cinematics.contains(child)) {
            this.cinematics.add(child);
        }
    }

    public void removeCinematic(Cinematic child) {
        if (this.cinematics.contains(child)) {
            this.cinematics.remove(child);
        }
    }

    public void clearCinematics() {
        this.cinematics.clear();
    }

    public List<Cinematic> getCinematics() {
        return this.cinematics;
    }

    /**
     * Create an instance of a cinematic out of a {@link de.keksuccino.cinematica.trigger.Cinematic.SerializedCinematic}.<br>
     * This is used to load saved cinematics on mod init.
     */
    public abstract Cinematic createCinematicFromSerializedObject(Cinematic.SerializedCinematic serializedCinematic);

    /**
     * Gets called when the "Add Cinematic" button in the UI is getting clicked (to add a new cinematic of this type).<br>
     * Can be used to open a popup with settings (for example).<br><br>
     *
     * You need to manually add the new {@link Cinematic} to the {@link Trigger#cinematics} list here!<br>
     * You need to manually sync changes made to cinematics by calling {@link Trigger#saveChanges()}!
     */
    public abstract void onAddCinematicButtonClick(Screen parentScreen, CinematicType type);

    /**
     * Gets called when the button to edit a cinematic is getting clicked.<br>
     * Can be used to open a popup with settings (for example).<br><br>
     *
     * You need to manually sync changes made to cinematics by calling {@link Trigger#saveChanges()}!
     */
    public abstract void onEditCinematicButtonClick(Screen parentScreen, Cinematic cinematic);

    /**
     * Used by the default edit/create cinematic screen.
     */
    public void onConditionValueButtonClick(AdvancedButton parentBtn, EditCinematicScreen parentScreen) {
        CinematicaTextInputPopup p = new CinematicaTextInputPopup(new Color(0, 0, 0, 0), parentBtn.getMessageString(), null, 240, (call) -> {
            if (call != null) {
                if (call.replace(" ", "").equals("")) {
                    parentScreen.setConditionValue(null);
                } else {
                    parentScreen.setConditionValue(call);
                }
            }
        });
        if (parentScreen.getConditionValue() != null) {
            p.setText(parentScreen.getConditionValue());
        } else if (parentScreen.getTrigger().getConditionValueExample() != null) {
            p.setText(parentScreen.getTrigger().getConditionValueExample());
        }
        PopupHandler.displayPopup(p);
    }

    /**
     * Used by the default select/choose trigger screen.
     */
    public abstract String getDisplayName();

    /**
     * Used by the default select/choose trigger screen.
     */
    public abstract List<String> getDescription();

    /**
     * Used by the default edit/create cinematic screen.
     */
    public String getConditionValueButtonDisplayName() {
        return Locals.localize("cinematica.cinematic.conditionvalue.set");
    }

    /**
     * Used by the default cinematic edit/creation screen.
     */
    public abstract List<String> getConditionValueDescription();

    /**
     * Used by the default cinematic edit/creation screen.
     */
    public abstract String getConditionValueExample();

    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Call this every time you add, remove or edit cinematics.
     */
    public void saveChanges() {
        TriggerRegistry.saveCinematicsForTrigger(this);
    }

    public static class WorldMusicSuppressContext {
        public boolean startedPlaying = false;
        public long startTime = 0;
    }

}
