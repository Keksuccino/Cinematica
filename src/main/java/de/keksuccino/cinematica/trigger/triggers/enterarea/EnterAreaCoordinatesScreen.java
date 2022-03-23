package de.keksuccino.cinematica.trigger.triggers.enterarea;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.cinematica.gui.ScrollableScreen;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.utils.WorldUtils;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedTextField;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollAreaEntry;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.CharacterFilter;
import de.keksuccino.konkrete.input.MouseInput;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnterAreaCoordinatesScreen extends ScrollableScreen {

    protected Consumer<String> callback;

    protected AdvancedTextField fromXTextField;
    protected AdvancedTextField fromYTextField;
    protected AdvancedTextField fromZTextField;
    protected AdvancedTextField toXTextField;
    protected AdvancedTextField toYTextField;
    protected AdvancedTextField toZTextField;

    protected AdvancedTextField dimensionTextField;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public EnterAreaCoordinatesScreen(Screen parent, @Nullable String triggerValue, Consumer<String> callback) {

        super(parent, Locals.localize("cinematica.trigger.enterarea.definearea"));
        this.callback = callback;

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        fromXTextField = new AdvancedTextField(font, 0, 0, 50, 20, true, CharacterFilter.getIntegerCharacterFiler());
        fromXTextField.setMaxStringLength(100000);
        fromYTextField = new AdvancedTextField(font, 0, 0, 50, 20, true, CharacterFilter.getIntegerCharacterFiler());
        fromYTextField.setMaxStringLength(100000);
        fromZTextField = new AdvancedTextField(font, 0, 0, 50, 20, true, CharacterFilter.getIntegerCharacterFiler());
        fromZTextField.setMaxStringLength(100000);
        toXTextField = new AdvancedTextField(font, 0, 0, 50, 20, true, CharacterFilter.getIntegerCharacterFiler());
        toXTextField.setMaxStringLength(100000);
        toYTextField = new AdvancedTextField(font, 0, 0, 50, 20, true, CharacterFilter.getIntegerCharacterFiler());
        toYTextField.setMaxStringLength(100000);
        toZTextField = new AdvancedTextField(font, 0, 0, 50, 20, true, CharacterFilter.getIntegerCharacterFiler());
        toZTextField.setMaxStringLength(100000);

        dimensionTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, CharacterFilter.getIntegerCharacterFiler());
        dimensionTextField.setMaxStringLength(100000);

        if (triggerValue != null) {
            if (triggerValue.contains(";")) {
                String[] values = triggerValue.split("[;]");
                if (values.length >= 3) {
                    String fromString = values[0];
                    String toString = values[1];
                    String dimensionString = values[2];
                    if (fromString.contains(",") && toString.contains(",")) {
                        String[] from = fromString.split("[,]");
                        String[] to = toString.split("[,]");
                        if ((from.length == 3) && (to.length == 3)) {
                            if (MathUtils.isInteger(from[0]) && MathUtils.isInteger(from[1]) && MathUtils.isInteger(from[2]) && MathUtils.isInteger(to[0]) && MathUtils.isInteger(to[1]) && MathUtils.isInteger(to[2])) {
                                fromXTextField.setText(from[0]);
                                fromYTextField.setText(from[1]);
                                fromZTextField.setText(from[2]);
                                toXTextField.setText(to[0]);
                                toYTextField.setText(to[1]);
                                toZTextField.setText(to[2]);
                            }
                        }
                    }
                    if (!dimensionString.replace(" ", "").equals("")) {
                        dimensionTextField.setText(dimensionString);
                    }
                }
            }
        }
        if ((fromXTextField.getText() == null) || fromXTextField.getText().equals("")) {
            fromXTextField.setText("0");
            fromYTextField.setText("0");
            fromZTextField.setText("0");
            toXTextField.setText("0");
            toYTextField.setText("0");
            toZTextField.setText("0");
        }
        if ((dimensionTextField.getText() == null) || dimensionTextField.getText().equals("")) {
            String curDim = WorldUtils.getCurrentDimensionKey();
            if (curDim != null) {
                dimensionTextField.setText(curDim);
            }
        }

    }

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

        // FROM COORDINATES -------------------
        ScrollAreaEntryBase fromEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {

            int xCenter = render.entry.x + (render.entry.getWidth() / 2);

            drawCenteredString(render.matrix, font, Locals.localize("cinematica.trigger.enterarea.definearea.from"), xCenter, render.entry.y + 4, -1);

            this.fromYTextField.setX(xCenter - (this.fromYTextField.getWidth() / 2) + 5);
            this.fromYTextField.setY(render.entry.y + render.entry.getHeight() - this.fromYTextField.getHeight() - 2);
            this.fromYTextField.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            drawCenteredString(render.matrix, font, "§lY:", this.fromYTextField.x - 7, this.fromYTextField.y + 7, -1);

            this.fromXTextField.setX(this.fromYTextField.x - 5 - 10 - this.fromXTextField.getWidth());
            this.fromXTextField.setY(render.entry.y + render.entry.getHeight() - this.fromXTextField.getHeight() - 2);
            this.fromXTextField.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            drawCenteredString(render.matrix, font, "§lX:", this.fromXTextField.x - 7, this.fromXTextField.y + 7, -1);

            this.fromZTextField.setX(this.fromYTextField.x + this.fromYTextField.getWidth() + 5 + 10);
            this.fromZTextField.setY(render.entry.y + render.entry.getHeight() - this.fromZTextField.getHeight() - 2);
            this.fromZTextField.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            drawCenteredString(render.matrix, font, "§lZ:", this.fromZTextField.x - 7, this.fromZTextField.y + 7, -1);

        });
        fromEntry.setHeight(38);
        this.scrollArea.addEntry(fromEntry);
        //-------------------------------------

        // TO COORDINATES ---------------------
        ScrollAreaEntryBase toEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {

            int xCenter = render.entry.x + (render.entry.getWidth() / 2);

            drawCenteredString(render.matrix, font, Locals.localize("cinematica.trigger.enterarea.definearea.to"), xCenter, render.entry.y + 4, -1);

            this.toYTextField.setX(xCenter - (this.toYTextField.getWidth() / 2) + 5);
            this.toYTextField.setY(render.entry.y + render.entry.getHeight() - this.toYTextField.getHeight() - 2);
            this.toYTextField.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            drawCenteredString(render.matrix, font, "§lY:", this.toYTextField.x - 7, this.toYTextField.y + 7, -1);

            this.toXTextField.setX(this.toYTextField.x - 5 - 10 - this.toXTextField.getWidth());
            this.toXTextField.setY(render.entry.y + render.entry.getHeight() - this.toXTextField.getHeight() - 2);
            this.toXTextField.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            drawCenteredString(render.matrix, font, "§lX:", this.toXTextField.x - 7, this.toXTextField.y + 7, -1);

            this.toZTextField.setX(this.toYTextField.x + this.toYTextField.getWidth() + 5 + 10);
            this.toZTextField.setY(render.entry.y + render.entry.getHeight() - this.toZTextField.getHeight() - 2);
            this.toZTextField.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            drawCenteredString(render.matrix, font, "§lZ:", this.toZTextField.x - 7, this.toZTextField.y + 7, -1);

        });
        toEntry.setHeight(38);
        this.scrollArea.addEntry(toEntry);
        //-------------------------------------

        // DIMENSION --------------------------
        ScrollAreaEntryBase dimensionEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {

            int xCenter = render.entry.x + (render.entry.getWidth() / 2);

            drawCenteredString(render.matrix, font, Locals.localize("cinematica.trigger.enterarea.definearea.dimension"), xCenter, render.entry.y + 4, -1);

            this.dimensionTextField.setX(xCenter - (this.dimensionTextField.getWidth() / 2));
            this.dimensionTextField.setY(render.entry.y + render.entry.getHeight() - this.dimensionTextField.getHeight() - 13);
            this.dimensionTextField.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());

            drawCenteredString(render.matrix, font, Locals.localize("cinematica.trigger.enterarea.definearea.dimension.current", "" + WorldUtils.getCurrentDimensionKey()), xCenter, render.entry.y + render.entry.getHeight() - 2, -1);

        });
        dimensionEntry.setHeight(49);
        this.scrollArea.addEntry(dimensionEntry);
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
            String fromXString = this.fromXTextField.getText().replace(" ", "");
            String fromYString = this.fromYTextField.getText().replace(" ", "");
            String fromZString = this.fromZTextField.getText().replace(" ", "");
            String toXString = this.toXTextField.getText().replace(" ", "");
            String toYString = this.toYTextField.getText().replace(" ", "");
            String toZString = this.toZTextField.getText().replace(" ", "");
            String dimString = this.dimensionTextField.getText().replace(" ", "");
            if (!MathUtils.isInteger(fromXString) || !MathUtils.isInteger(fromYString) || !MathUtils.isInteger(fromZString) || !MathUtils.isInteger(toXString) || !MathUtils.isInteger(toYString) || !MathUtils.isInteger(toZString)) {
                this.callback.accept(null);
                //TODO remove debug
                Cinematica.LOGGER.info("############### CALLBACK: NULL");
            } else {
                String fromString = fromXString + "," + fromYString + "," + fromZString;
                String toString = toXString + "," + toYString + "," + toZString;
                String valueString = fromString + ";" + toString + ";" + dimString;
                this.callback.accept(valueString);
            }
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

}
