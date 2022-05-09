package de.keksuccino.cinematica.engine.condition.conditions.chat.receivechat;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class ReceiveChatCondition extends Condition {

    public ReceiveChatCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {
            String content = conditionContext.getEntryValue("content");

            String conContent = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("content"), "§", "&");
            String conCheckType = this.conditionMeta.getEntryValue("check_type");
            if (conCheckType == null) {
                conCheckType = "equals";
            }

            boolean isLine = false;
            if (conContent == null) {
                isLine = true;
            } else if (content != null) {
                if (conCheckType.equals("equals")) {
                    if (content.equals(conContent)) {
                        isLine = true;
                    }
                } else if (conCheckType.equals("starts-with")) {
                    if (content.startsWith(conContent)) {
                        isLine = true;
                    }
                } else if (conCheckType.equals("ends-with")) {
                    if (content.endsWith(conContent)) {
                        isLine = true;
                    }
                } else if (conCheckType.equals("contains")) {
                    if (content.contains(conContent)) {
                        isLine = true;
                    }
                }
            }

            if (isLine) {
                return true;
            }
        }

        return false;

    }

}
