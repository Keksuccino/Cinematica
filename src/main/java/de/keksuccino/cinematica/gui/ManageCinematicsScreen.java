package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.cinematica.engine.cinematic.CinematicHandler;
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

public class ManageCinematicsScreen extends Screen {

    protected static final Color BACKGROUND_COLOR = new Color(38, 38, 38);

    protected Screen parent;

    protected AdvancedButton addCinematicButton;
    protected AdvancedButton removeCinematicButton;
    protected AdvancedButton editCinematicButton;
    protected AdvancedButton backButton;

    public ManageCinematicsScreen(Screen parent) {

        super(Component.literal(""));

        this.parent = parent;

        this.addCinematicButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.cinematic.add"), true, (press) -> {
            SelectCinematicTypeScreen s = new SelectCinematicTypeScreen(this, (call) -> {
                if (call != null) {
                    Minecraft.getInstance().setScreen(new EditCinematicScreen(this, call, (call2) -> {
                        if (call2 != null) {
                            CinematicHandler.addCinematic(call2);
                        }
                    }));
                }
            });
            Minecraft.getInstance().setScreen(s);
        });
        UIBase.colorizeButton(this.addCinematicButton);

        this.removeCinematicButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.cinematic.remove"), true, (press) -> {
            SelectCinematicScreen s = new SelectCinematicScreen(this, (call) -> {
                if (call != null) {
                    CinematicaYesNoPopup p = new CinematicaYesNoPopup(300, new Color(0, 0, 0, 0), 240, (call2) -> {
                        if (call2) {
                            CinematicHandler.removeCinematic(call);
                            Minecraft.getInstance().setScreen(this);
                        }
                    }, StringUtils.splitLines(Locals.localize("cinematica.cinematic.remove.confirm"), "%n%"));
                    PopupHandler.displayPopup(p);
                }
            });
            Minecraft.getInstance().setScreen(s);
        });
        UIBase.colorizeButton(this.removeCinematicButton);

        this.editCinematicButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.cinematic.edit"), true, (press) -> {
            SelectCinematicScreen s = new SelectCinematicScreen(this, (call) -> {
                if (call != null) {
                    Minecraft.getInstance().setScreen(new EditCinematicScreen(this, call, (call2) -> {
                        if (call2 != null) {
                            call.saveChanges();
                        }
                    }));
                }
            });
            Minecraft.getInstance().setScreen(s);
        });
        UIBase.colorizeButton(this.editCinematicButton);

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

        drawCenteredString(matrix, font, Locals.localize("cinematica.controls.managecinematics"), this.width / 2, 20, -1);

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
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

}
