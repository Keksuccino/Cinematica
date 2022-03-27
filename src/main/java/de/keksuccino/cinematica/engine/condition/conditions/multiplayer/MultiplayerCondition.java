package de.keksuccino.cinematica.engine.condition.conditions.multiplayer;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class MultiplayerCondition extends Condition {

    public MultiplayerCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {

            String curIp = conditionContext.getEntryValue("multiplayer");

            if ((curIp != null) && curIp.equals("true")) {
                //TODO remove debug
                Cinematica.LOGGER.info("############## CONDITIONS MET FOR MULTIPLAYER TRIGGER!");
                return true;
            }

        }

        return false;

    }

}
