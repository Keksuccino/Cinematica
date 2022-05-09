package de.keksuccino.cinematica.engine.condition.conditions.entity.killentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.cinematica.gui.ScrollableScreen;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedTextField;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollAreaEntry;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KillEntityConditionScreen extends ScrollableScreen {

    protected Consumer<PropertiesSection> callback;

    protected AdvancedTextField typeTextField;
    protected AdvancedTextField nameTextField;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public KillEntityConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, Locals.localize("cinematica.condition.configure"));
        this.callback = callback;

        Font font = Minecraft.getInstance().font;

        typeTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        typeTextField.setMaxLength(100000);

        nameTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        nameTextField.setMaxLength(100000);

        if (conditionMeta != null) {

            String typeString = conditionMeta.getEntryValue("entity_type");
            if ((typeString != null) && !typeString.replace(" ", "").equals("")) {
                typeTextField.setValue(typeString);
            }

            String nameString = conditionMeta.getEntryValue("entity_name");
            if (nameString != null) {
                nameTextField.setValue(nameString);
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
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.killentity.conditionmeta.type"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.typeTextField));
        String lastKilledType = "---";
        if (KillEntityConditionFactory.lastKilledEntity != null) {
            lastKilledType = KillEntityConditionFactory.lastKilledEntity.getType().getRegistryName().getNamespace() + ":" + KillEntityConditionFactory.lastKilledEntity.getType().getRegistryName().getPath();
        }
        TextEntry lastKilledTypeEntry = new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.killentity.conditionmeta.type.lastkilled", lastKilledType), false);
        lastKilledTypeEntry.setHeight(14);
        this.scrollArea.addEntry(lastKilledTypeEntry);
        //-------------------------------------

        // NAME -------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.killentity.conditionmeta.name"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.nameTextField));
        String lastKilledName = "---";
        if (KillEntityConditionFactory.lastKilledEntity != null) {
            lastKilledName = StringUtils.convertFormatCodes(KillEntityConditionFactory.lastKilledEntity.getDisplayName().getString(), "§", "&");
        }
        TextEntry lastKilledNameEntry = new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.killentity.conditionmeta.name.lastkilled", lastKilledName), false);
        lastKilledNameEntry.setHeight(14);
        this.scrollArea.addEntry(lastKilledNameEntry);
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
            PropertiesSection sec = new PropertiesSection("condition-meta");
            sec.addEntry("entity_type", typeString);
            sec.addEntry("entity_name", nameString);
            this.callback.accept(sec);
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

}
