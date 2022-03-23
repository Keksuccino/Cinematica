package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.cinematica.audio.AudioCinematicVolumeHandler;
import de.keksuccino.cinematica.ui.slider.AdvancedVolumeSlider;
import de.keksuccino.cinematica.video.VideoVolumeHandler;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CinematicVolumeScreen extends SettingsScreen {

    public CinematicVolumeScreen(Screen parent) {
        super(parent, Minecraft.getInstance().gameSettings, new StringTextComponent(Locals.localize("cinematica.audiochannel")));
    }

    protected void init() {

        int xCenter = this.width / 2;
        int yCenter = this.height / 2;

        //Cutscene Volume
        this.addButton(new AdvancedVolumeSlider(xCenter - 100, yCenter - 23, 200, 20, Locals.localize("cinematica.audiochannel.cutscenes"), VideoVolumeHandler.getVolume(), (call) -> {
            VideoVolumeHandler.setVolume(call);
        }));

        //Audio Cinematic Volume
        this.addButton(new AdvancedVolumeSlider(xCenter - 100, yCenter + 2, 200, 20, Locals.localize("cinematica.audiochannel.audios"), AudioCinematicVolumeHandler.getVolume(), (call) -> {
            AudioCinematicVolumeHandler.setVolume(call);
        }));

        this.addButton(new Button(xCenter - 100, this.height / 6 + 168, 200, 20, DialogTexts.GUI_DONE, (p_213104_1_) -> {
            this.minecraft.displayGuiScreen(this.parentScreen);
        }));

    }

    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        drawCenteredString(matrix, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

}