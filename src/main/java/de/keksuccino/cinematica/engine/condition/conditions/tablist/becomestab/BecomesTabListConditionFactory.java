package de.keksuccino.cinematica.engine.condition.conditions.tablist.becomestab;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.tablist.TabListConditionScreen;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class BecomesTabListConditionFactory extends ConditionFactory {

    public BecomesTabListConditionFactory() {
        super("cinematica_condition_becomes_tab_list");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void conditionContextTick() {
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().world != null) && (Minecraft.getInstance().getCurrentServerData() != null)) {

            ITextComponent header = getTabHeader();
            ITextComponent footer = getTabFooter();

            PropertiesSection sec = new PropertiesSection("condition-context");
            if (header != null) {
                sec.addEntry("header", StringUtils.convertFormatCodes(header.getString(), "§", "&"));
            } else {
                sec.addEntry("header", "");
            }
            if (footer != null) {
                sec.addEntry("footer", StringUtils.convertFormatCodes(footer.getString(), "§", "&"));
            } else {
                sec.addEntry("footer", "");
            }
            this.conditionContext = sec;

        } else {
            this.conditionContext = null;
        }
    }

    private ITextComponent getTabHeader() {
        try {
            Field f = ObfuscationReflectionHelper.findField(PlayerTabOverlayGui.class, "field_175256_i"); //header
            return (ITextComponent) f.get(Minecraft.getInstance().ingameGUI.getTabList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ITextComponent getTabFooter() {
        try {
            Field f = ObfuscationReflectionHelper.findField(PlayerTabOverlayGui.class, "field_175255_h"); //footer
            return (ITextComponent) f.get(Minecraft.getInstance().ingameGUI.getTabList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new BecomesTabListCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().displayGuiScreen(new TabListConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new BecomesTabListCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().displayGuiScreen(new TabListConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.tablist.becomestab");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.tablist.becomestab.desc"), "%n%"));
    }

}
