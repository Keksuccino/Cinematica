package de.keksuccino.cinematica.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class PlayerKilledEntityEvent extends Event {

    protected LivingEntity killedEntity;
    protected Player killer;

    public PlayerKilledEntityEvent(LivingEntity killedEntity, Player killer) {
        this.killedEntity = killedEntity;
        this.killer = killer;
    }

    public LivingEntity getKilledEntity() {
        return killedEntity;
    }

    public Player getKiller() {
        return killer;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

}
