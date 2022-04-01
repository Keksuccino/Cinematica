package de.keksuccino.cinematica.engine.condition.conditions.entity.killentity;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class KillEntityCondition extends Condition {

    public KillEntityCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {
            String curType = conditionContext.getEntryValue("entity_type");
            String curName = conditionContext.getEntryValue("entity_name");

            String conType = this.conditionMeta.getEntryValue("entity_type");
            String conName = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("entity_name"), "§", "&");

            boolean isType = false;
            if ((conType == null) || conType.replace(" ", "").equals("")) {
                isType = true;
            } else if ((curType != null) && curType.equals(conType)) {
                isType = true;
            }
            boolean isName = false;
            if ((conName == null) || conName.equals("")) {
                isName = true;
            } else if ((curName != null) && curName.equals(conName)) {
                isName = true;
            }

            if (isType && isName) {
                //TODO remove debug
                Cinematica.LOGGER.info("############## CONDITIONS MET FOR KILL ENTITY TRIGGER!");
                return true;
            }
        }

        return false;

    }

}
