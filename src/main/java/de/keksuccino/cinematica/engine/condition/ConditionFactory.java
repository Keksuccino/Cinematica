package de.keksuccino.cinematica.engine.condition;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.gui.screen.Screen;

import java.util.*;

public abstract class ConditionFactory {

    protected final String identifier;

    protected PropertiesSection conditionContext = new PropertiesSection("condition-context");

    public ConditionFactory(String uniqueIdentifier) {
        this.identifier = uniqueIdentifier;
    }

    public boolean isEditable() {
        return true;
    }

    /**
     * This method is called every tick to update the {@link ConditionFactory#conditionContext}.<br>
     * Conditions of this factory use the condition context to check if they meet all conditions to get triggered.
     */
    public abstract void conditionContextTick();

    public PropertiesSection getConditionContext() {
        return this.conditionContext;
    }

    /**
     * The ticker for this factory.<br>
     * Is called every client tick.<br><br>
     *
     * If you override this, don't forget to call {@code super.tick()}!
     */
    public void tick() {
        try {

            this.conditionContextTick();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Create an instance of a condition out of a {@link de.keksuccino.cinematica.condition.condition.Condition.SerializedCondition}.<br>
     * This is used to load saved cinematics on mod init and more.
     */
    public abstract Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized);

    /**
     * Is called when clicking the Add Condition button in the Manage Conditions screen.<br><br>
     *
     * You need to manually add the new condition to the cinematic by calling {@link Cinematic#addCondition(Condition)}!
     */
    public abstract void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo);

    /**
     * Is called when clicking the Edit Condition button in the Manage Conditions screen.<br><br>
     *
     * You need to manually call {@link Cinematic#saveChanges()} after editing a condition!
     */
    public abstract void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition);

    public abstract String getDisplayName();

    public abstract List<String> getDescription();

    public String getIdentifier() {
        return this.identifier;
    }

}
