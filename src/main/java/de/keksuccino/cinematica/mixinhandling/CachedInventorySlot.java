package de.keksuccino.cinematica.mixinhandling;

import net.minecraft.world.item.ItemStack;

public class CachedInventorySlot {

    public int slotID;
    public ItemStack stack;
    public int itemCount;

    public CachedInventorySlot(int slotID, ItemStack stack, int itemCount) {
        this.slotID = slotID;
        this.stack = stack;
        this.itemCount = itemCount;
    }

}
