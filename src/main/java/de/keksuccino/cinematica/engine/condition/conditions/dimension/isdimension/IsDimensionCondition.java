package de.keksuccino.cinematica.engine.condition.conditions.dimension.isdimension;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class IsDimensionCondition extends Condition {

    public IsDimensionCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
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

        return isDimension;

    }

}
