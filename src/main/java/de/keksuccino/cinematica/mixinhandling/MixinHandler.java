package de.keksuccino.cinematica.mixinhandling;

import de.keksuccino.cinematica.events.PlayerKilledEntityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MixinHandler {

    public static LivingEntity lastAttackedLivingEntity = null;

    protected static Level lastWorld = null;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new MixinHandler());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {

        if (lastWorld != null) {
            if (lastWorld != Minecraft.getInstance().level) {
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

        lastWorld = Minecraft.getInstance().level;

    }

}
