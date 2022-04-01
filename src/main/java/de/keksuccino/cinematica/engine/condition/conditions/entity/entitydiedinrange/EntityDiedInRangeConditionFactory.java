package de.keksuccino.cinematica.engine.condition.conditions.entity.entitydiedinrange;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entity.EntityRangeConditionScreen;
import de.keksuccino.cinematica.events.EntityDiedEvent;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityDiedInRangeConditionFactory extends ConditionFactory {

    protected List<EntityDiedEvent> diedEntityQueue = new ArrayList<>();

    public EntityDiedInRangeConditionFactory() {
        super("cinematica_condition_entity_died_in_range");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityDied(EntityDiedEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            this.diedEntityQueue.add(e);
        }
    }

    @Override
    public void conditionContextTick() {
        if (!this.diedEntityQueue.isEmpty() && (Minecraft.getInstance().player != null)) {

            EntityDiedEvent e = this.diedEntityQueue.get(0);
            LivingEntity diedEntity = (LivingEntity) e.getEntity();
            BlockPos deathPos = e.getDeathPosition();
            BlockPos playerPos = Minecraft.getInstance().player.getPosition();

            PropertiesSection sec = new PropertiesSection("condition-context");
            sec.addEntry("entity_name", StringUtils.convertFormatCodes(diedEntity.getDisplayName().getString(), "§", "&"));
            sec.addEntry("entity_type", diedEntity.getType().getRegistryName().getNamespace() + ":" + diedEntity.getType().getRegistryName().getPath());
            sec.addEntry("entity_pos", "" + deathPos.getX() + "," + deathPos.getY() + "," + deathPos.getZ());
            sec.addEntry("player_pos", "" + playerPos.getX() + "," + playerPos.getY() + "," + playerPos.getZ());
            this.conditionContext = sec;

            this.diedEntityQueue.remove(e);

        } else {
            this.conditionContext = null;
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new EntityDiedInRangeCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().displayGuiScreen(new EntityRangeConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new EntityDiedInRangeCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().displayGuiScreen(new EntityRangeConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.entity.entitydied");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.entity.entitydied.desc"), "%n%"));
    }

}
