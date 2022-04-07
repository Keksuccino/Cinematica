package de.keksuccino.cinematica.engine.condition.conditions.chat;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.cinematica.gui.ScrollableScreen;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedTextField;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollAreaEntry;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatConditionScreen extends ScrollableScreen {

    protected Consumer<PropertiesSection> callback;

    protected String contentCheckType = "equals";

    protected AdvancedTextField contentTextField;

    protected AdvancedButton contentCheckTypeButton;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public ChatConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, Locals.localize("cinematica.condition.configure"));
        this.callback = callback;

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        contentTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        contentTextField.setMaxStringLength(100000);

        contentCheckTypeButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
            if (this.contentCheckType.equals("equals")) {
                this.contentCheckType = "starts-with";
            } else if (this.contentCheckType.equals("starts-with")) {
                this.contentCheckType = "ends-with";
            } else if (this.contentCheckType.equals("ends-with")) {
                this.contentCheckType = "contains";
            } else if (this.contentCheckType.equals("contains")) {
                this.contentCheckType = "equals";
            }
        }) {
            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                if (contentCheckType.equals("equals")) {
                    this.setMessage(Locals.localize("cinematica.condition.chat.conditionmeta.content.checktype.equals"));
                } else if (contentCheckType.equals("starts-with")) {
                    this.setMessage(Locals.localize("cinematica.condition.chat.conditionmeta.content.checktype.starts-with"));
                } else if (contentCheckType.equals("ends-with")) {
                    this.setMessage(Locals.localize("cinematica.condition.chat.conditionmeta.content.checktype.ends-with"));
                } else if (contentCheckType.equals("contains")) {
                    this.setMessage(Locals.localize("cinematica.condition.chat.conditionmeta.content.checktype.contains"));
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        };

        if (conditionMeta != null) {

            String contentString = conditionMeta.getEntryValue("content");
            if (contentString != null) {
                contentTextField.setText(contentString);
            }

            String checkTypeString = conditionMeta.getEntryValue("check_type");
            if ((checkTypeString != null) && (checkTypeString.equals("starts-with") || checkTypeString.equals("ends-with") || checkTypeString.equals("contains"))) {
                this.contentCheckType = checkTypeString;
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

        // CONTENT ----------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.chat.conditionmeta.content"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.contentTextField));
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.contentCheckTypeButton));
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
            String contentString = this.contentTextField.getText();
            PropertiesSection sec = new PropertiesSection("condition-meta");
            sec.addEntry("content", contentString);
            sec.addEntry("check_type", this.contentCheckType);
            this.callback.accept(sec);
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

}
