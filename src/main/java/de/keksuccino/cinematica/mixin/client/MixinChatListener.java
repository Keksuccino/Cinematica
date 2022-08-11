package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.ChatMessageReceivedEvent;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.*;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ChatListener.class)
public class MixinChatListener {

    private static PlayerInfo cachedSender = null;
    private static PlayerChatMessage cachedPlayerChatMessage = null;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/PlayerChatMessage;filterMask()Lnet/minecraft/network/chat/FilterMask;"), method = "showMessageToPlayer")
    private void onShowMessageToPlayer(ChatType.Bound bound, PlayerChatMessage playerChatMessage, Component component, PlayerInfo playerInfo, boolean b, Instant instant, CallbackInfoReturnable<Boolean> info) {
        cachedSender = playerInfo;
        cachedPlayerChatMessage = playerChatMessage;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V"), method = "showMessageToPlayer")
    private void onAddMessageInShowMessageToPlayer(ChatComponent instance, Component component, MessageSignature signature, GuiMessageTag tag) {
        if (cachedSender != null) {
            ChatMessageReceivedEvent event = new ChatMessageReceivedEvent(component, cachedSender.getProfile().getId());
            MinecraftForge.EVENT_BUS.post(event);
        }
        instance.addMessage(component, signature, tag);
    }

}
