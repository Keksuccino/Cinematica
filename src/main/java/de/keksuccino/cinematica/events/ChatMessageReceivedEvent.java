package de.keksuccino.cinematica.events;

import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Event;

import java.util.UUID;

public class ChatMessageReceivedEvent extends Event {

    protected Component message;
    protected UUID sender;

    public ChatMessageReceivedEvent(Component message, UUID sender) {
        this.message = message;
        this.sender = sender;
    }

    public Component getMessage() {
        return message;
    }

    public UUID getSender() {
        return sender;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

}
