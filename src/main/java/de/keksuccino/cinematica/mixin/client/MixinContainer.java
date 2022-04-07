package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.AddItemToPlayerInventoryEvent;
import de.keksuccino.cinematica.mixinhandling.CachedInventorySlot;
import de.keksuccino.cinematica.mixinhandling.MixinCache;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Container.class)
public class MixinContainer {

    private static final Logger MIXIN_LOGGER = LogManager.getLogger("cinematica/mixin/MixinContainer");

    private Map<Integer, CachedInventorySlot> cachedSlots = new HashMap<>();

    @Inject(at = @At("HEAD"), method = "putStackInSlot")
    private void onPutStackInSlot(int slotID, ItemStack stack, CallbackInfo info) {
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().player.container != null)) {
            if (((Object) this) == Minecraft.getInstance().player.container) {
                if ((MixinCache.lastPlayerContainer != null) && (MixinCache.lastPlayerContainer == ((Object)this))) {
                    if ((stack != null) && !stack.getItem().getRegistryName().toString().equals(Items.AIR.getRegistryName().toString())) {
                        CachedInventorySlot cs = cachedSlots.get(slotID);
                        if (cs == null) {
                            cs = new CachedInventorySlot(slotID, ItemStack.EMPTY, 1);
                        }
                        if ((stack.getCount() > cs.itemCount) || cs.stack.getItem().getRegistryName().toString().equals(Items.AIR.getRegistryName().toString())) {
                            AddItemToPlayerInventoryEvent event = new AddItemToPlayerInventoryEvent(stack);
                            MinecraftForge.EVENT_BUS.post(event);
                        }
                    }
                }
                cachedSlots.put(slotID, new CachedInventorySlot(slotID, stack, stack.getCount()));
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "slotClick")
    private void onSlotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player, CallbackInfoReturnable<ItemStack> info) {
        if (clickTypeIn == ClickType.THROW) {
            MIXIN_LOGGER.info("############## DROP ITEM SLOT CLICK 1: " + player.getClass().getName());
            if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().player.getUniqueID().toString().equals(player.getUniqueID().toString()))) {
                MIXIN_LOGGER.info("############## DROP ITEM SLOT CLICK 2: " + player.getClass().getName());
            }
        }
    }

}
