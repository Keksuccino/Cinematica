package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.PlayerDropItemEvent;
import de.keksuccino.cinematica.events.PlayerFoodEatenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayerEntity {

    @Inject(at = @At("RETURN"), method = "eat")
    private void onOnFoodEaten(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        Player player = (Player)((Object)this);
        if (player instanceof LocalPlayer) {
            if ((Minecraft.getInstance().player != null) && player.getUUID().toString().equals(Minecraft.getInstance().player.getUUID().toString())) {
                PlayerFoodEatenEvent e = new PlayerFoodEatenEvent(stack);
                MinecraftForge.EVENT_BUS.post(e);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;")
    private void onDropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem, CallbackInfoReturnable<ItemEntity> info) {
        Player player = (Player)((Object)this);
        if (player instanceof LocalPlayer) {
            if ((Minecraft.getInstance().player != null) && player.getUUID().toString().equals(Minecraft.getInstance().player.getUUID().toString())) {
                PlayerDropItemEvent e = new PlayerDropItemEvent(droppedItem);
                MinecraftForge.EVENT_BUS.post(e);
            }
        }
    }

}
