package de.keksuccino.cinematica.engine.condition.conditions.singleplayer;

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

public class SingleplayerConditionFactory extends ConditionFactory {

    public SingleplayerConditionFactory() {
        super("cinematica_condition_singleplayer");
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void conditionContextTick() {
        PropertiesSection sec = new PropertiesSection("condition-context");
        sec.addEntry("singleplayer", "" + Minecraft.getInstance().hasSingleplayerServer());
        this.conditionContext = sec;
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new SingleplayerCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        cinematicToAddTheConditionTo.addCondition(new SingleplayerCondition(null, this, new PropertiesSection("condition-meta")));
        PopupHandler.displayPopup(new CinematicaNotificationPopup(300, new Color(0,0,0,0), 240, null, StringUtils.splitLines(Locals.localize("cinematica.condition.singleplayer.added"), "%n%")));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        //do nothing
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.singleplayer");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.singleplayer.desc"), "%n%"));
    }

}
