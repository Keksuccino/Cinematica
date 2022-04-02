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

public class EntitySpottingConditionScreen extends EntityRangeConditionScreen {

    protected AdvancedTextField fovOffsetTextField;

    public EntitySpottingConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, conditionMeta, callback);

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        fovOffsetTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, CharacterFilter.getDoubleCharacterFiler());
        fovOffsetTextField.setMaxStringLength(100000);

        if (conditionMeta != null) {

            String offsetString = conditionMeta.getEntryValue("fov_offset");
            if ((offsetString != null) && MathUtils.isDouble(offsetString)) {
                fovOffsetTextField.setText(offsetString);
            } else {
                fovOffsetTextField.setText("0.0");
            }

        }

    }

    @Override
    protected void init() {

        super.init();

        // FOV OFFSET -------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.entity.spotselection.conditionmeta.fov_offset"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.fovOffsetTextField));
        //-------------------------------------

    }

    @Override
    protected void onDone() {
        if (this.callback != null) {
            String typeString = this.typeTextField.getText().replace(" ", "");
            String nameString = StringUtils.convertFormatCodes(this.nameTextField.getText(), "§", "&");
            String rangeString = this.rangeTextField.getText().replace(" ", "");
            String offsetString = this.fovOffsetTextField.getText().replace(" ", "");
            PropertiesSection sec = new PropertiesSection("condition-meta");
            sec.addEntry("entity_type", typeString);
            sec.addEntry("entity_name", nameString);
            if (!rangeString.equals("") && MathUtils.isInteger(rangeString)) {
                sec.addEntry("range", rangeString);
            } else {
                sec.addEntry("range", "");
            }
            if (!offsetString.equals("") && MathUtils.isDouble(offsetString)) {
                sec.addEntry("fov_offset", offsetString);
            } else {
                sec.addEntry("fov_offset", "0.0");
            }
            this.callback.accept(sec);
        }
    }
}
