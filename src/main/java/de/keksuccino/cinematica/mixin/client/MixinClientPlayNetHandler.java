package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.ChatMessageReceivedEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPlayNetHandler {

    @Inject(at = @At("RETURN"), method = "handleChat")
    private void onHandleChat(ClientboundChatPacket packetIn, CallbackInfo info) {
        ChatMessageReceivedEvent event = new ChatMessageReceivedEvent(packetIn.getMessage(), packetIn.getSender());
        MinecraftForge.EVENT_BUS.post(event);
    }

}
