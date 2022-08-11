package de.keksuccino.cinematica.events;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class PlayerDropItemEvent extends Event {

    protected ItemStack stack;

    public PlayerDropItemEvent(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

}
