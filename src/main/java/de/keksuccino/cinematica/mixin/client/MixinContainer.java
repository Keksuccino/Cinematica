package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.AddItemToPlayerInventoryEvent;
import de.keksuccino.cinematica.mixinhandling.CachedInventorySlot;
import de.keksuccino.cinematica.mixinhandling.MixinCache;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(AbstractContainerMenu.class)
public class MixinContainer {

    private static final Logger MIXIN_LOGGER = LogManager.getLogger("cinematica/mixin/MixinContainer");

    private Map<Integer, CachedInventorySlot> cachedSlots = new HashMap<>();

    @Inject(at = @At("HEAD"), method = "setItem")
    private void onPutStackInSlot(int slotID, int stateID, ItemStack stack, CallbackInfo info) {
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().player.inventoryMenu != null)) {
            if (((Object) this) == Minecraft.getInstance().player.inventoryMenu) {
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

}
