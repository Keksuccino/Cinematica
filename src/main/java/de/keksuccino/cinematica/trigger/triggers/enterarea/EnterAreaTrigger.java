package de.keksuccino.cinematica.trigger.triggers.enterarea;

import de.keksuccino.cinematica.gui.EditCinematicScreen;
import de.keksuccino.cinematica.trigger.Cinematic;
import de.keksuccino.cinematica.trigger.Trigger;
import de.keksuccino.cinematica.utils.WorldUtils;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
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
            PropertiesSection sec = new PropertiesSection("trigger-context");
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
    public Cinematic createCinematicFromSerializedObject(Cinematic.SerializedCinematic serialized) {
        EnterAreaCinematic c = new EnterAreaCinematic(serialized.identifier, this, serialized.type, serialized.sourcePath, serialized.conditionMeta);
        c.allowCutsceneSkip = serialized.allowCutsceneSkip;
        c.triggerDelay = serialized.triggerDelay;
        c.oneTimeCinematic = serialized.oneTimeCinematic;
        c.fadeInCutscene = serialized.fadeInCutscene;
        c.fadeOutCutscene = serialized.fadeOutCutscene;
        c.stopWorldMusicOnAudio = serialized.stopWorldMusicOnAudio;
        return c;
    }

    @Override
    public void onConditionMetaButtonClick(AdvancedButton parentBtn, EditCinematicScreen parentScreen) {
        EnterAreaCoordinatesScreen s = new EnterAreaCoordinatesScreen(parentScreen, parentScreen.getContext().conditionMeta, (call) -> {
            if (call != null) {
                parentScreen.getContext().conditionMeta = call;
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
    public List<String> getConditionMetaButtonDescription() {
        return null;
    }

}
