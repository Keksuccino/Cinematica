package de.keksuccino.cinematica.engine.condition.conditions.block.standblock;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.block.BlockConditionScreen;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class StandBlockConditionFactory extends ConditionFactory {

    public StandBlockConditionFactory() {
        super("cinematica_condition_stand_block");
    }

    @Override
    public void conditionContextTick() {
        if ((Minecraft.getInstance().world != null) && (Minecraft.getInstance().player != null)) {
            try {
                PropertiesSection sec = new PropertiesSection("condition-context");
                BlockState blockstate = Minecraft.getInstance().world.getBlockState(Minecraft.getInstance().player.getPosition().down());
                String block = Registry.BLOCK.getKey(blockstate.getBlock()).toString();
                if (block != null) {
                    sec.addEntry("block", block);
                    this.conditionContext = sec;
                } else {
                    this.conditionContext = null;
                }
            } catch (Exception e) {
                this.conditionContext = null;
                e.printStackTrace();
            }
        } else {
            this.conditionContext = null;
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new StandBlockCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().displayGuiScreen(new BlockConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new StandBlockCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().displayGuiScreen(new BlockConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.block.standblock");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.block.standblock.desc"), "%n%"));
    }

}
