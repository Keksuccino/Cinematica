package de.keksuccino.cinematica.engine.condition.conditions.entity.killentity;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.events.PlayerKilledEntityEvent;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class KillEntityConditionFactory extends ConditionFactory {

    protected LivingEntity killedEntity = null;
    public static LivingEntity lastKilledEntity = null;

    public KillEntityConditionFactory() {
        super("cinematica_condition_kill_entity");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKillEntity(PlayerKilledEntityEvent e) {
        if (e.getKiller().getUUID().toString().equals(Minecraft.getInstance().player.getUUID().toString())) {
            this.killedEntity = e.getKilledEntity();
            lastKilledEntity = e.getKilledEntity();
        }
    }

    @Override
    public void conditionContextTick() {
        if (this.killedEntity != null) {
            PropertiesSection sec = new PropertiesSection("condition-context");
            sec.addEntry("entity_name", StringUtils.convertFormatCodes(this.killedEntity.getDisplayName().getString(), "§", "&"));
            sec.addEntry("entity_type", Registry.ENTITY_TYPE.getKey(this.killedEntity.getType()).getNamespace() + ":" + Registry.ENTITY_TYPE.getKey(this.killedEntity.getType()).getPath());
            this.conditionContext = sec;
            this.killedEntity = null;
        } else {
            this.conditionContext = null;
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new KillEntityCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().setScreen(new KillEntityConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new KillEntityCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().setScreen(new KillEntityConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.killentity");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.killentity.desc"), "%n%"));
    }

}
