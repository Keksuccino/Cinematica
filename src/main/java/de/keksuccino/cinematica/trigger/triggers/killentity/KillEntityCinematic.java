package de.keksuccino.cinematica.trigger.triggers.killentity;

import de.keksuccino.cinematica.trigger.Cinematic;
import de.keksuccino.cinematica.trigger.CinematicType;
import de.keksuccino.cinematica.trigger.Trigger;
import de.keksuccino.konkrete.properties.PropertiesSection;

public class KillEntityCinematic extends Cinematic {

    public KillEntityCinematic(Trigger parent, CinematicType type, String cinematicSource, PropertiesSection conditionMeta) {
        super(parent, type, cinematicSource, conditionMeta);
    }

    @Override
    public boolean conditionsMet(PropertiesSection parentConditionMeta) {
        //TODO
        return false;
    }

}
