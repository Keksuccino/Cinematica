package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.PlayerPlacedBlockEvent;
import de.keksuccino.cinematica.events.PlayerRightClickItemEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(at = @At("RETURN"), method = "use")
    private void onUseItemRightClick(Level level, Player playerIn, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info) {
        if (playerIn instanceof LocalPlayer) {
            if ((Minecraft.getInstance().player != null) && Minecraft.getInstance().player.getUUID().toString().equals(playerIn.getUUID().toString())) {
                ItemStack stack = (ItemStack)((Object)this);
                PlayerRightClickItemEvent event2 = new PlayerRightClickItemEvent(stack);
                MinecraftForge.EVENT_BUS.post(event2);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "useOn")
    private void onOnItemUse(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
        Player player = context.getPlayer();
        if (player instanceof LocalPlayer) {
            if ((Minecraft.getInstance().player != null) && Minecraft.getInstance().player.getUUID().toString().equals(player.getUUID().toString())) {
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
