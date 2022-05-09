package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.mixinhandling.MixinCache;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public abstract class MixinPlayerInventory {

    private boolean isCached = false;
    private boolean setInvMethodCalled = false;

    @Inject(at = @At("RETURN"), method = "tick")
    private void onTick(CallbackInfo info) {
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().player.getInventory() != null)) {
            if (((Object)this) == Minecraft.getInstance().player.getInventory()) {
                if (!isCached && setInvMethodCalled) {
                    MixinCache.lastPlayerContainer = Minecraft.getInstance().player.inventoryMenu;
                    isCached = true;
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "setItem")
    private void onSetInventorySlotContents(int index, ItemStack stack, CallbackInfo info) {
        this.setInvMethodCalled = true;
    }

}
