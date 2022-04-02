package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.PlayerFoodEatenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(at = @At("RETURN"), method = "onFoodEaten")
    private void onOnFoodEaten(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        PlayerEntity player = (PlayerEntity)((Object)this);
        if (player instanceof ClientPlayerEntity) {
            if ((Minecraft.getInstance().player != null) && player.getUniqueID().toString().equals(Minecraft.getInstance().player.getUniqueID().toString())) {
                PlayerFoodEatenEvent e = new PlayerFoodEatenEvent(stack);
                MinecraftForge.EVENT_BUS.post(e);
            }
        }
    }

}
