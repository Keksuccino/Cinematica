package de.keksuccino.cinematica.engine.condition;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ConditionFactoryHandler {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ConditionFactoryHandler());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onTick(TickEvent.ClientTickEvent e) {
        try {

            for (ConditionFactory t : ConditionFactoryRegistry.getFactories()) {
                t.tick();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
