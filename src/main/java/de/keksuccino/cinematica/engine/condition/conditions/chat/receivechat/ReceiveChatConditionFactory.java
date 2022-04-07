package de.keksuccino.cinematica.engine.condition.conditions.chat.receivechat;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.chat.ChatConditionScreen;
import de.keksuccino.cinematica.events.ChatMessageReceivedEvent;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class ReceiveChatConditionFactory extends ConditionFactory {

    public ITextComponent receivedChatMessage = null;

    public ReceiveChatConditionFactory() {
        super("cinematica_condition_receive_chat");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChatReceived(ChatMessageReceivedEvent e) {
        this.receivedChatMessage = e.getMessage();
    }

    @Override
    public void conditionContextTick() {
        if ((this.receivedChatMessage != null) && (Minecraft.getInstance().player != null) && (Minecraft.getInstance().world != null)) {

            PropertiesSection sec = new PropertiesSection("condition-context");
            sec.addEntry("content", StringUtils.convertFormatCodes(this.receivedChatMessage.getString(), "§", "&"));
            this.conditionContext = sec;

            this.receivedChatMessage = null;

        } else {
            this.conditionContext = null;
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new ReceiveChatCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().displayGuiScreen(new ChatConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new ReceiveChatCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().displayGuiScreen(new ChatConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.chat.receivechat");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.chat.receivechat.desc"), "%n%"));
    }

}
