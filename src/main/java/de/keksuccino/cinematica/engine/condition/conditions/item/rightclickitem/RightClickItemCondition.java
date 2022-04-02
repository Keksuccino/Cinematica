package de.keksuccino.cinematica.engine.condition.conditions.item.rightclickitem;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class RightClickItemCondition extends Condition {

    public RightClickItemCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {
            String itemType = conditionContext.getEntryValue("item_type");
            String itemName = conditionContext.getEntryValue("item_name");
            String itemLore = conditionContext.getEntryValue("item_lore");
            String itemCount = conditionContext.getEntryValue("item_count");

            String conType = this.conditionMeta.getEntryValue("item_type");
            String conName = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("item_name"), "§", "&");
            String conLore = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("item_lore"), "§", "&");
            String conLoreCheckType = this.conditionMeta.getEntryValue("item_lore_check_type");
            if (conLoreCheckType == null) {
                conLoreCheckType = "equals";
            }
            String conCount = this.conditionMeta.getEntryValue("item_count");

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
                if (conLoreCheckType.equals("equals")) {
                    if (itemLore.equals(conLore)) {
                        isLore = true;
                    }
                } else if (conLoreCheckType.equals("starts-with")) {
                    if (itemLore.startsWith(conLore)) {
                        isLore = true;
                    }
                } else if (conLoreCheckType.equals("ends-with")) {
                    if (itemLore.endsWith(conLore)) {
                        isLore = true;
                    }
                } else if (conLoreCheckType.equals("contains")) {
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
