package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.cinematica.trigger.Cinematic;
import de.keksuccino.cinematica.trigger.CinematicType;
import de.keksuccino.cinematica.trigger.Trigger;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.ui.popup.ChooseFilePopup;
import de.keksuccino.cinematica.ui.popup.CinematicaTextInputPopup;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollAreaEntry;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.CharacterFilter;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditCinematicScreen extends ScrollableScreen {

    public final CinematicType type;
    public final Trigger trigger;
    protected Consumer<Cinematic.SerializedCinematic> callback;
    protected Cinematic.SerializedCinematic context;

    protected AdvancedButton chooseSourceButton;
    protected AdvancedButton setConditionValueButton;
    protected AdvancedButton allowSkipButton;
    protected AdvancedButton setDelayButton;
    protected AdvancedButton oneTimeCinematicButton;
    protected AdvancedButton fadeInButton;
    protected AdvancedButton fadeOutButton;
    protected AdvancedButton stopWorldMusicButton;

    protected AdvancedButton cancelButton;
    protected AdvancedButton saveButton;

    public EditCinematicScreen(Screen parent, CinematicType type, Trigger trigger, Consumer<Cinematic.SerializedCinematic> callback) {
        this(parent, type, trigger, null, callback);
    }

    public EditCinematicScreen(Screen parent, CinematicType type, Trigger trigger, @Nullable Cinematic.SerializedCinematic context, Consumer<Cinematic.SerializedCinematic> callback) {
        super(parent, Locals.localize("cinematica.trigger.ui.addeditcinematic"));
        this.type = type;
        this.trigger = trigger;
        this.callback = callback;
        if (context != null) {
            this.context = context;
        } else {
            this.context = new Cinematic.SerializedCinematic(null, type);
        }
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

        // CHOOSE SOURCE -----------------------
        String chooseSourceLabel = Locals.localize("cinematica.cinematic.choosesource.video");
        if (this.type == CinematicType.AUDIO) {
            chooseSourceLabel = Locals.localize("cinematica.cinematic.choosesource.audio");
        }
        this.chooseSourceButton = new AdvancedButton(0, 0, 200, 20, chooseSourceLabel, true, (press) -> {
            String[] fileTypes = null;
            if (this.type == CinematicType.AUDIO) {
                fileTypes = new String[]{"wav", "mp3"};
            }
            if (this.type == CinematicType.CUTSCENE) {
                fileTypes = new String[]{"mp4"};
            }
            ChooseFilePopup p = new ChooseFilePopup((call) -> {
                if (call != null) {
                    if (call.replace(" ", "").equals("")) {
                        this.context.sourcePath = null;
                    } else {
                        this.context.sourcePath = call;
                    }
                }
            }, fileTypes);
            if (this.context.sourcePath != null) {
                p.setText(this.context.sourcePath);
            }
            PopupHandler.displayPopup(p);
        });
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, "", true) {
            @Override
            public void renderEntry(MatrixStack matrix) {
                this.text = Locals.localize("cinematica.cinematic.source.nosource");
                if (context.sourcePath != null) {
                    this.text = Locals.localize("cinematica.cinematic.source", context.sourcePath);
                }
                super.renderEntry(matrix);
            }
        });
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.chooseSourceButton));
        //-------------------------------------

        // SET CONDITION ----------------------
        String conValBtnLabel = this.trigger.getConditionMetaButtonDisplayName();
        if (conValBtnLabel == null) {
            conValBtnLabel = "";
        }
        this.setConditionValueButton = new AdvancedButton(0, 0, 200, 20, conValBtnLabel, true, (press) -> {
            this.trigger.onConditionMetaButtonClick((AdvancedButton)press, this);
        });
        List<String> conValDesc = this.trigger.getConditionMetaButtonDescription();
        if ((conValDesc != null) && !conValDesc.isEmpty()) {
            this.setConditionValueButton.setDescription(conValDesc.toArray(new String[0]));
        }
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.setConditionValueButton));
        //-------------------------------------

        // SET TRIGGER DELAY ------------------
        this.setDelayButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.cinematic.setdelay"), true, (press) -> {
            CinematicaTextInputPopup p = new CinematicaTextInputPopup(new Color(0, 0, 0, 0), Locals.localize("cinematica.cinematic.setdelay"), CharacterFilter.getDoubleCharacterFiler(), 240, (call) -> {
                if (call != null) {
                    if (MathUtils.isDouble(call)) {
                        this.context.triggerDelay = Double.parseDouble(call);
                    } else {
                        this.context.triggerDelay = 0D;
                    }
                }
            });
            p.setText("" + this.context.triggerDelay);
            PopupHandler.displayPopup(p);
        });
        this.setDelayButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.setdelay.btn.desc"), "%n%"));
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.setDelayButton));
        //-------------------------------------

        // TOGGLE ONE-TIME-CINEMATIC ------------
        this.oneTimeCinematicButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
            if (this.context.oneTimeCinematic) {
                this.context.oneTimeCinematic = false;
            } else {
                this.context.oneTimeCinematic = true;
            }
        }) {
            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                if (context.oneTimeCinematic) {
                    this.setMessage(Locals.localize("cinematica.cinematic.onetimetrigger.on"));
                } else {
                    this.setMessage(Locals.localize("cinematica.cinematic.onetimetrigger.off"));
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        };
        this.oneTimeCinematicButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.onetimetrigger.btn.desc"), "%n%"));
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.oneTimeCinematicButton));
        //-------------------------------------

        // TOGGLE ALLOW SKIP ------------------
        if (this.type == CinematicType.CUTSCENE) {
            this.allowSkipButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
                if (this.context.allowCutsceneSkip) {
                    this.context.allowCutsceneSkip = false;
                } else {
                    this.context.allowCutsceneSkip = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (context.allowCutsceneSkip) {
                        this.setMessage(Locals.localize("cinematica.cinematic.allowskip.on"));
                    } else {
                        this.setMessage(Locals.localize("cinematica.cinematic.allowskip.off"));
                    }
                    super.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            };
            this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.allowSkipButton));
        }
        //-------------------------------------

        // TOGGLE FADE-IN ---------------------
        if (this.type == CinematicType.CUTSCENE) {
            this.fadeInButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
                if (this.context.fadeInCutscene) {
                    this.context.fadeInCutscene = false;
                } else {
                    this.context.fadeInCutscene = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (context.fadeInCutscene) {
                        this.setMessage(Locals.localize("cinematica.cinematic.fadein.on"));
                    } else {
                        this.setMessage(Locals.localize("cinematica.cinematic.fadein.off"));
                    }
                    super.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            };
            this.fadeInButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.fadein.btn.desc"), "%n%"));
            this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.fadeInButton));
        }
        //-------------------------------------

        // TOGGLE FADE-OUT --------------------
        if (this.type == CinematicType.CUTSCENE) {
            this.fadeOutButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
                if (this.context.fadeOutCutscene) {
                    this.context.fadeOutCutscene = false;
                } else {
                    this.context.fadeOutCutscene = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (context.fadeOutCutscene) {
                        this.setMessage(Locals.localize("cinematica.cinematic.fadeout.on"));
                    } else {
                        this.setMessage(Locals.localize("cinematica.cinematic.fadeout.off"));
                    }
                    super.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            };
            this.fadeOutButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.fadeout.btn.desc"), "%n%"));
            this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.fadeOutButton));
        }
        //-------------------------------------

        // TOGGLE STOP WORLD MUSIC ------------
        if (this.type == CinematicType.AUDIO) {
            this.stopWorldMusicButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
                if (this.context.stopWorldMusicOnAudio) {
                    this.context.stopWorldMusicOnAudio = false;
                } else {
                    this.context.stopWorldMusicOnAudio = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (context.stopWorldMusicOnAudio) {
                        this.setMessage(Locals.localize("cinematica.cinematic.stopworldmusic.on"));
                    } else {
                        this.setMessage(Locals.localize("cinematica.cinematic.stopworldmusic.off"));
                    }
                    super.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            };
            this.stopWorldMusicButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.stopworldmusic.btn.desc"), "%n%"));
            this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.stopWorldMusicButton));
        }
        //-------------------------------------

        this.cancelButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("cinematica.trigger.ui.cancel"), true, (press) -> {
            this.onCancel();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.cancelButton);

        this.saveButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("cinematica.cinematic.save"), true, (press) -> {
            this.onSave();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.saveButton);

    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        int xCenter = this.width / 2;

        super.render(matrix, mouseX, mouseY, partialTicks);

        //Cancel Button
        this.cancelButton.setX(xCenter - this.cancelButton.getWidth() - 5);
        this.cancelButton.setY(this.height - 35);
        this.cancelButton.render(matrix, mouseX, mouseY, partialTicks);

        //Save Button
        this.saveButton.setX(xCenter + 5);
        this.saveButton.setY(this.height - 35);
        this.saveButton.render(matrix, mouseX, mouseY, partialTicks);

    }

    @Override
    public void closeScreen() {
        if (!PopupHandler.isPopupActive()) {
            this.onCancel();
            super.closeScreen();
        }
    }

    protected void onSave() {
        if (this.callback != null) {
            this.callback.accept(this.context);
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

    public Cinematic.SerializedCinematic getContext() {
        return this.context;
    }

}
