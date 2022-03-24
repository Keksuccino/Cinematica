package de.keksuccino.cinematica.trigger.triggers.killentity;

import de.keksuccino.cinematica.trigger.Cinematic;
import de.keksuccino.cinematica.trigger.CinematicType;
import de.keksuccino.cinematica.trigger.Trigger;
import de.keksuccino.konkrete.properties.PropertiesSection;

public class KillEntityCinematic extends Cinematic {

    public KillEntityCinematic(String identifier, Trigger parent, CinematicType type, String cinematicSource, PropertiesSection conditionMeta) {
        super(identifier, parent, type, cinematicSource, conditionMeta);
    }

    @Override
    public boolean conditionsMet(PropertiesSection triggerContext) {
        //TODO
        return false;
    }

}
