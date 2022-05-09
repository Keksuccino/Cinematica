package de.keksuccino.cinematica.engine.condition.conditions.item.rightclickitem;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.item.ItemConditionScreen;
import de.keksuccino.cinematica.events.PlayerRightClickItemEvent;
import de.keksuccino.cinematica.utils.formatting.FormattingUtils;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class RightClickItemConditionFactory extends ConditionFactory {

    protected ItemStack rightClickedItem = null;

    public RightClickItemConditionFactory() {
        super("cinematica_condition_right_click_item");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRightClick(PlayerRightClickItemEvent e) {
        this.rightClickedItem = e.getItemStack().copy();
    }

    @Override
    public void conditionContextTick() {
        if ((this.rightClickedItem != null) && (Minecraft.getInstance().player != null) && (Minecraft.getInstance().level != null)) {

            PropertiesSection sec = new PropertiesSection("condition-context");
            sec.addEntry("item_type", this.rightClickedItem.getItem().getRegistryName().toString());
            sec.addEntry("item_name", StringUtils.convertFormatCodes(this.rightClickedItem.getHoverName().getString(), "§", "&"));
            String lore = FormattingUtils.deserializeItemLoreToString(this.rightClickedItem);
            if (lore == null) {
                lore = "";
            }
            sec.addEntry("item_lore", StringUtils.convertFormatCodes(lore, "§", "&"));
            sec.addEntry("item_count", "" + this.rightClickedItem.getCount());
            this.conditionContext = sec;

            this.rightClickedItem = null;

        } else {
            this.conditionContext = null;
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new RightClickItemCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().setScreen(new ItemConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new RightClickItemCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().setScreen(new ItemConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.item.rightclickitem");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.item.rightclickitem.desc"), "%n%"));
    }

}
