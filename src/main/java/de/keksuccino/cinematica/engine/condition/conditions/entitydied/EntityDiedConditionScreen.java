package de.keksuccino.cinematica.engine.condition.conditions.entitydied;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.cinematica.gui.ScrollableScreen;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedTextField;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollAreaEntry;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.CharacterFilter;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EntityDiedConditionScreen extends ScrollableScreen {

    protected Consumer<PropertiesSection> callback;

    protected AdvancedTextField typeTextField;
    protected AdvancedTextField nameTextField;
    protected AdvancedTextField rangeTextField;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public EntityDiedConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, Locals.localize("cinematica.condition.configure"));
        this.callback = callback;

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        typeTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        typeTextField.setMaxStringLength(100000);

        nameTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        nameTextField.setMaxStringLength(100000);

        rangeTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, CharacterFilter.getIntegerCharacterFiler());
        rangeTextField.setMaxStringLength(100000);

        if (conditionMeta != null) {

            String typeString = conditionMeta.getEntryValue("entity_type");
            if ((typeString != null) && !typeString.replace(" ", "").equals("")) {
                typeTextField.setText(typeString);
            }

            String nameString = conditionMeta.getEntryValue("entity_name");
            if (nameString != null) {
                nameTextField.setText(nameString);
            }

            String rangeString = conditionMeta.getEntryValue("range");
            if ((rangeString != null) && MathUtils.isInteger(rangeString)) {
                rangeTextField.setText(rangeString);
            }

        }

    }

    @Override
    public boolean isOverlayButtonHovered() {
        if (this.doneButton != null) {
            if (this.doneButton.isHovered()) {
                return true;
            }
        }
        if (this.cancelButton != null) {
            if (this.cancelButton.isHovered()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void init() {

        super.init();

        //Clear entry list on init
        List<ScrollAreaEntry> oldEntries = new ArrayList<>();
        oldEntries.addAll(this.scrollArea.getEntries());
        for (ScrollAreaEntry e : oldEntries) {
            this.scrollArea.removeEntry(e);
        }

        // TYPE -------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.entitydied.conditionmeta.type"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.typeTextField));
        //-------------------------------------

        // NAME -------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.entitydied.conditionmeta.name"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.nameTextField));
        //-------------------------------------

        // RANGE ------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.entitydied.conditionmeta.range"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.rangeTextField));
        //-------------------------------------

        this.cancelButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("cinematica.ui.cancel"), true, (press) -> {
            this.onCancel();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.cancelButton);

        this.doneButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("popup.done"), true, (press) -> {
            this.onDone();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.doneButton);

    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        int xCenter = this.width / 2;

        super.render(matrix, mouseX, mouseY, partialTicks);

        //Cancel Button
        this.cancelButton.setX(xCenter - this.cancelButton.getWidth() - 5);
        this.cancelButton.setY(this.height - 35);
        this.cancelButton.render(matrix, mouseX, mouseY, partialTicks);

        //Done Button
        this.doneButton.setX(xCenter + 5);
        this.doneButton.setY(this.height - 35);
        this.doneButton.render(matrix, mouseX, mouseY, partialTicks);

    }

    @Override
    public void closeScreen() {
        if (!PopupHandler.isPopupActive()) {
            this.onCancel();
            super.closeScreen();
        }
    }

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

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

}
