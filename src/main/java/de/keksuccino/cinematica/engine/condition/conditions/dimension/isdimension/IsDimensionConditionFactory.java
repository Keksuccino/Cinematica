package de.keksuccino.cinematica.engine.condition.conditions.dimension.isdimension;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.dimension.DimensionConditionScreen;
import de.keksuccino.cinematica.utils.WorldUtils;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import java.util.Arrays;
import java.util.List;

public class IsDimensionConditionFactory extends ConditionFactory {

    public IsDimensionConditionFactory() {
        super("cinematica_condition_is_dimension");
    }

    @Override
    public void conditionContextTick() {
        if (Minecraft.getInstance().player != null) {
            PropertiesSection sec = new PropertiesSection("condition-context");
            String dim = WorldUtils.getCurrentDimensionKey();
            if (dim == null) {
                dim = "";
            }
            sec.addEntry("dimension", dim);
            this.conditionContext = sec;
        } else {
            this.conditionContext = new PropertiesSection("condition-context");
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new IsDimensionCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().displayGuiScreen(new DimensionConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new IsDimensionCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().displayGuiScreen(new DimensionConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.dimension.isdimension");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.dimension.isdimension.desc"), "%n%"));
    }

}
