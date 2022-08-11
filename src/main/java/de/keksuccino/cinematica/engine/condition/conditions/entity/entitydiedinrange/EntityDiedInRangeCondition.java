package de.keksuccino.cinematica.engine.condition.conditions.entity.entitydiedinrange;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class EntityDiedInRangeCondition extends Condition {

    public EntityDiedInRangeCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {
            String curType = conditionContext.getEntryValue("entity_type");
            String curName = conditionContext.getEntryValue("entity_name");
            String entityPos = conditionContext.getEntryValue("entity_pos");
            String playerPos = conditionContext.getEntryValue("player_pos");

            String conType = this.conditionMeta.getEntryValue("entity_type");
            String conName = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("entity_name"), "§", "&");
            String conRange = this.conditionMeta.getEntryValue("range");

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
            boolean isInRange = false;
            if ((conRange == null) || conRange.equals("")) {
                isInRange = true;
            } else if (MathUtils.isInteger(conRange)) {
                int radius = Integer.parseInt(conRange);
                Integer[] pPos = getCoordinates(playerPos);
                Integer[] ePos = getCoordinates(entityPos);
                if ((pPos != null) && (ePos != null)) {
                    int pX = pPos[0];
                    int pY = pPos[1];
                    int pZ = pPos[2];
                    int eX = ePos[0];
                    int eY = ePos[1];
                    int eZ = ePos[2];
                    double d0 = eX - pX;
                    double d1 = eY - pY;
                    double d2 = eZ - pZ;
                    if ((d0 * d0 + d1 * d1 + d2 * d2) < (radius * radius)) {
                        isInRange = true;
                    }
                }
            }

            if (isType && isName && isInRange) {
                return true;
            }
        }

        return false;

    }

    /** [X,Y,Z] **/
    protected static Integer[] getCoordinates(String coordString) {
        try {
            if (coordString != null) {
                if (coordString.contains(",")) {
                    String[] coords = coordString.split("[,]");
                    if (coords.length == 3) {
                        String xString = coords[0];
                        String yString = coords[1];
                        String zString = coords[2];
                        if (MathUtils.isInteger(xString) && MathUtils.isInteger(yString) && MathUtils.isInteger(zString)) {
                            int x = Integer.parseInt(xString);
                            int y = Integer.parseInt(yString);
                            int z = Integer.parseInt(zString);
                            return new Integer[]{x,y,z};
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
