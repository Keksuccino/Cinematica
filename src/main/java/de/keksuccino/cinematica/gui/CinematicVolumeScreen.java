package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.cinematica.audio.AudioCinematicVolumeHandler;
import de.keksuccino.cinematica.ui.slider.AdvancedVolumeSlider;
import de.keksuccino.cinematica.video.VideoVolumeHandler;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CinematicVolumeScreen extends OptionsSubScreen {

    public CinematicVolumeScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.literal(Locals.localize("cinematica.audiochannel")));
    }

    protected void init() {

        int xCenter = this.width / 2;
        int yCenter = this.height / 2;

        //Cutscene Volume
        this.addRenderableWidget(new AdvancedVolumeSlider(xCenter - 100, yCenter - 23, 200, 20, Locals.localize("cinematica.audiochannel.cutscenes"), VideoVolumeHandler.getVolume(), (call) -> {
            VideoVolumeHandler.setVolume(call);
        }));

        //Audio Cinematic Volume
        this.addRenderableWidget(new AdvancedVolumeSlider(xCenter - 100, yCenter + 2, 200, 20, Locals.localize("cinematica.audiochannel.audios"), AudioCinematicVolumeHandler.getVolume(), (call) -> {
            AudioCinematicVolumeHandler.setVolume(call);
        }));

        this.addRenderableWidget(new Button(xCenter - 100, this.height - 35, 200, 20, CommonComponents.GUI_DONE, (press) -> {
            this.minecraft.setScreen(this.lastScreen);
        }));

    }

    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        drawCenteredString(matrix, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

}