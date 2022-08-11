package de.keksuccino.cinematica.engine.condition.conditions.biome.isbiome;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class IsBiomeCondition extends Condition {

    public IsBiomeCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {
            String curBiome = conditionContext.getEntryValue("biome");
            String conBiome = this.conditionMeta.getEntryValue("biome");

            boolean isBiome = false;
            if ((conBiome != null) && !conBiome.replace(" ", "").equals("") && (curBiome != null) && !curBiome.replace(" ", "").equals("")) {
                if (curBiome.equals(conBiome)) {
                    isBiome = true;
                }
            }

            if (isBiome) {
                return true;
            }
        }

        return false;

    }

}
