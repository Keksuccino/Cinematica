package de.keksuccino.cinematica.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

public class EntityDiedEvent extends Event {

    protected Entity entity;
    protected BlockPos deathPos;
    protected Level world;

    public EntityDiedEvent(Entity entity, BlockPos deathPos, Level world) {
        this.entity = entity;
        this.deathPos = deathPos;
        this.world = world;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockPos getDeathPosition() {
        return deathPos;
    }

    public Level getWorld() {
        return world;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }


}
