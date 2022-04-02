package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.PlayerPlacedBlockEvent;
import de.keksuccino.cinematica.events.PlayerRightClickItemEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(at = @At("RETURN"), method = "useItemRightClick")
    private void onUseItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand, CallbackInfoReturnable<ActionResult<ItemStack>> info) {
        if (playerIn instanceof ClientPlayerEntity) {
            if ((Minecraft.getInstance().player != null) && Minecraft.getInstance().player.getUniqueID().toString().equals(playerIn.getUniqueID().toString())) {
                ItemStack stack = (ItemStack)((Object)this);
                PlayerRightClickItemEvent event2 = new PlayerRightClickItemEvent(stack);
                MinecraftForge.EVENT_BUS.post(event2);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onItemUse(Lnet/minecraft/item/ItemUseContext;)Lnet/minecraft/util/ActionResultType;")
    private void onOnItemUse(ItemUseContext context, CallbackInfoReturnable<ActionResultType> info) {
        PlayerEntity player = context.getPlayer();
        if (player instanceof ClientPlayerEntity) {
            if ((Minecraft.getInstance().player != null) && Minecraft.getInstance().player.getUniqueID().toString().equals(player.getUniqueID().toString())) {
                ItemStack stack = (ItemStack)((Object)this);
                if (stack.getItem() instanceof BlockItem) {
                    //TODO FIX: is not fired for LiliPadItem (and maybe for other items that override right-click stuff in their Item class)
                    PlayerPlacedBlockEvent event1 = new PlayerPlacedBlockEvent(stack);
                    MinecraftForge.EVENT_BUS.post(event1);
                }
                //TODO FIX: is fired twice (fired in this method and onUseItemRightClick) for some items like food items, when right-clicking a block
                PlayerRightClickItemEvent event2 = new PlayerRightClickItemEvent(stack);
                MinecraftForge.EVENT_BUS.post(event2);
            }
        }
    }

}
