package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.events.ChatMessageReceivedEvent;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public class MixinClientPlayNetHandler {

    @Inject(at = @At("RETURN"), method = "handleChat")
    private void onHandleChat(SChatPacket packetIn, CallbackInfo info) {
        ChatMessageReceivedEvent event = new ChatMessageReceivedEvent(packetIn.getChatComponent(), packetIn.getSender());
        MinecraftForge.EVENT_BUS.post(event);
    }

}
