package de.keksuccino.cinematica.engine.condition.conditions.serverip;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class ServerIpCondition extends Condition {

    public ServerIpCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {

            String curIp = conditionContext.getEntryValue("ip");

            String conIp = this.conditionMeta.getEntryValue("ip");

            if ((curIp != null) && curIp.equals(conIp)) {
                //TODO remove debug
                Cinematica.LOGGER.info("############## CONDITIONS MET FOR SERVER IP TRIGGER!");
                return true;
            }

        }

        return false;

    }

}
