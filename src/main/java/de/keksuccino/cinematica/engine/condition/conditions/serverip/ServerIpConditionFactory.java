package de.keksuccino.cinematica.engine.condition.conditions.serverip;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import java.util.Arrays;
import java.util.List;

public class ServerIpConditionFactory extends ConditionFactory {

    public ServerIpConditionFactory() {
        super("cinematica_condition_server_ip");
    }

    @Override
    public void conditionContextTick() {
        if (Minecraft.getInstance().getCurrentServerData() != null) {
            PropertiesSection sec = new PropertiesSection("condition-context");
            sec.addEntry("ip", Minecraft.getInstance().getCurrentServerData().serverIP);
            this.conditionContext = sec;
        } else {
            this.conditionContext = null;
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new ServerIpCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().displayGuiScreen(new ServerIpConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new ServerIpCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().displayGuiScreen(new ServerIpConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.serverip");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.serverip.desc"), "%n%"));
    }

}
