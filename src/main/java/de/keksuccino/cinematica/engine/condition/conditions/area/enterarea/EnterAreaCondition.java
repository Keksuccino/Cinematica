package de.keksuccino.cinematica.engine.condition.conditions.area.enterarea;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public class EnterAreaCondition extends Condition {

    protected boolean gotTriggered = false;

    public EnterAreaCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        Integer[] playerCoords = getCoordinates(conditionContext.getEntryValue("player_coordinates"));

        Integer[] fromCoords = getCoordinates(this.conditionMeta.getEntryValue("from_coordinates"));
        Integer[] toCoords = getCoordinates(this.conditionMeta.getEntryValue("to_coordinates"));

        if ((playerCoords != null) && (fromCoords != null) && (toCoords != null)) {

            int xPlayer = playerCoords[0];
            int yPlayer = playerCoords[1];
            int zPlayer = playerCoords[2];

            int xFrom = Math.min(fromCoords[0], toCoords[0]);
            int yFrom = Math.min(fromCoords[1], toCoords[1]);
            int zFrom = Math.min(fromCoords[2], toCoords[2]);

            int xTo = Math.max(fromCoords[0], toCoords[0]);
            int yTo = Math.max(fromCoords[1], toCoords[1]);
            int zTo = Math.max(fromCoords[2], toCoords[2]);

            boolean isX = (xPlayer >= xFrom) && (xPlayer <= xTo);
            boolean isY = (yPlayer >= yFrom) && (yPlayer <= yTo);
            boolean isZ = (zPlayer >= zFrom) && (zPlayer <= zTo);

            if (isX && isY && isZ) {
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
