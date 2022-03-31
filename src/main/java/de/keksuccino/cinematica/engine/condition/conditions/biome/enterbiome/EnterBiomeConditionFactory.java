package de.keksuccino.cinematica.engine.condition.conditions.biome.enterbiome;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.biome.BiomeConditionScreen;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class EnterBiomeConditionFactory extends ConditionFactory {

    public EnterBiomeConditionFactory() {
        super("cinematica_condition_enter_biome");
    }

    @Override
    public void conditionContextTick() {
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().world != null)) {
            try {
                PropertiesSection sec = new PropertiesSection("condition-context");
                String biome = Minecraft.getInstance().world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(Minecraft.getInstance().world.getBiome(Minecraft.getInstance().player.getPosition())).toString();
                if (biome != null) {
                    sec.addEntry("biome", biome);
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
        return new EnterBiomeCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().displayGuiScreen(new BiomeConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new EnterBiomeCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().displayGuiScreen(new BiomeConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.biome.enterbiome");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.biome.enterbiome.desc"), "%n%"));
    }

}
