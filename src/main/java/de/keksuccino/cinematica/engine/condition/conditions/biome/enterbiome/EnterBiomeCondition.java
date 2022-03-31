package de.keksuccino.cinematica.engine.condition.conditions.biome.enterbiome;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class EnterBiomeCondition extends Condition {

    protected boolean gotTriggered = false;

    public EnterBiomeCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
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
                if (!this.gotTriggered) {
                    this.gotTriggered = true;
                    return true;
                }
            } else {
                this.gotTriggered = false;
            }
        }

        return false;

    }

}
