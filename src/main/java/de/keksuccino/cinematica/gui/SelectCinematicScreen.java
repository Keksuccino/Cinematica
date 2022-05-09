package de.keksuccino.cinematica.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.cinematic.CinematicHandler;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollArea;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollAreaEntry;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.MouseInput;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.rendering.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class SelectCinematicScreen extends Screen {

    protected static final Color ENTRY_BACKGROUND_COLOR = new Color(92, 92, 92);
    protected static final Color SCREEN_BACKGROUND_COLOR = new Color(54, 54, 54);
    protected static final Color HEADER_FOOTER_COLOR = new Color(33, 33, 33);

    protected ScrollArea cinematicScrollList;
    protected Screen parent;
    protected AdvancedButton backButton;

    protected Consumer<Cinematic> callback;

    public SelectCinematicScreen(Screen parent, Consumer<Cinematic> callback) {

        super(new TextComponent(""));
        this.parent = parent;
        this.callback = callback;

        this.cinematicScrollList = new ScrollArea(0, 50, 300, 0);
        this.cinematicScrollList.backgroundColor = ENTRY_BACKGROUND_COLOR;

        this.backButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.ui.back"), true, (press) -> {
            this.onCancel();
            Minecraft.getInstance().setScreen(this.parent);
        });
        UIBase.colorizeButton(this.backButton);

    }

    @Override
    protected void init() {

        this.updateEntries();
        this.cinematicScrollList.x = (this.width / 2) - 150;
        this.cinematicScrollList.height = this.height - 100;

    }

    protected void updateEntries() {
        if (this.cinematicScrollList != null) {
            List<ScrollAreaEntry> l = new ArrayList<>();
            l.addAll(this.cinematicScrollList.getEntries());
            for (ScrollAreaEntry e : l) {
                this.cinematicScrollList.removeEntry(e);
            }
            for (Cinematic c : CinematicHandler.getCinematics()) {
                this.cinematicScrollList.addEntry(new CinematicScrollAreaEntry(this.cinematicScrollList, c, this));
            }
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

    //On Esc
    @Override
    public void onClose() {
        this.onCancel();
        Minecraft.getInstance().setScreen(this.parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {

        int xCenter = this.width / 2;

        RenderSystem.enableBlend();

        //Draw screen background
        fill(matrix, 0, 0, this.width, this.height, SCREEN_BACKGROUND_COLOR.getRGB());

        this.cinematicScrollList.render(matrix);

        //Draw header
        fill(matrix, 0, 0, this.width, 50, HEADER_FOOTER_COLOR.getRGB());

        //Draw title
        drawCenteredString(matrix, font, Locals.localize("cinematica.cinematic.choose"), this.width / 2, 20, -1);

        //Draw footer
        fill(matrix, 0, this.height - 50, this.width, this.height, HEADER_FOOTER_COLOR.getRGB());

        this.backButton.setX(xCenter - (this.backButton.getWidth() / 2));
        this.backButton.setY(this.height - 35);
        this.backButton.render(matrix, mouseX, mouseY, partialTicks);

        super.render(matrix, mouseX, mouseY, partialTicks);

    }

    protected static void renderDescription(PoseStack matrix, List<String> desc, int mouseX, int mouseY) {
        if (desc != null) {
            int width = 10;
            int height = 10;
            //Getting the longest string from the list to render the background with the correct width
            for (String s : desc) {
                int i = Minecraft.getInstance().font.width(s) + 10;
                if (i > width) {
                    width = i;
                }
                height += 10;
            }
            mouseX += 5;
            mouseY += 5;
            if (Minecraft.getInstance().screen.width < mouseX + width) {
                mouseX -= width + 10;
            }
            if (Minecraft.getInstance().screen.height < mouseY + height) {
                mouseY -= height + 10;
            }
            RenderUtils.setZLevelPre(matrix, 600);
            renderDescriptionBackground(matrix, mouseX, mouseY, width, height);
            RenderSystem.enableBlend();
            int i2 = 5;
            for (String s : desc) {
                drawString(matrix, Minecraft.getInstance().font, s, mouseX + 5, mouseY + i2, Color.WHITE.getRGB());
                i2 += 10;
            }
            RenderUtils.setZLevelPost(matrix);
            RenderSystem.disableBlend();
        }
    }

    protected static void renderDescriptionBackground(PoseStack matrix, int x, int y, int width, int height) {
        fill(matrix, x, y, x + width, y + height, new Color(26, 26, 26, 250).getRGB());
    }

    protected static void colorizeButton(AdvancedButton b) {
        b.setBackgroundColor(new Color(100, 100, 100), new Color(130, 130, 130), new Color(180, 180, 180), new Color(199, 199, 199), 1);
    }

    public static class CinematicScrollAreaEntry extends ScrollAreaEntry {

        protected Cinematic cinematic;
        protected Font font = Minecraft.getInstance().font;
        protected SelectCinematicScreen parentScreen;

        protected boolean isMouseDown = false;

        public CinematicScrollAreaEntry(ScrollArea parent, Cinematic cinematic, SelectCinematicScreen parentScreen) {
            super(parent);
            this.cinematic = cinematic;
            this.parentScreen = parentScreen;
        }

        @Override
        public void renderEntry(PoseStack matrix) {

            int center = this.x + (this.getWidth() / 2);

            if (!this.isHoveredOrFocused()) {
                fill(matrix, this.x, this.y, this.x + this.getWidth(), this.y + this.getHeight(), ENTRY_BACKGROUND_COLOR.getRGB());
            } else {
                fill(matrix, this.x, this.y, this.x + this.getWidth(), this.y + this.getHeight(), ENTRY_BACKGROUND_COLOR.brighter().brighter().getRGB());
            }

            String labelString;
            if ((this.cinematic.name != null) && !this.cinematic.name.replace(" ", "").equals("")) {
                labelString = this.cinematic.name;
            } else {
                String sourceString = this.cinematic.sourcePath;
                if (font.width(sourceString) > this.getWidth() - 30) {
                    sourceString = new StringBuilder(sourceString).reverse().toString();
                    sourceString = font.plainSubstrByWidth(sourceString, this.getWidth() - 30);
                    sourceString = new StringBuilder(sourceString).reverse().toString();
                    sourceString = ".." + sourceString;
                }
                labelString = sourceString;
            }
            drawCenteredString(matrix, font, labelString, center, this.y + 10, -1);

            this.handleSelection();

        }

        protected void handleSelection() {

            if (!PopupHandler.isPopupActive() && !this.parentScreen.backButton.isHoveredOrFocused()) {
                if (MouseInput.isLeftMouseDown() && !this.isMouseDown) {
                    if (this.isHoveredOrFocused()) {
                        if (this.parentScreen.callback != null) {
                            this.parentScreen.callback.accept(this.cinematic);
                        }
                    }
                    this.isMouseDown = true;
                }
                if (!MouseInput.isLeftMouseDown()) {
                    this.isMouseDown = false;
                }
            } else if (MouseInput.isLeftMouseDown()) {
                this.isMouseDown = true;
            }

        }

        @Override
        public int getHeight() {
            return 26;
        }

    }

}
