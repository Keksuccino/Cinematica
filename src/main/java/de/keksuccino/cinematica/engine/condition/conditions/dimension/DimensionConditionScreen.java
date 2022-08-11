package de.keksuccino.cinematica.engine.condition.conditions.dimension;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.cinematica.gui.ScrollableScreen;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.utils.WorldUtils;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedTextField;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollAreaEntry;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DimensionConditionScreen extends ScrollableScreen {

    protected Consumer<PropertiesSection> callback;

    protected AdvancedTextField dimensionTextField;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public DimensionConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, Locals.localize("cinematica.condition.configure"));
        this.callback = callback;

        Font font = Minecraft.getInstance().font;

        dimensionTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        dimensionTextField.setMaxLength(100000);

        if (conditionMeta != null) {
            String dimensionString = conditionMeta.getEntryValue("dimension");
            if ((dimensionString != null) && !dimensionString.replace(" ", "").equals("") && !dimensionString.equals("cinematica.blankdimension")) {
                dimensionTextField.setValue(dimensionString);
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

        // DIMENSION --------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.dimension.conditionmeta.dimension"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.dimensionTextField));
        TextEntry currentDimensionEntry = new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.dimension.conditionmeta.dimension.current", "" + WorldUtils.getCurrentDimensionKey()), false);
        currentDimensionEntry.setHeight(14);
        this.scrollArea.addEntry(currentDimensionEntry);
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
            String dimString = this.dimensionTextField.getValue().replace(" ", "");
            if (dimString.equals("")) {
                dimString = "cinematica.blankdimension";
            }
            PropertiesSection sec = new PropertiesSection("condition-meta");
            sec.addEntry("dimension", dimString);
            this.callback.accept(sec);
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

}
