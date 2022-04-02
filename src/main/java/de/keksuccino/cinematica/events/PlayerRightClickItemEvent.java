package de.keksuccino.cinematica.events;

import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class PlayerRightClickItemEvent extends Event {

    protected ItemStack itemStack;

    public PlayerRightClickItemEvent(ItemStack itemStack) {
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
