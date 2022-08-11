package de.keksuccino.cinematica;

import de.keksuccino.auudio.audio.AudioClip;
import de.keksuccino.cinematica.audio.AudioCinematicHandler;
import de.keksuccino.cinematica.audio.VanillaAudioHandler;
import de.keksuccino.cinematica.cutscene.CutScenePauseMenu;
import de.keksuccino.cinematica.events.AddItemToPlayerInventoryEvent;
import de.keksuccino.cinematica.events.EntityDiedEvent;
import de.keksuccino.cinematica.gui.CinematicVolumeScreen;
import de.keksuccino.cinematica.gui.CinematicaConfigScreen;
import de.keksuccino.cinematica.gui.ManageCinematicsScreen;
import de.keksuccino.cinematica.engine.cinematic.CinematicHandler;
import de.keksuccino.cinematica.ui.CinematicaContextMenu;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.ui.popup.CinematicaYesNoPopup;
import de.keksuccino.cinematica.utils.formatting.FormattingUtils;
import de.keksuccino.cinematica.video.VideoVolumeHandler;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedImageButton;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.MouseInput;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventHandler {

    protected static final ResourceLocation CINEMATICA_BTN_TEXTURE = new ResourceLocation("cinematica", "textures/cinematica_button.png");

    AdvancedButton cinematicaButton;
    CinematicaContextMenu cinematicaControlsMenu;

    protected float lastMcMasterVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER);
    protected boolean resumeWorldSounds = false;
    protected Level lastActiveWorld = null;

    protected Screen lastPauseScreen = null;

    @SubscribeEvent
    public void onDrawScreenPost(ScreenEvent.Render.Post e) {

        if (this.cinematicaControlsMenu == null) {

            this.cinematicaControlsMenu = new CinematicaContextMenu();
            this.cinematicaControlsMenu.setAutoclose(true);

            AdvancedButton manageCinematicsButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("cinematica.controls.managecinematics"), true, (press) -> {
                ManageCinematicsScreen s = new ManageCinematicsScreen(this.lastPauseScreen);
                Minecraft.getInstance().setScreen(s);
            });
            this.cinematicaControlsMenu.addContent(manageCinematicsButton);

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

            AdvancedButton volumeButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("cinematica.audiochannel"), true, (press) -> {
                Minecraft.getInstance().setScreen(new CinematicVolumeScreen(Minecraft.getInstance().screen));
            });
            this.cinematicaControlsMenu.addContent(volumeButton);

            AdvancedButton settingsButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("cinematica.config"), true, (press) -> {
                Minecraft.getInstance().setScreen(new CinematicaConfigScreen(Minecraft.getInstance().screen));
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

        if (((e.getScreen() instanceof PauseScreen) || (e.getScreen() instanceof CutScenePauseMenu)) && Cinematica.config.getOrDefault("show_controls_in_pause_screen", true) && !PopupHandler.isPopupActive()) {

            this.lastPauseScreen = e.getScreen();

            if (!this.cinematicaButton.isHoveredOrFocused()) {
                this.cinematicaButton.setX(-10);
            } else {
                this.cinematicaButton.setX(-2);
            }
            this.cinematicaButton.render(e.getPoseStack(), e.getMouseX(), e.getMouseY(), e.getPartialTick());

            float scale = UIBase.getUIScale();
            MouseInput.setRenderScale(scale);
            e.getPoseStack().pushPose();
            e.getPoseStack().scale(scale, scale, scale);
            this.cinematicaControlsMenu.render(e.getPoseStack(), MouseInput.getMouseX(), MouseInput.getMouseY());
            e.getPoseStack().popPose();
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
    public void onInitPost(ScreenEvent.Init.Post e) {

        if (e.getScreen() instanceof SoundOptionsScreen) {
            if (Cinematica.config.getOrDefault("add_slider_to_sound_controls", true)) {

                VolumeSlider slider = getLastSlider(e.getListenersList());
                if (slider != null) {
                    e.addListener(new AdvancedButton(slider.x + slider.getWidth() + 10, slider.y, slider.getWidth(), slider.getHeight(), Locals.localize("cinematica.audiochannel"), false, (press) -> {
                        Minecraft.getInstance().setScreen(new CinematicVolumeScreen(e.getScreen()));
                    }));
                }

            }
        }

    }

    protected static VolumeSlider getLastSlider(List<GuiEventListener> widgets) {
        List<VolumeSlider> l = new ArrayList<>();
        for (GuiEventListener w : widgets) {
            if (w instanceof VolumeSlider) {
                l.add((VolumeSlider) w);
            }
        }
        if (!l.isEmpty()) {
            return l.get(l.size()-1);
        }
        return null;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {

        if (Minecraft.getInstance().screen == null) {
            if (this.cinematicaControlsMenu != null) {
                this.cinematicaControlsMenu.closeMenu();
            }
            MouseInput.unblockVanillaInput("cinematica_controls_context_menu");
        }

        //Update cutscene volume on master volume change
        if (this.lastMcMasterVolume != Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER)) {
            VideoVolumeHandler.updateVolume();
        }
        this.lastMcMasterVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER);

        //Stop world sounds in cutscenes
        if (Minecraft.getInstance().level != null) {
            if (Minecraft.getInstance().screen != null) {
                if (Minecraft.getInstance().screen.getClass().getName().startsWith("de.keksuccino.cinematica.cutscene.")) {
                    if (!this.resumeWorldSounds) {
                        //TODO make audio cinematics fade out with world music
                        AudioCinematicHandler.stopAll();
                        VanillaAudioHandler.fadeOutAndSuppressWorldMusic(() -> {
                            Minecraft.getInstance().getSoundManager().stop();
                        });
                        this.resumeWorldSounds = true;
                    }
                }
            } else {
                if (this.resumeWorldSounds) {
                    VanillaAudioHandler.setSuppressWorldMusic(false);
                    this.resumeWorldSounds = false;
                }
            }
        }

        //Stop cinematic audios when leaving or changing the world
        if (this.lastActiveWorld != Minecraft.getInstance().level) {
            for (AudioClip c : AudioCinematicHandler.getCachedAudios()) {
                c.stop();
            }
            AudioCinematicHandler.clearUnfinishedAudioCache();
        }
        this.lastActiveWorld = Minecraft.getInstance().level;

    }

    @SubscribeEvent
    public void onEntityDied(EntityDiedEvent e) {
        try {
            if (Cinematica.config.getOrDefault("print_died_entities", false)) {
                if ((Minecraft.getInstance().level != null) && (Minecraft.getInstance().player != null)) {
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e-----------------------"));
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e[CINEMATICA] DEBUG: §lENTITY DIED:"));
                    ResourceLocation entityResLoc = Registry.ENTITY_TYPE.getKey(e.getEntity().getType());
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e[CINEMATICA] DEBUG: ID/TYPE: " + entityResLoc.toString()));
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e[CINEMATICA] DEBUG: NAME: " + StringUtils.convertFormatCodes(e.getEntity().getName().getString(), "§", "&")));
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e-----------------------"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onItemAddedToInventory(AddItemToPlayerInventoryEvent e) {
        try {
            if (Cinematica.config.getOrDefault("print_added_items", false)) {
                if ((Minecraft.getInstance().level != null) && (Minecraft.getInstance().player != null)) {
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e-----------------------"));
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e[CINEMATICA] DEBUG: §lITEM ADDED TO INVENTORY:"));
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e[CINEMATICA] DEBUG: ITEM TYPE: " + Registry.ITEM.getKey(e.getStack().getItem()).toString()));
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e[CINEMATICA] DEBUG: ITEM NAME: " + StringUtils.convertFormatCodes(e.getStack().getHoverName().getString(), "§", "&")));
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e[CINEMATICA] DEBUG: ITEM COUNT: " + e.getStack().getCount()));
                    String lore = FormattingUtils.deserializeItemLoreToString(e.getStack());
                    if (lore != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e[CINEMATICA] DEBUG: ITEM LORE: " + StringUtils.convertFormatCodes(lore, "§", "&")));
                    }
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§e-----------------------"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
