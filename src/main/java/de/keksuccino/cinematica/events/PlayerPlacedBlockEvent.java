package de.keksuccino.cinematica.events;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class PlayerPlacedBlockEvent extends Event {

    protected ItemStack itemStack;

    public PlayerPlacedBlockEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
