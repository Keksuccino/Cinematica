package de.keksuccino.cinematica.events;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

public class EntityDiedEvent extends Event {

    protected Entity entity;
    protected BlockPos deathPos;
    protected World world;

    public EntityDiedEvent(Entity entity, BlockPos deathPos, World world) {
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

    public World getWorld() {
        return world;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }


}
