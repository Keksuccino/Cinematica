package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.cinematica.trigger.Trigger;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.ui.popup.CinematicaYesNoPopup;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;

public class ManageTriggerScreen extends Screen {

    protected static final Color BACKGROUND_COLOR = new Color(38, 38, 38);

    protected Screen parent;
    protected Trigger trigger;

    protected AdvancedButton addCinematicButton;
    protected AdvancedButton removeCinematicButton;
    protected AdvancedButton editCinematicButton;
    protected AdvancedButton backButton;

    public ManageTriggerScreen(Screen parent, Trigger trigger) {

        super(new StringTextComponent(""));

        this.parent = parent;
        this.trigger = trigger;

        this.addCinematicButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.trigger.ui.addcinematic"), true, (press) -> {
            SelectCinematicTypeScreen s = new SelectCinematicTypeScreen(this, (call) -> {
                if (call != null) {
                    Minecraft.getInstance().displayGuiScreen(this);
                    this.trigger.onAddCinematicButtonClick(this, call);
                }
            });
            Minecraft.getInstance().displayGuiScreen(s);
        });
        UIBase.colorizeButton(this.addCinematicButton);

        this.removeCinematicButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.trigger.ui.removecinematic"), true, (press) -> {
            SelectCinematicScreen s = new SelectCinematicScreen(this, this.trigger, (call) -> {
                if (call != null) {
                    CinematicaYesNoPopup p = new CinematicaYesNoPopup(300, new Color(0, 0, 0, 0), 240, (call2) -> {
                        if (call2) {
                            this.trigger.removeCinematic(call);
                            this.trigger.saveChanges();
                            Minecraft.getInstance().displayGuiScreen(this);
                        }
                    }, StringUtils.splitLines(Locals.localize("cinematica.trigger.ui.removecinematic.sure"), "%n%"));
                    PopupHandler.displayPopup(p);
                }
            });
            Minecraft.getInstance().displayGuiScreen(s);
        });
        UIBase.colorizeButton(this.removeCinematicButton);

        this.editCinematicButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.trigger.ui.editcinematics"), true, (press) -> {
            SelectCinematicScreen s = new SelectCinematicScreen(this, this.trigger, (call) -> {
                if (call != null) {
                    this.trigger.onEditCinematicButtonClick(this, call);
                }
            });
            Minecraft.getInstance().displayGuiScreen(s);
        });
        UIBase.colorizeButton(this.editCinematicButton);

        this.backButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.trigger.ui.back"), true, (press) -> {
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

        drawCenteredString(matrix, font, Locals.localize("cinematica.trigger.ui.managetrigger"), this.width / 2, 20, -1);

        this.addCinematicButton.setX(xCenter - (this.addCinematicButton.getWidth() / 2));
        this.addCinematicButton.setY(yCenter - 35);
        this.addCinematicButton.render(matrix, mouseX, mouseY, partialTicks);

        this.removeCinematicButton.setX(xCenter - (this.removeCinematicButton.getWidth() / 2));
        this.removeCinematicButton.setY(yCenter - 10);
        this.removeCinematicButton.render(matrix, mouseX, mouseY, partialTicks);

        this.editCinematicButton.setX(xCenter - (this.editCinematicButton.getWidth() / 2));
        this.editCinematicButton.setY(yCenter + 15);
        this.editCinematicButton.render(matrix, mouseX, mouseY, partialTicks);

        this.backButton.setX(xCenter - (this.backButton.getWidth() / 2));
        this.backButton.setY(this.height - 35);
        this.backButton.render(matrix, mouseX, mouseY, partialTicks);

    }

    @Override
    public void closeScreen() {
        Minecraft.getInstance().displayGuiScreen(this.parent);
    }

}
