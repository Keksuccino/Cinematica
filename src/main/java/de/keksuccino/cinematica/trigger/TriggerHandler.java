package de.keksuccino.cinematica.trigger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TriggerHandler {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new TriggerHandler());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onTick(TickEvent.ClientTickEvent e) {
        try {

            for (Trigger t : TriggerRegistry.getTriggers()) {
                t.tick();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
