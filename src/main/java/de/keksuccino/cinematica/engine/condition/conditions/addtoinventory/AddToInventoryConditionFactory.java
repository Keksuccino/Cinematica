package de.keksuccino.cinematica.engine.condition.conditions.addtoinventory;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.events.AddItemToPlayerInventoryEvent;
import de.keksuccino.cinematica.utils.formatting.FormattingUtils;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddToInventoryConditionFactory extends ConditionFactory {

    protected List<ItemStack> addedItemQueue = new ArrayList<>();

    public AddToInventoryConditionFactory() {
        super("cinematica_condition_add_item_to_inventory");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAddItemToInv(AddItemToPlayerInventoryEvent e) {
        addedItemQueue.add(e.getStack());
    }

    @Override
    public void conditionContextTick() {
        if (!this.addedItemQueue.isEmpty() && (Minecraft.getInstance().player != null) && (Minecraft.getInstance().world != null)) {

            ItemStack item = this.addedItemQueue.get(0);

            PropertiesSection sec = new PropertiesSection("condition-context");
            sec.addEntry("item_type", item.getItem().getRegistryName().toString());
            sec.addEntry("item_name", StringUtils.convertFormatCodes(item.getDisplayName().getString(), "§", "&"));
            sec.addEntry("item_lore", StringUtils.convertFormatCodes(FormattingUtils.deserializeItemLoreToString(item), "§", "&"));
            sec.addEntry("item_count", "" + item.getCount());
            this.conditionContext = sec;

            this.addedItemQueue.remove(0);

        } else {
            this.conditionContext = null;
        }
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new AddItemToInventoryCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().displayGuiScreen(new AddToInventoryConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new AddItemToInventoryCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().displayGuiScreen(new AddToInventoryConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.addtoinventory");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.addtoinventory.desc"), "%n%"));
    }

}
