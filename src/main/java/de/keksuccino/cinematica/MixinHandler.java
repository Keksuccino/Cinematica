package de.keksuccino.cinematica;

import de.keksuccino.cinematica.events.PlayerKilledEntityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MixinHandler {

    public static LivingEntity lastAttackedLivingEntity = null;

    protected static World lastWorld = null;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new MixinHandler());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {

        if (lastWorld != null) {
            if (lastWorld != Minecraft.getInstance().world) {
                lastAttackedLivingEntity = null;
            }
        } else {
            lastAttackedLivingEntity = null;
        }

        if (lastAttackedLivingEntity != null) {
            if (!lastAttackedLivingEntity.isAlive()) {
                PlayerKilledEntityEvent event = new PlayerKilledEntityEvent(lastAttackedLivingEntity, Minecraft.getInstance().player);
                MinecraftForge.EVENT_BUS.post(event);
                lastAttackedLivingEntity = null;
            }
        }

        lastWorld = Minecraft.getInstance().world;

    }

}
