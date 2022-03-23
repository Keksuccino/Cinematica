package de.keksuccino.cinematica;

import de.keksuccino.auudio.audio.AudioClip;
import de.keksuccino.cinematica.audio.AudioCinematicHandler;
import de.keksuccino.cinematica.audio.VanillaAudioHandler;
import de.keksuccino.cinematica.gui.CinematicVolumeScreen;
import de.keksuccino.cinematica.gui.CinematicaConfigScreen;
import de.keksuccino.cinematica.gui.ManageTriggerScreen;
import de.keksuccino.cinematica.gui.SelectTriggerScreen;
import de.keksuccino.cinematica.trigger.CinematicHandler;
import de.keksuccino.cinematica.ui.CinematicaContextMenu;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.ui.popup.CinematicaYesNoPopup;
import de.keksuccino.cinematica.video.VideoVolumeHandler;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedImageButton;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.MouseInput;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.OptionsSoundsScreen;
import net.minecraft.client.gui.widget.SoundSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EventHandler {

    protected static final ResourceLocation CINEMATICA_BTN_TEXTURE = new ResourceLocation("cinematica", "textures/cinematica_button.png");

    AdvancedButton cinematicaButton;
    CinematicaContextMenu cinematicaControlsMenu;

    protected float lastMcMasterVolume = Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.MASTER);
    protected boolean resumeWorldSounds = false;
    protected World lastActiveWorld = null;
    protected boolean stoppedCinematicAudiosInMenu = false;

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post e) {

        if (this.cinematicaControlsMenu == null) {

            this.cinematicaControlsMenu = new CinematicaContextMenu();
            this.cinematicaControlsMenu.setAutoclose(true);

            AdvancedButton manageTriggersButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("cinematica.controls.managetriggers"), true, (press) -> {
                SelectTriggerScreen s = new SelectTriggerScreen(new IngameMenuScreen(true), (call) -> {
                    if (call != null) {
                        ManageTriggerScreen ms = new ManageTriggerScreen(Minecraft.getInstance().currentScreen, call);
                        Minecraft.getInstance().displayGuiScreen(ms);
                    }
                });
                Minecraft.getInstance().displayGuiScreen(s);
            });
            this.cinematicaControlsMenu.addContent(manageTriggersButton);

            AdvancedButton resetTriggeredOneTimeCinematicsButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("cinematica.controls.resetonetimecinematics"), true, (press) -> {
                CinematicaYesNoPopup p = new CinematicaYesNoPopup(300, new Color(0, 0, 0, 0), 240, (call) -> {
                    if (call) {
                        CinematicHandler.clearTriggeredOneTimeCinematics();
                    }
                }, StringUtils.splitLines(Locals.localize("cinematica.controls.resetonetimecinematics.confirm"), "%n%"));
                PopupHandler.displayPopup(p);
            });
            this.cinematicaControlsMenu.addContent(resetTriggeredOneTimeCinematicsButton);

            this.cinematicaControlsMenu.addSeparator();

            AdvancedButton settingsButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("cinematica.config"), true, (press) -> {
                Minecraft.getInstance().displayGuiScreen(new CinematicaConfigScreen(Minecraft.getInstance().currentScreen));
            });
            this.cinematicaControlsMenu.addContent(settingsButton);

        }

        if (this.cinematicaButton == null) {
            this.cinematicaButton = new AdvancedImageButton(0, 40, 50, 40, CINEMATICA_BTN_TEXTURE, true, (press) -> {
                UIBase.openScaledContextMenuAt(
                        this.cinematicaControlsMenu,
                        this.cinematicaButton.getX() + this.cinematicaButton.getWidth() + 2,
                        this.cinematicaButton.getY());
            });
            this.cinematicaButton.setDescription("Cinematica");
            UIBase.colorizeButton(this.cinematicaButton);
            this.cinematicaControlsMenu.setParentButton(this.cinematicaButton);
        }

        if ((e.getGui() instanceof IngameMenuScreen) && Cinematica.config.getOrDefault("show_controls_in_pause_screen", true) && !PopupHandler.isPopupActive()) {

            if (!this.cinematicaButton.isHovered()) {
                this.cinematicaButton.setX(-10);
            } else {
                this.cinematicaButton.setX(-2);
            }
            this.cinematicaButton.render(e.getMatrixStack(), e.getMouseX(), e.getMouseY(), e.getRenderPartialTicks());

            float scale = UIBase.getUIScale();
            MouseInput.setRenderScale(scale);
            e.getMatrixStack().push();
            e.getMatrixStack().scale(scale, scale, scale);
            this.cinematicaControlsMenu.render(e.getMatrixStack(), MouseInput.getMouseX(), MouseInput.getMouseY());
            e.getMatrixStack().pop();
            MouseInput.resetRenderScale();
            if (this.cinematicaControlsMenu.isOpen()) {
                MouseInput.blockVanillaInput("cinematica_controls_context_menu");
                UIBase.openScaledContextMenuAt(
                        this.cinematicaControlsMenu,
                        this.cinematicaButton.getX() + this.cinematicaButton.getWidth() + 2,
                        this.cinematicaButton.getY());
            } else {
                MouseInput.unblockVanillaInput("cinematica_controls_context_menu");
            }

        } else {
            if (this.cinematicaControlsMenu != null) {
                this.cinematicaControlsMenu.closeMenu();
            }
            MouseInput.unblockVanillaInput("cinematica_controls_context_menu");
        }

    }

    @SubscribeEvent
    public void onInitPost(GuiScreenEvent.InitGuiEvent.Post e) {

        if (e.getGui() instanceof OptionsSoundsScreen) {
            if (Cinematica.config.getOrDefault("add_slider_to_sound_controls", true)) {

                SoundSlider slider = getLastSlider(e.getWidgetList());
                if (slider != null) {
                    e.addWidget(new AdvancedButton(slider.x + slider.getWidth() + 10, slider.y, slider.getWidth(), slider.getHeight(), Locals.localize("cinematica.audiochannel"), false, (press) -> {
                        Minecraft.getInstance().displayGuiScreen(new CinematicVolumeScreen(e.getGui()));
                    }));
                }

            }
        }

    }

    protected static SoundSlider getLastSlider(List<Widget> widgets) {
        List<SoundSlider> l = new ArrayList<>();
        for (Widget w : widgets) {
            if (w instanceof SoundSlider) {
                l.add((SoundSlider)w);
            }
        }
        if (!l.isEmpty()) {
            return l.get(l.size()-1);
        }
        return null;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {

        if (Minecraft.getInstance().currentScreen == null) {
            if (this.stoppedCinematicAudiosInMenu) {
                AudioCinematicHandler.resumeUnfinishedAudios();
                this.stoppedCinematicAudiosInMenu = false;
            }
            if (this.cinematicaControlsMenu != null) {
                this.cinematicaControlsMenu.closeMenu();
            }
            MouseInput.unblockVanillaInput("cinematica_controls_context_menu");
        } else {
            if (!this.stoppedCinematicAudiosInMenu) {
                AudioCinematicHandler.pauseAll();
                this.stoppedCinematicAudiosInMenu = true;
            }
        }

        //Update cutscene volume on master volume change
        if (this.lastMcMasterVolume != Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.MASTER)) {
            VideoVolumeHandler.updateVolume();
        }
        this.lastMcMasterVolume = Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.MASTER);

        //Pause world sounds in cutscenes
        if (Minecraft.getInstance().world != null) {
            if (Minecraft.getInstance().currentScreen != null) {
                if (Minecraft.getInstance().currentScreen.getClass().getName().startsWith("de.keksuccino.cinematica.cutscene.")) {
                    if (!this.resumeWorldSounds) {
//                        Minecraft.getInstance().getSoundHandler().pause();
                        VanillaAudioHandler.fadeOutAndSuppressWorldMusic(() -> {
                            Minecraft.getInstance().getSoundHandler().pause();
                        });
                        this.resumeWorldSounds = true;
                    }
                }
            } else {
                if (this.resumeWorldSounds) {
                    Minecraft.getInstance().getSoundHandler().resume();
                    VanillaAudioHandler.setSuppressWorldMusic(false);
                    this.resumeWorldSounds = false;
                }
            }
        }

        //Stop cinematic audios when leaving or changing the world
        if (this.lastActiveWorld != Minecraft.getInstance().world) {
            for (AudioClip c : AudioCinematicHandler.getCachedAudios()) {
                c.stop();
                c.restart();
            }
            //TODO remove debug
            Cinematica.LOGGER.info("####################### WORLD CHANGE: STOPPING AUDIOS!");
            AudioCinematicHandler.clearUnfinishedAudioCache();
        }
        this.lastActiveWorld = Minecraft.getInstance().world;

    }

}