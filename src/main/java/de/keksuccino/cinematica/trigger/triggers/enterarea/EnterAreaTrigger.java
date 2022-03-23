package de.keksuccino.cinematica.trigger.triggers.enterarea;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.gui.EditCinematicScreen;
import de.keksuccino.cinematica.trigger.Cinematic;
import de.keksuccino.cinematica.trigger.CinematicHandler;
import de.keksuccino.cinematica.trigger.CinematicType;
import de.keksuccino.cinematica.trigger.Trigger;
import de.keksuccino.cinematica.utils.WorldUtils;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class EnterAreaTrigger extends Trigger {

    public EnterAreaTrigger() {
        super("cinematica_trigger_enter_area");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerWalk(TickEvent.ClientTickEvent e) {
        if (Minecraft.getInstance().player != null) {
            BlockPos pos = Minecraft.getInstance().player.getPosition();
            PropertiesSection sec = new PropertiesSection("condition-meta");
            sec.addEntry("player_coordinates", pos.getX() + "," + pos.getY() + "," + pos.getZ());
            String dim = WorldUtils.getCurrentDimensionKey();
            if (dim == null) {
                dim = "";
            }
            sec.addEntry("dimension", dim);
            this.trigger(sec);
        }
    }

    @Override
    public Cinematic createCinematicFromSerializedObject(Cinematic.SerializedCinematic serializedCinematic) {
        EnterAreaCinematic c = new EnterAreaCinematic(
                this,
                serializedCinematic.type,
                serializedCinematic.cinematicSource,
                serializedCinematic.conditionMeta);
        c.allowCutsceneSkip = serializedCinematic.allowCutsceneSkip;
        c.triggerDelay = serializedCinematic.triggerDelay;
        c.oneTimeCinematic = serializedCinematic.oneTimeCinematic;
        c.fadeInCutscene = serializedCinematic.fadeInCutscene;
        c.fadeOutCutscene = serializedCinematic.fadeOutCutscene;
        c.stopWorldMusicOnAudio = serializedCinematic.stopWorldMusicOnAudio;
        return c;
    }

    @Override
    public void onAddCinematicButtonClick(Screen parentScreen, CinematicType type) {
        EditCinematicScreen s = new EditCinematicScreen(parentScreen, type, this, (call) -> {
            if (call != null) {
                if ((call.conditionValue != null) && !call.conditionValue.replace(" ", "").equals("")) {

                    PropertiesSection sec = new PropertiesSection("condition-meta");
                    String[] values = call.conditionValue.split("[;]");
                    if (values.length >= 3) {
                        sec.addEntry("from_coordinates", values[0]);
                        sec.addEntry("to_coordinates", values[1]);
                        sec.addEntry("dimension", values[2]);
                    }

                    EnterAreaCinematic c = new EnterAreaCinematic(this, call.type, call.sourcePath, sec);
                    c.allowCutsceneSkip = call.allowCutsceneSkip;
                    c.triggerDelay = call.triggerDelay;
                    c.oneTimeCinematic = call.onTimeCinematic;
                    c.fadeInCutscene = call.fadeInCutscene;
                    c.fadeOutCutscene = call.fadeOutCutscene;
                    c.stopWorldMusicOnAudio = call.stopWorldMusicOnAudio;

                    this.addCinematic(c);
                    this.saveChanges();

                }
            }
        });
        Minecraft.getInstance().displayGuiScreen(s);
    }

    @Override
    public void onEditCinematicButtonClick(Screen parentScreen, Cinematic cinematic) {
        EditCinematicScreen s = new EditCinematicScreen(parentScreen, cinematic.type, this, (call) -> {
            if (call != null) {

                cinematic.cinematicSource = call.sourcePath;

                PropertiesSection sec = new PropertiesSection("condition-meta");
                String[] values = call.conditionValue.split("[;]");
                if (values.length >= 3) {
                    sec.addEntry("from_coordinates", values[0]);
                    sec.addEntry("to_coordinates", values[1]);
                    sec.addEntry("dimension", values[2]);
                }
                cinematic.conditionMeta = sec;

                cinematic.allowCutsceneSkip = call.allowCutsceneSkip;
                cinematic.triggerDelay = call.triggerDelay;
                cinematic.oneTimeCinematic = call.onTimeCinematic;
                if (!cinematic.oneTimeCinematic && CinematicHandler.isTriggeredOneTimeCinematic(cinematic)) {
                    CinematicHandler.removeFromTriggeredOneTimeCinematics(cinematic);
                }
                cinematic.fadeInCutscene = call.fadeInCutscene;
                cinematic.fadeOutCutscene = call.fadeOutCutscene;
                cinematic.stopWorldMusicOnAudio = call.stopWorldMusicOnAudio;

                this.saveChanges();

            }
        });
        s.setSourcePath(cinematic.cinematicSource);
        s.setAllowSkip(cinematic.allowCutsceneSkip);
        s.setTriggerDelay(cinematic.triggerDelay);
        s.setOneTimeCinematic(cinematic.oneTimeCinematic);
        s.setFadeInCutscene(cinematic.fadeInCutscene);
        s.setFadeOutCutscene(cinematic.fadeOutCutscene);
        s.setStopWorldMusicOnAudio(cinematic.stopWorldMusicOnAudio);

        //Condition meta converted to a string
        s.setConditionValue(cinematic.conditionMeta.getEntryValue("from_coordinates") + ";" + cinematic.conditionMeta.getEntryValue("to_coordinates") + ";" + cinematic.conditionMeta.getEntryValue("dimension"));

        Minecraft.getInstance().displayGuiScreen(s);
    }

    @Override
    public void onConditionValueButtonClick(AdvancedButton parentBtn, EditCinematicScreen parentScreen) {
        EnterAreaCoordinatesScreen s = new EnterAreaCoordinatesScreen(parentScreen, parentScreen.getConditionValue(), (call) -> {
            if (call != null) {
                parentScreen.setConditionValue(call);
            }
        });
        Minecraft.getInstance().displayGuiScreen(s);
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.trigger.enterarea");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.trigger.enterarea.desc"), "%n%"));
    }

    @Override
    public String getConditionValueButtonDisplayName() {
        return Locals.localize("cinematica.trigger.enterarea.definearea");
    }

    @Override
    public List<String> getConditionValueDescription() {
        return null;
    }

    @Override
    public String getConditionValueExample() {
        return null;
    }

}
