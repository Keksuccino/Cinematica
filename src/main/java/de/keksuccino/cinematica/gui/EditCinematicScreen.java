package de.keksuccino.cinematica.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.cinematica.trigger.CinematicType;
import de.keksuccino.cinematica.trigger.Trigger;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.cinematica.ui.popup.ChooseFilePopup;
import de.keksuccino.cinematica.ui.popup.CinematicaTextInputPopup;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollAreaEntry;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.CharacterFilter;
import de.keksuccino.konkrete.input.MouseInput;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditCinematicScreen extends ScrollableScreen {

    protected CinematicType type;
    protected Trigger trigger;
    protected Consumer<CinematicProperties> callback;

    protected String sourcePath = null;
    protected String conditionValue = null;
    protected boolean allowSkip = true;
    protected boolean oneTimeCinematic = false;
    protected double triggerDelay = 0D;
    protected boolean fadeInCutscene = true;
    protected boolean fadeOutCutscene = true;
    protected boolean stopWorldMusicOnAudio = false;

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

    public EditCinematicScreen(Screen parent, CinematicType type, Trigger trigger, Consumer<CinematicProperties> callback) {
        super(parent, Locals.localize("cinematica.trigger.ui.addeditcinematic"));
        this.type = type;
        this.trigger = trigger;
        this.callback = callback;
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
                        this.sourcePath = null;
                    } else {
                        this.sourcePath = call;
                    }
                }
            }, fileTypes);
            if (this.sourcePath != null) {
                p.setText(this.sourcePath);
            }
            PopupHandler.displayPopup(p);
        });
        UIBase.colorizeButton(this.chooseSourceButton);
        ScrollAreaEntryBase chooseSourceEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {
            int xCenter = render.entry.x + (render.entry.getWidth() / 2);
            if (this.sourcePath != null) {
                drawCenteredString(render.matrix, font, Locals.localize("cinematica.cinematic.source", this.sourcePath), xCenter, render.entry.y + 4, -1);
            } else {
                drawCenteredString(render.matrix, font, Locals.localize("cinematica.cinematic.source.nosource"), xCenter, render.entry.y + 4, -1);
            }
            this.chooseSourceButton.setX(xCenter - (this.chooseSourceButton.getWidth() / 2));
            this.chooseSourceButton.setY(render.entry.y + render.entry.getHeight() - this.chooseSourceButton.getHeight() - 2);
            this.chooseSourceButton.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
        });
        chooseSourceEntry.setHeight(38);
        this.scrollArea.addEntry(chooseSourceEntry);
        //-------------------------------------

        // SET CONDITION ----------------------
        String conValBtnLabel = this.trigger.getConditionValueButtonDisplayName();
        if (conValBtnLabel == null) {
            conValBtnLabel = "";
        }
        this.setConditionValueButton = new AdvancedButton(0, 0, 200, 20, conValBtnLabel, true, (press) -> {
            this.trigger.onConditionValueButtonClick((AdvancedButton)press, this);
        });
        List<String> conValDesc = this.trigger.getConditionValueDescription();
        if ((conValDesc != null) && !conValDesc.isEmpty()) {
            this.setConditionValueButton.setDescription(conValDesc.toArray(new String[0]));
        }
        UIBase.colorizeButton(this.setConditionValueButton);
        ScrollAreaEntryBase setConditionEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {
            int xCenter = render.entry.x + (render.entry.getWidth() / 2);
            this.setConditionValueButton.setX(xCenter - (this.setConditionValueButton.getWidth() / 2));
            this.setConditionValueButton.setY(render.entry.y + 2);
            this.setConditionValueButton.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
        });
        setConditionEntry.setHeight(24);
        this.scrollArea.addEntry(setConditionEntry);
        //-------------------------------------

        // SET TRIGGER DELAY ------------------
        this.setDelayButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.cinematic.setdelay"), true, (press) -> {
            CinematicaTextInputPopup p = new CinematicaTextInputPopup(new Color(0, 0, 0, 0), Locals.localize("cinematica.cinematic.setdelay"), CharacterFilter.getDoubleCharacterFiler(), 240, (call) -> {
                if (call != null) {
                    if (MathUtils.isDouble(call)) {
                        this.triggerDelay = Double.parseDouble(call);
                    } else {
                        this.triggerDelay = 0D;
                    }
                }
            });
            p.setText("" + this.triggerDelay);
            PopupHandler.displayPopup(p);
        });
        this.setDelayButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.setdelay.btn.desc"), "%n%"));
        UIBase.colorizeButton(this.setDelayButton);
        ScrollAreaEntryBase setTriggerDelayEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {
            int xCenter = render.entry.x + (render.entry.getWidth() / 2);
            this.setDelayButton.setX(xCenter - (this.setDelayButton.getWidth() / 2));
            this.setDelayButton.setY(render.entry.y + 2);
            this.setDelayButton.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
        });
        setTriggerDelayEntry.setHeight(24);
        this.scrollArea.addEntry(setTriggerDelayEntry);
        //-------------------------------------

        // TOGGLE ONE-TIME-CINEMATIC ------------
        this.oneTimeCinematicButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
            if (this.oneTimeCinematic) {
                this.oneTimeCinematic = false;
            } else {
                this.oneTimeCinematic = true;
            }
        }) {
            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                if (oneTimeCinematic) {
                    this.setMessage(Locals.localize("cinematica.cinematic.onetimetrigger.on"));
                } else {
                    this.setMessage(Locals.localize("cinematica.cinematic.onetimetrigger.off"));
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        };
        this.oneTimeCinematicButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.onetimetrigger.btn.desc"), "%n%"));
        UIBase.colorizeButton(this.oneTimeCinematicButton);
        ScrollAreaEntryBase oneTimeCinematicEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {
            int xCenter = render.entry.x + (render.entry.getWidth() / 2);
            this.oneTimeCinematicButton.setX(xCenter - (this.oneTimeCinematicButton.getWidth() / 2));
            this.oneTimeCinematicButton.setY(render.entry.y + 2);
            this.oneTimeCinematicButton.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
        });
        oneTimeCinematicEntry.setHeight(24);
        this.scrollArea.addEntry(oneTimeCinematicEntry);
        //-------------------------------------

        // TOGGLE ALLOW SKIP ------------------
        if (this.type == CinematicType.CUTSCENE) {
            this.allowSkipButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
                if (this.allowSkip) {
                    this.allowSkip = false;
                } else {
                    this.allowSkip = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (allowSkip) {
                        this.setMessage(Locals.localize("cinematica.cinematic.allowskip.on"));
                    } else {
                        this.setMessage(Locals.localize("cinematica.cinematic.allowskip.off"));
                    }
                    super.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            };
            UIBase.colorizeButton(this.allowSkipButton);
            ScrollAreaEntryBase allowSkipEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {
                int xCenter = render.entry.x + (render.entry.getWidth() / 2);
                this.allowSkipButton.setX(xCenter - (this.allowSkipButton.getWidth() / 2));
                this.allowSkipButton.setY(render.entry.y + 2);
                this.allowSkipButton.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            });
            allowSkipEntry.setHeight(24);
            this.scrollArea.addEntry(allowSkipEntry);
        }
        //-------------------------------------

        // TOGGLE FADE-IN ---------------------
        if (this.type == CinematicType.CUTSCENE) {
            this.fadeInButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
                if (this.fadeInCutscene) {
                    this.fadeInCutscene = false;
                } else {
                    this.fadeInCutscene = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (fadeInCutscene) {
                        this.setMessage(Locals.localize("cinematica.cinematic.fadein.on"));
                    } else {
                        this.setMessage(Locals.localize("cinematica.cinematic.fadein.off"));
                    }
                    super.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            };
            this.fadeInButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.fadein.btn.desc"), "%n%"));
            UIBase.colorizeButton(this.fadeInButton);
            ScrollAreaEntryBase fadeInEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {
                int xCenter = render.entry.x + (render.entry.getWidth() / 2);
                this.fadeInButton.setX(xCenter - (this.fadeInButton.getWidth() / 2));
                this.fadeInButton.setY(render.entry.y + 2);
                this.fadeInButton.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            });
            fadeInEntry.setHeight(24);
            this.scrollArea.addEntry(fadeInEntry);
        }
        //-------------------------------------

        // TOGGLE FADE-OUT --------------------
        if (this.type == CinematicType.CUTSCENE) {
            this.fadeOutButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
                if (this.fadeOutCutscene) {
                    this.fadeOutCutscene = false;
                } else {
                    this.fadeOutCutscene = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (fadeOutCutscene) {
                        this.setMessage(Locals.localize("cinematica.cinematic.fadeout.on"));
                    } else {
                        this.setMessage(Locals.localize("cinematica.cinematic.fadeout.off"));
                    }
                    super.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            };
            this.fadeOutButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.fadeout.btn.desc"), "%n%"));
            UIBase.colorizeButton(this.fadeOutButton);
            ScrollAreaEntryBase fadeOutEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {
                int xCenter = render.entry.x + (render.entry.getWidth() / 2);
                this.fadeOutButton.setX(xCenter - (this.fadeOutButton.getWidth() / 2));
                this.fadeOutButton.setY(render.entry.y + 2);
                this.fadeOutButton.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            });
            fadeOutEntry.setHeight(24);
            this.scrollArea.addEntry(fadeOutEntry);
        }
        //-------------------------------------

        // TOGGLE STOP WORLD MUSIC ------------
        if (this.type == CinematicType.AUDIO) {
            this.stopWorldMusicButton = new AdvancedButton(0, 0, 200, 20, "", true, (press) -> {
                if (this.stopWorldMusicOnAudio) {
                    this.stopWorldMusicOnAudio = false;
                } else {
                    this.stopWorldMusicOnAudio = true;
                }
            }) {
                @Override
                public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                    if (stopWorldMusicOnAudio) {
                        this.setMessage(Locals.localize("cinematica.cinematic.stopworldmusic.on"));
                    } else {
                        this.setMessage(Locals.localize("cinematica.cinematic.stopworldmusic.off"));
                    }
                    super.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            };
            this.stopWorldMusicButton.setDescription(StringUtils.splitLines(Locals.localize("cinematica.cinematic.stopworldmusic.btn.desc"), "%n%"));
            UIBase.colorizeButton(this.stopWorldMusicButton);
            ScrollAreaEntryBase stopWorldMusicEntry = new ScrollAreaEntryBase(this.scrollArea, (render) -> {
                int xCenter = render.entry.x + (render.entry.getWidth() / 2);
                this.stopWorldMusicButton.setX(xCenter - (this.stopWorldMusicButton.getWidth() / 2));
                this.stopWorldMusicButton.setY(render.entry.y + 2);
                this.stopWorldMusicButton.render(render.matrix, MouseInput.getMouseX(), MouseInput.getMouseY(), Minecraft.getInstance().getRenderPartialTicks());
            });
            stopWorldMusicEntry.setHeight(24);
            this.scrollArea.addEntry(stopWorldMusicEntry);
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
            CinematicProperties p = new CinematicProperties();
            p.type = this.type;
            p.sourcePath = this.sourcePath;
            p.conditionValue = this.conditionValue;
            p.allowCutsceneSkip = this.allowSkip;
            p.triggerDelay = this.triggerDelay;
            p.onTimeCinematic = this.oneTimeCinematic;
            p.fadeInCutscene = this.fadeInCutscene;
            p.fadeOutCutscene = this.fadeOutCutscene;
            p.stopWorldMusicOnAudio = this.stopWorldMusicOnAudio;
            this.callback.accept(p);
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

    public Trigger getTrigger() {
        return this.trigger;
    }

    public void setConditionValue(String s) {
        this.conditionValue = s;
    }

    public String getConditionValue() {
        return this.conditionValue;
    }

    public void setSourcePath(String s) {
        this.sourcePath = s;
    }

    public void setAllowSkip(boolean b) {
        this.allowSkip = b;
    }

    public void setOneTimeCinematic(boolean b) {
        this.oneTimeCinematic = b;
    }

    public void setTriggerDelay(double d) {
        this.triggerDelay = d;
    }

    public void setFadeInCutscene(boolean b) {
        this.fadeInCutscene = b;
    }

    public void setFadeOutCutscene(boolean b) {
        this.fadeOutCutscene = b;
    }

    public void setStopWorldMusicOnAudio(boolean b) {
        this.stopWorldMusicOnAudio = b;
    }

    public static class CinematicProperties {

        public CinematicType type;
        public String sourcePath;
        public String conditionValue;
        public boolean allowCutsceneSkip;
        public double triggerDelay;
        public boolean onTimeCinematic;
        public boolean fadeInCutscene = true;
        public boolean fadeOutCutscene = true;
        public boolean stopWorldMusicOnAudio = false;

    }

}
