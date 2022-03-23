package de.keksuccino.cinematica.trigger.triggers.killentity;

import de.keksuccino.cinematica.trigger.Cinematic;
import de.keksuccino.cinematica.trigger.CinematicType;
import de.keksuccino.cinematica.trigger.Trigger;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class KillEntityTrigger extends Trigger {

    public KillEntityTrigger() {
        super("cinematica_trigger:kill_entity");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public Cinematic createCinematicFromSerializedObject(Cinematic.SerializedCinematic serializedCinematic) {
        return null;
    }

    @Override
    public void onAddCinematicButtonClick(Screen parentScreen, CinematicType type) {

    }

    @Override
    public void onEditCinematicButtonClick(Screen parentScreen, Cinematic cinematic) {

    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public List<String> getDescription() {
        return null;
    }

    @Override
    public String getConditionValueButtonDisplayName() {
        return null;
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
