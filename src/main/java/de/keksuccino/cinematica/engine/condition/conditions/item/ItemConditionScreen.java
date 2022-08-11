package de.keksuccino.cinematica.engine.condition.conditions.item;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemConditionScreen extends ScrollableScreen {

    protected Consumer<PropertiesSection> callback;

    protected String loreCheckType = "equals";

    protected AdvancedTextField typeTextField;
    protected AdvancedTextField nameTextField;
    protected AdvancedTextField loreTextField;
    protected AdvancedTextField countTextField;

    protected AdvancedButton loreCheckTypeButton;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public ItemConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, Locals.localize("cinematica.condition.configure"));
        this.callback = callback;

        Font font = Minecraft.getInstance().font;

        typeTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        typeTextField.setMaxLength(100000);

        nameTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        nameTextField.setMaxLength(100000);

        loreTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        loreTextField.setMaxLength(100000);

        countTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, CharacterFilter.getIntegerCharacterFiler());
        countTextField.setMaxLength(100000);

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
            public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
                if (loreCheckType.equals("equals")) {
                    this.setMessage(Locals.localize("cinematica.condition.item.conditionmeta.lore.checktype.equals"));
                } else if (loreCheckType.equals("starts-with")) {
                    this.setMessage(Locals.localize("cinematica.condition.item.conditionmeta.lore.checktype.starts-with"));
                } else if (loreCheckType.equals("ends-with")) {
                    this.setMessage(Locals.localize("cinematica.condition.item.conditionmeta.lore.checktype.ends-with"));
                } else if (loreCheckType.equals("contains")) {
                    this.setMessage(Locals.localize("cinematica.condition.item.conditionmeta.lore.checktype.contains"));
                }
                super.render(PoseStack, mouseX, mouseY, partialTicks);
            }
        };

        if (conditionMeta != null) {

            String typeString = conditionMeta.getEntryValue("item_type");
            if ((typeString != null) && !typeString.replace(" ", "").equals("")) {
                typeTextField.setValue(typeString);
            }

            String nameString = conditionMeta.getEntryValue("item_name");
            if (nameString != null) {
                nameTextField.setValue(nameString);
            }

            String loreString = conditionMeta.getEntryValue("item_lore");
            if (loreString != null) {
                loreTextField.setValue(loreString);
            }

            String countString = conditionMeta.getEntryValue("item_count");
            if ((countString != null) && MathUtils.isInteger(countString)) {
                countTextField.setValue(countString);
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
            if (this.doneButton.isHoveredOrFocused()) {
                return true;
            }
        }
        if (this.cancelButton != null) {
            if (this.cancelButton.isHoveredOrFocused()) {
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
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.item.conditionmeta.type"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.typeTextField));
        //-------------------------------------

        // NAME -------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.item.conditionmeta.name"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.nameTextField));
        //-------------------------------------

        // LORE -------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.item.conditionmeta.lore"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.loreTextField));
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.loreCheckTypeButton));
        //-------------------------------------

        // COUNT ------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.item.conditionmeta.count"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.countTextField));
        //-------------------------------------

        this.cancelButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("cinematica.ui.cancel"), true, (press) -> {
            this.onCancel();
            Minecraft.getInstance().setScreen(this.parent);
        });
        UIBase.colorizeButton(this.cancelButton);

        this.doneButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("popup.done"), true, (press) -> {
            this.onDone();
            Minecraft.getInstance().setScreen(this.parent);
        });
        UIBase.colorizeButton(this.doneButton);

    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {

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
    public void onClose() {
        if (!PopupHandler.isPopupActive()) {
            this.onCancel();
            super.onClose();
        }
    }

    protected void onDone() {
        if (this.callback != null) {
            String typeString = this.typeTextField.getValue().replace(" ", "");
            String nameString = StringUtils.convertFormatCodes(this.nameTextField.getValue(), "§", "&");
            String loreString = this.loreTextField.getValue();
            String countString = this.countTextField.getValue().replace(" ", "");
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
