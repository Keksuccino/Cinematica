package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.utils.WorldUtils;
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

//TODO HIER WEITER MACHEN !!!!!
//TODO HIER WEITER MACHEN !!!!!
//TODO HIER WEITER MACHEN !!!!!
//TODO HIER WEITER MACHEN !!!!!

public class ConfigureConditionsScreen extends ScrollableScreen {

    protected Consumer<PropertiesSection> callback;

    protected AdvancedTextField dimensionTextField;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public ConfigureConditionsScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, Locals.localize("cinematica.trigger.conditionmeta.configure"));
        this.callback = callback;

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        dimensionTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        dimensionTextField.setMaxStringLength(100000);

        if (conditionMeta != null) {
            String dimensionString = conditionMeta.getEntryValue("dimension");
            if ((dimensionString != null) && !dimensionString.replace(" ", "").equals("") && !dimensionString.equals("cinematica.blankdimension")) {
                dimensionTextField.setText(dimensionString);
            }
        }

    }

    protected void preInit() {}

    protected void postInit() {}

    @Override
    protected void init() {

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        super.init();

        //Clear entry list on init
        List<ScrollAreaEntry> oldEntries = new ArrayList<>();
        oldEntries.addAll(this.scrollArea.getEntries());
        for (ScrollAreaEntry e : oldEntries) {
            this.scrollArea.removeEntry(e);
        }

        this.preInit();

        // DIMENSION --------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.trigger.enterarea.conditionmeta.dimension"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.dimensionTextField));
        TextEntry curDimTextEntry = new TextEntry(this.scrollArea, Locals.localize("cinematica.trigger.enterarea.conditionmeta.dimension.current", "" + WorldUtils.getCurrentDimensionKey()), false);
        curDimTextEntry.setHeight(13);
        this.scrollArea.addEntry(curDimTextEntry);
//        ScrollAreaEntryBase dimensionEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {
//
//            int xCenter = render.entry.x + (render.entry.getWidth() / 2);
//
//            drawCenteredString(render.matrix, font, Locals.localize("cinematica.trigger.enterarea.conditionmeta.dimension"), xCenter, render.entry.y + 4, -1);
//
//            this.dimensionTextField.setX(xCenter - (this.dimensionTextField.getWidth() / 2));
//            this.dimensionTextField.setY(render.entry.y + render.entry.getHeight() - this.dimensionTextField.getHeight() - 11);
//            this.dimensionTextField.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
//
//            drawCenteredString(render.matrix, font, Locals.localize("cinematica.trigger.enterarea.conditionmeta.dimension.current", "" + WorldUtils.getCurrentDimensionKey()), xCenter, render.entry.y + render.entry.getHeight() - 2, -1);
//
//        });
//        dimensionEntry.setHeight(47);
//        this.scrollArea.addEntry(dimensionEntry);
        //-------------------------------------

        this.cancelButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("cinematica.trigger.ui.cancel"), true, (press) -> {
            this.onCancel();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.cancelButton);

        this.doneButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("popup.done"), true, (press) -> {
            this.onDone();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.doneButton);

        this.postInit();

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

    @Nullable
    protected PropertiesSection buildConditionMeta() {

        PropertiesSection sec = new PropertiesSection("condition-meta");

        String dimString = this.dimensionTextField.getText().replace(" ", "");
        if (dimString.equals("")) {
            dimString = "cinematica.blankdimension";
        }
        sec.addEntry("dimension", dimString);

        return sec;

    }

    protected void onDone() {
        if (this.callback != null) {
            PropertiesSection sec = this.buildConditionMeta();
            this.callback.accept(sec);
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

}
