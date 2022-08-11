package de.keksuccino.cinematica.engine.condition.conditions.tablist.becomestab;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class BecomesTabListCondition extends Condition {

    protected boolean gotTriggered = false;

    public BecomesTabListCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {
            String header = conditionContext.getEntryValue("header");
            String footer = conditionContext.getEntryValue("footer");

            String conContent = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("content"), "§", "&");
            String conLine = this.conditionMeta.getEntryValue("line");
            if (conLine == null) {
                conLine = "header";
            }
            String conCheckType = this.conditionMeta.getEntryValue("check_type");
            if (conCheckType == null) {
                conCheckType = "equals";
            }

            String line = null;
            boolean isLine = false;
            if (conLine.equals("header")) {
                line = header;
            } else if (conLine.equals("footer")) {
                line = footer;
            }
            if (conContent == null) {
                isLine = true;
            } else if (line != null) {
                if (conCheckType.equals("equals")) {
                    if (line.equals(conContent)) {
                        isLine = true;
                    }
                } else if (conCheckType.equals("starts-with")) {
                    if (line.startsWith(conContent)) {
                        isLine = true;
                    }
                } else if (conCheckType.equals("ends-with")) {
                    if (line.endsWith(conContent)) {
                        isLine = true;
                    }
                } else if (conCheckType.equals("contains")) {
                    if (line.contains(conContent)) {
                        isLine = true;
                    }
                }
            }

            if (isLine) {
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
