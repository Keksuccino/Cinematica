package de.keksuccino.cinematica.engine.condition.conditions.tablist;

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

public class TabListConditionScreen extends ScrollableScreen {

    protected Consumer<PropertiesSection> callback;

    protected String loreCheckType = "equals";

    protected AdvancedTextField typeTextField;
    protected AdvancedTextField nameTextField;
    protected AdvancedTextField loreTextField;
    protected AdvancedTextField countTextField;

    protected AdvancedButton loreCheckTypeButton;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public TabListConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, Locals.localize("cinematica.condition.configure"));
        this.callback = callback;

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        typeTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        typeTextField.setMaxStringLength(100000);

        nameTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        nameTextField.setMaxStringLength(100000);

        loreTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        loreTextField.setMaxStringLength(100000);

        countTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, CharacterFilter.getIntegerCharacterFiler());
        countTextField.setMaxStringLength(100000);

        loreCheckTypeButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
            if (this.loreCheckType.equals("equals")) {
                this.loreCheckType = "starts-with";
            } else if (this.loreCheckType.equals("starts-with")) {
                this.loreCheckType = "ends-with";
            } else if (this.loreCheckType.equals("ends-with")) {
                this.loreCheckType = "contains";
            } else if (this.loreCheckType.equals("contains")) {
                this.loreCheckType = "equals";
            }
        }) {
            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                if (loreCheckType.equals("equals")) {
                    this.setMessage(Locals.localize("cinematica.condition.addtoinventory.conditionmeta.lore.checktype.equals"));
                } else if (loreCheckType.equals("starts-with")) {
                    this.setMessage(Locals.localize("cinematica.condition.addtoinventory.conditionmeta.lore.checktype.starts-with"));
                } else if (loreCheckType.equals("ends-with")) {
                    this.setMessage(Locals.localize("cinematica.condition.addtoinventory.conditionmeta.lore.checktype.ends-with"));
                } else if (loreCheckType.equals("contains")) {
                    this.setMessage(Locals.localize("cinematica.condition.addtoinventory.conditionmeta.lore.checktype.contains"));
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        };

        if (conditionMeta != null) {

            String typeString = conditionMeta.getEntryValue("item_type");
            if ((typeString != null) && !typeString.replace(" ", "").equals("")) {
                typeTextField.setText(typeString);
            }

            String nameString = conditionMeta.getEntryValue("item_name");
            if (nameString != null) {
                nameTextField.setText(nameString);
            }

            String loreString = conditionMeta.getEntryValue("item_lore");
            if (loreString != null) {
                loreTextField.setText(loreString);
            }

            String countString = conditionMeta.getEntryValue("item_count");
            if ((countString != null) && MathUtils.isInteger(countString)) {
                countTextField.setText(countString);
            }

            String loreCheckTypeString = conditionMeta.getEntryValue("item_lore_check_type");
            if ((loreCheckTypeString != null) && (loreCheckTypeString.equals("starts-with") || loreCheckTypeString.equals("ends-with") || loreCheckTypeString.equals("contains"))) {
                this.loreCheckType = loreCheckTypeString;
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
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.addtoinventory.conditionmeta.type"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.typeTextField));
        //-------------------------------------

        // NAME -------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.addtoinventory.conditionmeta.name"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.nameTextField));
        //-------------------------------------

        // LORE -------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.addtoinventory.conditionmeta.lore"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.loreTextField));
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.loreCheckTypeButton));
        //-------------------------------------

        // COUNT ------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.addtoinventory.conditionmeta.count"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.countTextField));
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
            String loreString = this.loreTextField.getText();
            String countString = this.countTextField.getText().replace(" ", "");
            PropertiesSection sec = new PropertiesSection("condition-meta");
            sec.addEntry("item_type", typeString);
            sec.addEntry("item_name", nameString);
            sec.addEntry("item_lore", loreString);
            sec.addEntry("item_lore_check_type", this.loreCheckType);
            if (!countString.equals("") && MathUtils.isInteger(countString)) {
                sec.addEntry("item_count", countString);
            } else {
                sec.addEntry("item_count", "");
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
