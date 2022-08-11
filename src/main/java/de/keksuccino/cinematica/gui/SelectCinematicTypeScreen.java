package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.cinematica.engine.cinematic.CinematicType;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.function.Consumer;

public class SelectCinematicTypeScreen extends Screen {

    protected static final Color BACKGROUND_COLOR = new Color(38, 38, 38);

    protected Screen parent;
    protected Consumer<CinematicType> callback;

    protected AdvancedButton cutsceneTypeButton;
    protected AdvancedButton audioTypeButton;

    protected AdvancedButton backButton;

    protected SelectCinematicTypeScreen(Screen parent, Consumer<CinematicType> callback) {
        super(Component.literal(""));
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

        this.backButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.ui.back"), true, (press) -> {
            this.onCancel();
            Minecraft.getInstance().setScreen(this.parent);
        });
        UIBase.colorizeButton(this.backButton);

    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {

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
    public void onClose() {
        this.onCancel();
        Minecraft.getInstance().setScreen(this.parent);
    }

}
