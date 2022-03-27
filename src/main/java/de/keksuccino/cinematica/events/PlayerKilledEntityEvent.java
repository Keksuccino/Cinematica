package de.keksuccino.cinematica.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class PlayerKilledEntityEvent extends Event {

    protected LivingEntity killedEntity;
    protected PlayerEntity killer;

    public PlayerKilledEntityEvent(LivingEntity killedEntity, PlayerEntity killer) {
        this.killedEntity = killedEntity;
        this.killer = killer;
    }

    public LivingEntity getKilledEntity() {
        return killedEntity;
    }

    public PlayerEntity getKiller() {
        return killer;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

}
