package de.keksuccino.cinematica.engine.condition.conditions.dimension.enterdimension;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class EnterDimensionCondition extends Condition {

    protected boolean gotTriggered = false;

    public EnterDimensionCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        String curDim = conditionContext.getEntryValue("dimension");
        String conDim = this.conditionMeta.getEntryValue("dimension");

        boolean isDimension = false;
        if ((conDim == null) || conDim.replace(" ", "").equals("") || conDim.equals("cinematica.blankdimension")) {
            isDimension = true;
        } else if ((curDim != null) && curDim.equals(conDim)) {
            isDimension = true;
        }

        if (isDimension) {
            if (!this.gotTriggered) {
                this.gotTriggered = true;
                return true;
            }
        } else {
            this.gotTriggered = false;
        }

        return false;

    }

}
