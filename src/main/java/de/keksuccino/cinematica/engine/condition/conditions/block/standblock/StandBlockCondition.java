package de.keksuccino.cinematica.engine.condition.conditions.block.standblock;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class StandBlockCondition extends Condition {

    public StandBlockCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {
            String curBlock = conditionContext.getEntryValue("block");
            String conBlock = this.conditionMeta.getEntryValue("block");

            boolean isBlock = false;
            if ((conBlock != null) && !conBlock.replace(" ", "").equals("") && (curBlock != null) && !curBlock.replace(" ", "").equals("")) {
                if (curBlock.equals(conBlock)) {
                    isBlock = true;
                }
            }

            if (isBlock) {
                return true;
            }
        }

        return false;

    }

}