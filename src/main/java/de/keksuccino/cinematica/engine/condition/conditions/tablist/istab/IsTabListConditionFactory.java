package de.keksuccino.cinematica.engine.condition.conditions.tablist.istab;

import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.tablist.TabListConditionScreen;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class IsTabListConditionFactory extends ConditionFactory {

    public IsTabListConditionFactory() {
        super("cinematica_condition_is_tab_list");
    }

    @Override
    public void conditionContextTick() {
        if ((Minecraft.getInstance().player != null) && (Minecraft.getInstance().level != null)) {

            Component header = getTabHeader();
            Component footer = getTabFooter();

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

    private Component getTabHeader() {
        try {
            Field f = ObfuscationReflectionHelper.findField(PlayerTabOverlay.class, "f_94522_"); //header
            return (Component) f.get(Minecraft.getInstance().gui.getTabList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Component getTabFooter() {
        try {
            Field f = ObfuscationReflectionHelper.findField(PlayerTabOverlay.class, "f_94521_"); //footer
            return (Component) f.get(Minecraft.getInstance().gui.getTabList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Condition createConditionFromSerializedObject(Condition.SerializedCondition serialized) {
        return new IsTabListCondition(serialized.identifier, this, serialized.conditionMeta);
    }

    @Override
    public void onAddConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Cinematic cinematicToAddTheConditionTo) {
        Minecraft.getInstance().setScreen(new TabListConditionScreen(parentScreen, null, (call) -> {
            if (call != null) {
                cinematicToAddTheConditionTo.addCondition(new IsTabListCondition(null, this, call));
            }
        }));
    }

    @Override
    public void onEditConditionButtonClick(AdvancedButton parentBtn, Screen parentScreen, Condition conditionToEdit, Cinematic parentOfCondition) {
        Minecraft.getInstance().setScreen(new TabListConditionScreen(parentScreen, conditionToEdit.conditionMeta, (call) -> {
            if (call != null) {
                conditionToEdit.conditionMeta = call;
                parentOfCondition.saveChanges();
            }
        }));
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("cinematica.condition.tablist.istab");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("cinematica.condition.tablist.istab.desc"), "%n%"));
    }

}
