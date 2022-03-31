package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.mixinhandling.MixinCache;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {

    private boolean isCached = false;
    private boolean setInvMethodCalled = false;

    @Inject(at = @At("RETURN"), method = "tick")
    private void onTick(CallbackInfo info) {
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().player.inventory != null)) {
            if (((Object)this) == Minecraft.getInstance().player.inventory) {
                if (!isCached && setInvMethodCalled) {
                    MixinCache.lastPlayerContainer = Minecraft.getInstance().player.container;
                    isCached = true;
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "setInventorySlotContents")
    private void onSetInventorySlotContents(int index, ItemStack stack, CallbackInfo info) {
        this.setInvMethodCalled = true;
    }

}
