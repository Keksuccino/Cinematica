package de.keksuccino.cinematica.trigger.triggers.enterarea;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.trigger.Cinematic;
import de.keksuccino.cinematica.trigger.CinematicType;
import de.keksuccino.cinematica.trigger.Trigger;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

public class EnterAreaCinematic extends Cinematic {

    protected boolean gotTriggered = false;

    public EnterAreaCinematic(Trigger parent, CinematicType type, String cinematicSource, PropertiesSection conditionMeta) {
        super(parent, type, cinematicSource, conditionMeta);
    }

    @Override
    public boolean conditionsMet(PropertiesSection parentConditionMeta) {

        Integer[] playerCoords = getCoordinates(parentConditionMeta.getEntryValue("player_coordinates"));

        Integer[] fromCoords = getCoordinates(this.conditionMeta.getEntryValue("from_coordinates"));
        Integer[] toCoords = getCoordinates(this.conditionMeta.getEntryValue("to_coordinates"));
        String curDim = parentConditionMeta.getEntryValue("dimension");
        String conDim = this.conditionMeta.getEntryValue("dimension");

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

            boolean isDimension = false;
            if ((conDim == null) || conDim.replace(" ", "").equals("")) {
                isDimension = true;
            } else if ((curDim != null) && curDim.equals(conDim)) {
                isDimension = true;
            }

            if (isX && isY && isZ && isDimension) {
                if (!this.gotTriggered) {
                    this.gotTriggered = true;
                    //TODO remove debug
                    Cinematica.LOGGER.info("############## CONDITIONS MET FOR AREA TRIGGER!");
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
