package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.PlayerDropItemEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class MixinClientPlayerEntity {

    @Inject(at = @At("HEAD"), method = "drop")
    private void onDrop(boolean b, CallbackInfoReturnable<Boolean> info) {
        LocalPlayer player = (LocalPlayer) ((Object)this);
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().player.getUUID().toString().equals(player.getUUID().toString()))) {
            PlayerDropItemEvent e = new PlayerDropItemEvent(player.getInventory().getSelected());
            MinecraftForge.EVENT_BUS.post(e);
        }
    }

}
