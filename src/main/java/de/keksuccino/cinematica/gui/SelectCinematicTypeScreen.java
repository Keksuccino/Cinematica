package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.cinematica.trigger.CinematicType;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.function.Consumer;

public class SelectCinematicTypeScreen extends Screen {

    protected static final Color BACKGROUND_COLOR = new Color(38, 38, 38);

    protected Screen parent;
    protected Consumer<CinematicType> callback;

    protected AdvancedButton cutsceneTypeButton;
    protected AdvancedButton audioTypeButton;

    protected AdvancedButton backButton;

    protected SelectCinematicTypeScreen(Screen parent, Consumer<CinematicType> callback) {
        super(new StringTextComponent(""));
        this.parent = parent;
        this.callback = callback;

        this.cutsceneTypeButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.cinematic.type.cutscene"), true, (press) -> {
            this.onSelection(CinematicType.CUTSCENE);
        });
        UIBase.colorizeButton(this.cutsceneTypeButton);

        this.audioTypeButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.cinematic.type.audio"), true, (press) -> {
            this.onSelection(CinematicType.AUDIO);
        });
        UIBase.colorizeButton(this.audioTypeButton);

        this.backButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.trigger.ui.back"), true, (press) -> {
            this.onCancel();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.backButton);

    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        int xCenter = this.width / 2;
        int yCenter = this.height / 2;

        RenderSystem.enableBlend();

        fill(matrix, 0, 0, this.width, this.height, BACKGROUND_COLOR.getRGB());

        drawCenteredString(matrix, font, Locals.localize("cinematica.cinematic.type.selecttype"), this.width / 2, 20, -1);

        this.cutsceneTypeButton.setX(xCenter - (this.cutsceneTypeButton.getWidth() / 2));
        this.cutsceneTypeButton.setY(yCenter - 23);
        this.cutsceneTypeButton.render(matrix, mouseX, mouseY, partialTicks);

        this.audioTypeButton.setX(xCenter - (this.audioTypeButton.getWidth() / 2));
        this.audioTypeButton.setY(yCenter + 2);
        this.audioTypeButton.render(matrix, mouseX, mouseY, partialTicks);

        this.backButton.setX(xCenter - (this.backButton.getWidth() / 2));
        this.backButton.setY(this.height - 35);
        this.backButton.render(matrix, mouseX, mouseY, partialTicks);

    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

    protected void onSelection(CinematicType type) {
        if (this.callback != null) {
            this.callback.accept(type);
        }
    }

    @Override
    public void closeScreen() {
        this.onCancel();
        Minecraft.getInstance().displayGuiScreen(this.parent);
    }

}
