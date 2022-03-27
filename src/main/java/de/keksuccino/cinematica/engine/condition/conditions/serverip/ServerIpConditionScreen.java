package de.keksuccino.cinematica.engine.condition.conditions.serverip;

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

public class ServerIpConditionScreen extends ScrollableScreen {

    protected Consumer<PropertiesSection> callback;

    protected AdvancedTextField ipTextField;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public ServerIpConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, Locals.localize("cinematica.condition.configure"));
        this.callback = callback;

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        ipTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        ipTextField.setMaxStringLength(100000);

        if (conditionMeta != null) {

            String ipString = conditionMeta.getEntryValue("ip");
            if ((ipString != null) && !ipString.replace(" ", "").equals("")) {
                ipTextField.setText(ipString);
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

        // IP ---------------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.serverip.conditionmeta.ip"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.ipTextField));
        String currentIpString = "---";
        if (Minecraft.getInstance().getCurrentServerData() != null) {
            currentIpString = Minecraft.getInstance().getCurrentServerData().serverIP;
            if (currentIpString == null) {
                currentIpString = "---";
            }
        }
        TextEntry currentIpEntry = new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.serverip.conditionmeta.ip.current", currentIpString), false);
        currentIpEntry.setHeight(14);
        this.scrollArea.addEntry(currentIpEntry);
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
            String ipString = this.ipTextField.getText().replace(" ", "");
            PropertiesSection sec = new PropertiesSection("condition-meta");
            sec.addEntry("ip", ipString);
            this.callback.accept(sec);
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

}
