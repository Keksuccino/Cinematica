package de.keksuccino.cinematica.engine.condition.conditions.singleplayer;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class SingleplayerCondition extends Condition {

    public SingleplayerCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {

            String curSP = conditionContext.getEntryValue("singleplayer");

            if ((curSP != null) && curSP.equals("true")) {
                return true;
            }

        }

        return false;

    }

}
