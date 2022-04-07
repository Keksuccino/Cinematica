package de.keksuccino.cinematica.events;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.eventbus.api.Event;

import java.util.UUID;

public class ChatMessageReceivedEvent extends Event {

    protected ITextComponent message;
    protected UUID sender;

    public ChatMessageReceivedEvent(ITextComponent message, UUID sender) {
        this.message = message;
        this.sender = sender;
    }

    public ITextComponent getMessage() {
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
