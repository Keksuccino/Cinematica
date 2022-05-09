package de.keksuccino.cinematica.engine.condition.conditions.biome.isbiome;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.biome.BiomeConditionScreen;
import de.keksuccino.cinematica.utils.WorldUtils;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.Arrays;
import java.util.List;

public class IsBiomeConditionFactory extends ConditionFactory {

    public IsBiomeConditionFactory() {
        super("cinematica_condition_is_biome");
    }

    @Override
    public void conditionContextTick() {
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().level != null)) {
            try {
                PropertiesSection sec = new PropertiesSection("condition-context");
                String biome = WorldUtils.getCurrentBiomeName();
                if (biome != null) {
                    sec.addEntry("biome", biome);
                    this.conditionContext = sec;
                } else {
                    this.conditionContext = null;
                }
            } catch (Exception e) {
                this.conditionContext = null;
                e.printStackTrace();
            }
        } else {
            this.conditionContext = null;
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new IsBiomeCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().setScreen(new BiomeConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new IsBiomeCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().setScreen(new BiomeConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.biome.isbiome");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.biome.isbiome.desc"), "%n%"));
    }

}
