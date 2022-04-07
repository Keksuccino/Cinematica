package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.PlayerDropItemEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Inject(at = @At("HEAD"), method = "drop")
    private void onDrop(boolean b, CallbackInfoReturnable<Boolean> info) {
        ClientPlayerEntity player = (ClientPlayerEntity)((Object)this);
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().player.getUniqueID().toString().equals(player.getUniqueID().toString()))) {
            PlayerDropItemEvent e = new PlayerDropItemEvent(player.inventory.getCurrentItem());
            MinecraftForge.EVENT_BUS.post(e);
        }
    }

}
