package de.keksuccino.cinematica.engine.condition.conditions.tablist.becomestab;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class BecomesTabListCondition extends Condition {

    public BecomesTabListCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {
            String header = conditionContext.getEntryValue("header");
            String footer = conditionContext.getEntryValue("footer");

            String conHeader = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("header"), "§", "&");
            String conFooter = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("footer"), "§", "&");
            String conLine = this.conditionMeta.getEntryValue("line");
            String conCheckType = this.conditionMeta.getEntryValue("check_type");
            if (conCheckType == null) {
                conCheckType = "equals";
            }

            //TODO HIER WEITER MACHEN !!!
            //TODO HIER WEITER MACHEN !!!
            //TODO HIER WEITER MACHEN !!!
            //TODO HIER WEITER MACHEN !!!

            boolean isType = false;
            if ((conType == null) || conType.replace(" ", "").equals("")) {
                isType = true;
            } else if ((itemType != null) && itemType.equals(conType)) {
                isType = true;
            }
            boolean isName = false;
            if ((conName == null) || conName.equals("")) {
                isName = true;
            } else if ((itemName != null) && itemName.equals(conName)) {
                isName = true;
            }
            boolean isLore = false;
            if ((conLore == null) || conLore.equals("")) {
                isLore = true;
            } else if (itemLore != null) {
                if (conCheckType.equals("equals")) {
                    if (itemLore.equals(conLore)) {
                        isLore = true;
                    }
                } else if (conCheckType.equals("starts-with")) {
                    if (itemLore.startsWith(conLore)) {
                        isLore = true;
                    }
                } else if (conCheckType.equals("ends-with")) {
                    if (itemLore.endsWith(conLore)) {
                        isLore = true;
                    }
                } else if (conCheckType.equals("contains")) {
                    if (itemLore.contains(conLore)) {
                        isLore = true;
                    }
                }
            }
            boolean isCount = false;
            if ((conCount == null) || conCount.replace(" ", "").equals("") || conCount.replace(" ", "").equals("0") || conCount.replace(" ", "").equals("-1")) {
                isCount = true;
            } else if ((itemCount != null) && MathUtils.isInteger(itemCount.replace(" ", "")) && MathUtils.isInteger(conCount.replace(" ", ""))) {
                int iCount = Integer.parseInt(itemCount.replace(" ", ""));
                int cCount = Integer.parseInt(conCount.replace(" ", ""));
                if (iCount == cCount) {
                    isCount = true;
                }
            }

            if (isType && isName && isLore && isCount) {
                return true;
            }
        }

        return false;

    }

}
