package de.keksuccino.cinematica.trigger.triggers;

import de.keksuccino.cinematica.trigger.TriggerRegistry;
import de.keksuccino.cinematica.trigger.triggers.enterarea.EnterAreaTrigger;
import de.keksuccino.cinematica.trigger.triggers.killentity.KillEntityTrigger;

public class Triggers {

    public static void init() {

//        TriggerRegistry.registerTrigger(new KillEntityTrigger());
        TriggerRegistry.registerTrigger(new EnterAreaTrigger());

    }

}
