package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.cinematic.CinematicType;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.ui.popup.ChooseFilePopup;
import de.keksuccino.cinematica.ui.popup.CinematicaTextInputPopup;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedTextField;
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
    protected Cinematic cinematic;
    protected Consumer<Cinematic> callback;

    protected boolean isNewCinematic = false;

    protected AdvancedTextField nameTextField;

    protected AdvancedButton chooseSourceButton;
    protected AdvancedButton manageConditionsButton;
    protected AdvancedButton allowSkipButton;
    protected AdvancedButton setDelayButton;
    protected AdvancedButton oneTimeCinematicButton;
    protected AdvancedButton oncePerSessionButton;
    protected AdvancedButton fadeInButton;
    protected AdvancedButton fadeOutButton;
    protected AdvancedButton stopWorldMusicButton;
    protected AdvancedButton copyIdentifierButton;

    protected AdvancedButton doneButton;
    protected AdvancedButton cancelButton;

    public EditCinematicScreen(Screen parent, CinematicType type, Consumer<Cinematic> callback) {
        this(parent, type, null, callback);
    }

    public EditCinematicScreen(Screen parent, Cinematic cinematicToEdit, Consumer<Cinematic> callback) {
        this(parent, cinematicToEdit.type, cinematicToEdit, callback);
    }

    public EditCinematicScreen(Screen parent, CinematicType type, @Nullable Cinematic cinematicToEdit, Consumer<Cinematic> callback) {
        super(parent, Locals.localize("cinematica.cinematic.add_or_edit"));
        this.type = type;
        this.cinematic = cinematicToEdit;
        if (this.cinematic == null) {
            this.isNewCinematic = true;
            this.cinematic = new Cinematic(null, type, null);
        }
        this.callback = callback;

        this.nameTextField = new AdvancedTextField(Minecraft.getInstance().fontRenderer, 0, 0, 200, 20, true, null) {
            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                super.render(matrixStack, mouseX, mouseY, partialTicks);
                cinematic.name = this.getText();
            }
        };
        this.nameTextField.setMaxStringLength(100000);
        if (this.cinematic.name != null) {
            this.nameTextField.setText(this.cinematic.name);
        }

    }

    @Override
    public boolean isOverlayButtonHovered() {
        if (this.doneButton != null) {
            if (this.doneButton.isHovered()) {
                return true;
            }
        }
        if (this.cancelButton != null) {
            if (this.cancelButton.isHovered()) {
                return true;
            }
        }
        return false;
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

        // COPY IDENTIFIER ---------------------
        this.copyIdentifierButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.cinematic.copy_identifier"), true, (press) -> {
            Minecraft.getInstance().keyboardListener.setClipboardString(this.cinematic.getIdentifier());
        });
        this.copyIdentifierButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.copy_identifier.desc"), "%n%"));
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.copyIdentifierButton));
        //--------------------------------------

        // NAME --------------------------------
        TextEntry nameLabelEntry = new TextEntry(this.scrollArea, Locals.localize("cinematica.cinematic.name"), true);
        nameLabelEntry.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.name.desc"), "%n%"));
        this.scrollArea.addEntry(nameLabelEntry);
        TextFieldEntry nameFieldEntry = new TextFieldEntry(this.scrollArea, this.nameTextField);
        nameFieldEntry.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.name.desc"), "%n%"));
        this.scrollArea.addEntry(nameFieldEntry);
        //--------------------------------------

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
                        this.cinematic.sourcePath = null;
                    } else {
                        this.cinematic.sourcePath = call;
                    }
                }
            }, fileTypes);
            if (this.cinematic.sourcePath != null) {
                p.setText(this.cinematic.sourcePath);
            }
            PopupHandler.displayPopup(p);
        });
        this.scrollArea.addEntry(new TextEntry(this.scrollArea, "", true) {
            @Override
            public void renderEntry(MatrixStack matrix) {
                this.text = Locals.localize("cinematica.cinematic.source.nosource");
                if (cinematic.sourcePath != null) {
                    this.text = Locals.localize("cinematica.cinematic.source", cinematic.sourcePath);
                }
                super.renderEntry(matrix);
            }
        });
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.chooseSourceButton));
        //-------------------------------------

        // MANAGE CONDITIONS ------------------
        this.manageConditionsButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.condition.manage"), true, (press) -> {
            ManageConditionsScreen s = new ManageConditionsScreen(this, this.cinematic);
            Minecraft.getInstance().displayGuiScreen(s);
        });
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.manageConditionsButton));
        //-------------------------------------

        // SET TRIGGER DELAY ------------------
        this.setDelayButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.cinematic.setdelay"), true, (press) -> {
            CinematicaTextInputPopup p = new CinematicaTextInputPopup(new Color(0, 0, 0, 0), Locals.localize("cinematica.cinematic.setdelay"), CharacterFilter.getDoubleCharacterFiler(), 240, (call) -> {
                if (call != null) {
                    if (MathUtils.isDouble(call)) {
                        this.cinematic.triggerDelay = Double.parseDouble(call);
                    } else {
                        this.cinematic.triggerDelay = 0D;
                    }
                }
            });
            p.setText("" + this.cinematic.triggerDelay);
            PopupHandler.displayPopup(p);
        });
        this.setDelayButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.setdelay.btn.desc"), "%n%"));
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.setDelayButton));
        //-------------------------------------

        // TOGGLE ONE-TIME-CINEMATIC ------------
        this.oneTimeCinematicButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
            if (this.cinematic.oneTimeCinematic) {
                this.cinematic.oneTimeCinematic = false;
            } else {
                this.cinematic.oneTimeCinematic = true;
            }
        }) {
            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                if (cinematic.oneTimeCinematic) {
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

        // TOGGLE ONCE-PER-SESSION-CINEMATIC --
        this.oncePerSessionButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
            if (this.cinematic.oncePerSessionCinematic) {
                this.cinematic.oncePerSessionCinematic = false;
            } else {
                this.cinematic.oncePerSessionCinematic = true;
            }
        }) {
            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                if (cinematic.oncePerSessionCinematic) {
                    this.setMessage(Locals.localize("cinematica.cinematic.oncepersession.on"));
                } else {
                    this.setMessage(Locals.localize("cinematica.cinematic.oncepersession.off"));
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        };
        this.oncePerSessionButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.oncepersession.btn.desc"), "%n%"));
        this.scrollArea.addEntry(new ButtonEntry(this.scrollArea, this.oncePerSessionButton));
        //-------------------------------------

        // TOGGLE ALLOW SKIP ------------------
        if (this.type == CinematicType.CUTSCENE) {
            this.allowSkipButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
                if (this.cinematic.allowCutsceneSkip) {
                    this.cinematic.allowCutsceneSkip = false;
                } else {
                    this.cinematic.allowCutsceneSkip = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (cinematic.allowCutsceneSkip) {
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
                if (this.cinematic.fadeInCutscene) {
                    this.cinematic.fadeInCutscene = false;
                } else {
                    this.cinematic.fadeInCutscene = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (cinematic.fadeInCutscene) {
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
                if (this.cinematic.fadeOutCutscene) {
                    this.cinematic.fadeOutCutscene = false;
                } else {
                    this.cinematic.fadeOutCutscene = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (cinematic.fadeOutCutscene) {
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
                if (this.cinematic.stopWorldMusicOnAudio) {
                    this.cinematic.stopWorldMusicOnAudio = false;
                } else {
                    this.cinematic.stopWorldMusicOnAudio = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (cinematic.stopWorldMusicOnAudio) {
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

        this.doneButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("cinematica.ui.done"), true, (press) -> {
            this.onDone();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.doneButton);

        this.cancelButton = new AdvancedButton(0, 0, 95, 20, Locals.localize("cinematica.ui.cancel"), true, (press) -> {
            this.onCancel();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.cancelButton);

    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        int xCenter = this.width / 2;

        super.render(matrix, mouseX, mouseY, partialTicks);

        //Save Button
        if (!this.isNewCinematic) {
            this.doneButton.setX(xCenter - (this.doneButton.getWidth() / 2));
        } else {
            this.doneButton.setX(xCenter + 5);
        }
        this.doneButton.setY(this.height - 35);
        this.doneButton.render(matrix, mouseX, mouseY, partialTicks);

        if (this.isNewCinematic) {
            this.cancelButton.setX(xCenter - this.cancelButton.getWidth() - 5);
            this.cancelButton.setY(this.height - 35);
            this.cancelButton.render(matrix, mouseX, mouseY, partialTicks);
        }

    }

    @Override
    public void closeScreen() {
        if (!PopupHandler.isPopupActive()) {
            if (this.isNewCinematic) {
                this.onCancel();
            } else {
                this.onDone();
            }
            super.closeScreen();
        }
    }

    protected void onDone() {
        if (this.callback != null) {
            this.callback.accept(this.cinematic);
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

    public Cinematic getCinematic() {
        return this.cinematic;
    }

}
