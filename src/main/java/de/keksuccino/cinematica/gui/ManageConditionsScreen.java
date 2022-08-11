package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.ui.popup.CinematicaYesNoPopup;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.Color;

public class ManageConditionsScreen extends Screen {

    protected static final Color BACKGROUND_COLOR = new Color(38, 38, 38);

    protected Screen parent;
    protected Cinematic cinematic;

    protected AdvancedButton addConditionButton;
    protected AdvancedButton removeConditionButton;
    protected AdvancedButton editConditionButton;
    protected AdvancedButton backButton;

    public ManageConditionsScreen(Screen parent, Cinematic cinematic) {

        super(Component.literal(""));

        this.parent = parent;
        this.cinematic = cinematic;

        this.addConditionButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.condition.add"), true, (press) -> {
            SelectConditionFactoryScreen s = new SelectConditionFactoryScreen(this, (call) -> {
                if (call != null) {
                    call.onAddConditionButtonClick(this.addConditionButton, this, this.cinematic);
                }
            });
            Minecraft.getInstance().setScreen(s);
        });
        UIBase.colorizeButton(this.addConditionButton);

        this.removeConditionButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.condition.remove"), true, (press) -> {
            SelectConditionScreen s = new SelectConditionScreen(this, this.cinematic, false, (call) -> {
                if (call != null) {
                    CinematicaYesNoPopup p = new CinematicaYesNoPopup(300, new Color(0, 0, 0, 0), 240, (call2) -> {
                        if (call2) {
                            this.cinematic.removeCondition(call);
                            Minecraft.getInstance().setScreen(this);
                        }
                    }, StringUtils.splitLines(Locals.localize("cinematica.condition.remove.confirm"), "%n%"));
                    PopupHandler.displayPopup(p);
                }
            });
            Minecraft.getInstance().setScreen(s);
        });
        UIBase.colorizeButton(this.removeConditionButton);

        this.editConditionButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.condition.edit"), true, (press) -> {
            SelectConditionScreen s = new SelectConditionScreen(this, this.cinematic, true, (call) -> {
                if (call != null) {
                    call.parent.onEditConditionButtonClick(this.editConditionButton, this, call, this.cinematic);
                }
            });
            Minecraft.getInstance().setScreen(s);
        });
        UIBase.colorizeButton(this.editConditionButton);

        this.backButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.ui.back"), true, (press) -> {
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

        drawCenteredString(matrix, font, Locals.localize("cinematica.condition.manage"), this.width / 2, 20, -1);

        this.addConditionButton.setX(xCenter - (this.addConditionButton.getWidth() / 2));
        this.addConditionButton.setY(yCenter - 35);
        this.addConditionButton.render(matrix, mouseX, mouseY, partialTicks);

        this.removeConditionButton.setX(xCenter - (this.removeConditionButton.getWidth() / 2));
        this.removeConditionButton.setY(yCenter - 10);
        this.removeConditionButton.render(matrix, mouseX, mouseY, partialTicks);

        this.editConditionButton.setX(xCenter - (this.editConditionButton.getWidth() / 2));
        this.editConditionButton.setY(yCenter + 15);
        this.editConditionButton.render(matrix, mouseX, mouseY, partialTicks);

        this.backButton.setX(xCenter - (this.backButton.getWidth() / 2));
        this.backButton.setY(this.height - 35);
        this.backButton.render(matrix, mouseX, mouseY, partialTicks);

    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

}
