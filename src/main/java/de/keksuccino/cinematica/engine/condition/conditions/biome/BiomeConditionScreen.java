package de.keksuccino.cinematica.engine.condition.conditions.biome;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BiomeConditionScreen extends ScrollableScreen {

    protected Consumer<PropertiesSection> callback;

    protected AdvancedTextField biomeTextField;

    protected AdvancedButton cancelButton;
    protected AdvancedButton doneButton;

    public BiomeConditionScreen(Screen parent, @Nullable PropertiesSection conditionMeta, Consumer<PropertiesSection> callback) {

        super(parent, Locals.localize("cinematica.condition.configure"));
        this.callback = callback;

        FontRenderer font = Minecraft.getInstance().fontRenderer;

        biomeTextField = new AdvancedTextField(font, 0, 0, 200, 20, true, null);
        biomeTextField.setMaxStringLength(100000);

        if (conditionMeta != null) {
            String biomeString = conditionMeta.getEntryValue("biome");
            if ((biomeString != null) && !biomeString.replace(" ", "").equals("")) {
                biomeTextField.setText(biomeString);
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

        // BIOME -----------------------------
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.biome.conditionmeta.biome"), true));
        this.scrollArea.addEntry(new TextFieldEntry(this.scrollArea, this.biomeTextField));
        String currentBiomeString = "";
        try {
            currentBiomeString = Minecraft.getInstance().world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(Minecraft.getInstance().world.getBiome(Minecraft.getInstance().player.getPosition())).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextEntry currentBiomeEntry = new TextEntry(this.scrollArea, Locals.localize("cinematica.condition.biome.conditionmeta.biome.current", "" + currentBiomeString), false);
        currentBiomeEntry.setHeight(14);
        this.scrollArea.addEntry(currentBiomeEntry);
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
            String biomeString = this.biomeTextField.getText().replace(" ", "");
            PropertiesSection sec = new PropertiesSection("condition-meta");
            sec.addEntry("biome", biomeString);
            this.callback.accept(sec);
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

}
