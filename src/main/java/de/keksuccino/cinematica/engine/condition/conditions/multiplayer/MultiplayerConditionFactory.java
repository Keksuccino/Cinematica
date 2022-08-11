package de.keksuccino.cinematica.engine.condition.conditions.multiplayer;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.ui.popup.CinematicaNotificationPopup;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MultiplayerConditionFactory extends ConditionFactory {

    public MultiplayerConditionFactory() {
        super("cinematica_condition_multiplayer");
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void conditionContextTick() {
        if (Minecraft.getInstance().getCurrentServer() != null) {
            PropertiesSection sec = new PropertiesSection("condition-context");
            sec.addEntry("multiplayer", "" + (!Minecraft.getInstance().hasSingleplayerServer()));
            this.conditionContext = sec;
        } else {
            this.conditionContext = null;
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new MultiplayerCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        cinematicToAddTheConditionTo.addCondition(new MultiplayerCondition(null, this, new PropertiesSection("condition-meta")));
        PopupHandler.displayPopup(new CinematicaNotificationPopup(300, new Color(0,0,0,0), 240, null, StringUtils.splitLines(Locals.localize("cinematica.condition.multiplayer.added"), "%n%")));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        //do nothing
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.multiplayer");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.multiplayer.desc"), "%n%"));
    }

}
