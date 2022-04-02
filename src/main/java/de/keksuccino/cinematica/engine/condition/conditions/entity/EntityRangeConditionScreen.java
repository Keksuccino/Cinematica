package de.keksuccino.cinematica.engine.condition.conditions.entity;

import de.keksuccino.konkrete.gui.content.AdvancedTextField;
import de.keksuccino.konkrete.input.CharacterFilter;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EntityRangeConditionScreen extends EntityConditionScreen {

    protected AdvancedTextField rangeTextField;

    public EntityRangeConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {
        super(parent, conditionMeta, callback);

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        rangeTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, CharacterFilter.getIntegerCharacterFiler());
        rangeTextField.setMaxStringLength(100000);

        if (conditionMeta != null) {

            String rangeString = conditionMeta.getEntryValue("range");
            if ((rangeString != null) && MathUtils.isInteger(rangeString)) {
                rangeTextField.setText(rangeString);
            }

        }

    }

    @Override
    protected void init() {

        super.init();

        // RANGE ------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.entity.rangeselection.conditionmeta.range"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.rangeTextField));
        //-------------------------------------

    }

    @Override
    protected void onDone() {
        if (this.callback != null) {
            String typeString = this.typeTextField.getText().replace(" ", "");
            String nameString = StringUtils.convertFormatCodes(this.nameTextField.getText(), "§", "&");
            String rangeString = this.rangeTextField.getText().replace(" ", "");
            PropertiesSection sec = new PropertiesSection("condition-meta");
            sec.addEntry("entity_type", typeString);
            sec.addEntry("entity_name", nameString);
            if (!rangeString.equals("") && MathUtils.isInteger(rangeString)) {
                sec.addEntry("range", rangeString);
            } else {
                sec.addEntry("range", "");
            }
            this.callback.accept(sec);
        }
    }

}
