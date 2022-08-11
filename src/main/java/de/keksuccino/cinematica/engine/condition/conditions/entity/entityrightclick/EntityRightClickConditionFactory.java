package de.keksuccino.cinematica.engine.condition.conditions.entity.entityrightclick;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entity.EntityConditionScreen;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class EntityRightClickConditionFactory extends ConditionFactory {

    protected LivingEntity rightClickTarget = null;

    public EntityRightClickConditionFactory() {
        super("cinematica_condition_entity_right_click");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInteractEntity(PlayerInteractEvent.EntityInteract e) {
        if (e.getSide().isClient()) {
            if (e.getTarget() instanceof LivingEntity) {
                this.rightClickTarget = (LivingEntity) e.getTarget();
            }
        }
    }

    @Override
    public void conditionContextTick() {

        if ((this.rightClickTarget != null) && (Minecraft.getInstance().level != null) && (Minecraft.getInstance().player != null)) {

            PropertiesSection sec = new PropertiesSection("condition-context");
            sec.addEntry("entity_name", StringUtils.convertFormatCodes(this.rightClickTarget.getDisplayName().getString(), "§", "&"));
            sec.addEntry("entity_type", Registry.ENTITY_TYPE.getKey(this.rightClickTarget.getType()).toString());
            this.conditionContext = sec;

            this.rightClickTarget = null;

        } else {
            this.conditionContext = null;
        }

    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new EntityRightClickCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().setScreen(new EntityConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new EntityRightClickCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().setScreen(new EntityConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.entity.entityrightclick");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.entity.entityrightclick.desc"), "%n%"));
    }

}
