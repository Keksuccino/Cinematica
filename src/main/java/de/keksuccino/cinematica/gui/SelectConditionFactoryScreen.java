package de.keksuccino.cinematica.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.engine.condition.ConditionFactoryRegistry;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollArea;
import de.keksuccino.konkrete.gui.content.scrollarea.ScrollAreaEntry;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.MouseInput;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.rendering.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class SelectConditionFactoryScreen extends Screen {

    protected static final Color ENTRY_BACKGROUND_COLOR = new Color(92, 92, 92);
    protected static final Color SCREEN_BACKGROUND_COLOR = new Color(54, 54, 54);
    protected static final Color HEADER_FOOTER_COLOR = new Color(33, 33, 33);

    protected ScrollArea factoryScrollList;
    protected Screen parent;
    protected AdvancedButton backButton;

    protected Consumer<ConditionFactory> callback;

    public SelectConditionFactoryScreen(Screen parent, Consumer<ConditionFactory> callback) {

        super(new StringTextComponent(""));
        this.parent = parent;
        this.callback = callback;

        this.factoryScrollList = new ScrollArea(0, 50, 300, 0);
        this.factoryScrollList.backgroundColor = ENTRY_BACKGROUND_COLOR;

        this.backButton = new AdvancedButton(0, 0, 200, 20, Locals.localize("cinematica.ui.back"), true, (press) -> {
            this.onCancel();
            Minecraft.getInstance().displayGuiScreen(this.parent);
        });
        UIBase.colorizeButton(this.backButton);

    }

    @Override
    protected void init() {

        this.updateEntries();
        this.factoryScrollList.x = (this.width / 2) - 150;
        this.factoryScrollList.height = this.height - 100;

    }

    protected void updateEntries() {
        if (this.factoryScrollList != null) {
            List<ScrollAreaEntry> l = new ArrayList<>();
            l.addAll(this.factoryScrollList.getEntries());
            for (ScrollAreaEntry e : l) {
                this.factoryScrollList.removeEntry(e);
            }
            for (Map.Entry<String, ConditionFactory> m : ConditionFactoryRegistry.getFactoriesMap().entrySet()) {
                this.factoryScrollList.addEntry(new FactoryScrollAreaEntry(this.factoryScrollList, m.getValue(), this));
            }
        }
    }

    protected void onCancel() {
        if (this.callback != null) {
            this.callback.accept(null);
        }
    }

    @Override
    public void closeScreen() {
        this.onCancel();
        Minecraft.getInstance().displayGuiScreen(this.parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        int xCenter = this.width / 2;
        int yCenter = this.height / 2;

        RenderSystem.enableBlend();

        //Draw screen background
        fill(matrix, 0, 0, this.width, this.height, SCREEN_BACKGROUND_COLOR.getRGB());

        this.factoryScrollList.render(matrix);

        //Draw header
        fill(matrix, 0, 0, this.width, 50, HEADER_FOOTER_COLOR.getRGB());

        //Draw title
        drawCenteredString(matrix, font, Locals.localize("cinematica.condition.factory.select"), this.width / 2, 20, -1);

        //Draw footer
        fill(matrix, 0, this.height - 50, this.width, this.height, HEADER_FOOTER_COLOR.getRGB());

        this.backButton.setX(xCenter - (this.backButton.getWidth() / 2));
        this.backButton.setY(this.height - 35);
        this.backButton.render(matrix, mouseX, mouseY, partialTicks);

        super.render(matrix, mouseX, mouseY, partialTicks);

        //Draw description for hovered entry
        for (ScrollAreaEntry e : this.factoryScrollList.getEntries()) {
            if (e instanceof FactoryScrollAreaEntry) {
                if (e.isHovered()) {
                    renderDescription(matrix, ((FactoryScrollAreaEntry)e).factory.getDescription(), mouseX, mouseY);
                    break;
                }
            }
        }

    }

    protected static void renderDescription(MatrixStack matrix, List<String> desc, int mouseX, int mouseY) {
        if (desc != null) {
            int width = 10;
            int height = 10;
            //Getting the longest string from the list to render the background with the correct width
            for (String s : desc) {
                int i = Minecraft.getInstance().fontRenderer.getStringWidth(s) + 10;
                if (i > width) {
                    width = i;
                }
                height += 10;
            }
            mouseX += 5;
            mouseY += 5;
            if (Minecraft.getInstance().currentScreen.width < mouseX + width) {
                mouseX -= width + 10;
            }
            if (Minecraft.getInstance().currentScreen.height < mouseY + height) {
                mouseY -= height + 10;
            }
            RenderUtils.setZLevelPre(matrix, 600);
            renderDescriptionBackground(matrix, mouseX, mouseY, width, height);
            RenderSystem.enableBlend();
            int i2 = 5;
            for (String s : desc) {
                drawString(matrix, Minecraft.getInstance().fontRenderer, s, mouseX + 5, mouseY + i2, Color.WHITE.getRGB());
                i2 += 10;
            }
            RenderUtils.setZLevelPost(matrix);
            RenderSystem.disableBlend();
        }
    }

    protected static void renderDescriptionBackground(MatrixStack matrix, int x, int y, int width, int height) {
        IngameGui.fill(matrix, x, y, x + width, y + height, new Color(26, 26, 26, 250).getRGB());
    }

    protected static void colorizeButton(AdvancedButton b) {
        b.setBackgroundColor(new Color(100, 100, 100), new Color(130, 130, 130), new Color(180, 180, 180), new Color(199, 199, 199), 1);
    }

    public static class FactoryScrollAreaEntry extends ScrollAreaEntry {

        protected ConditionFactory factory;
        protected FontRenderer font = Minecraft.getInstance().fontRenderer;
        protected SelectConditionFactoryScreen parentScreen;

        protected boolean isMouseDown = false;

        public FactoryScrollAreaEntry(ScrollArea parent, ConditionFactory factory, SelectConditionFactoryScreen parentScreen) {
            super(parent);
            this.factory = factory;
            this.parentScreen = parentScreen;
        }

        @Override
        public void renderEntry(MatrixStack matrix) {

            int center = this.x + (this.getWidth() / 2);

            if (!this.isHovered()) {
                fill(matrix, this.x, this.y, this.x + this.getWidth(), this.y + this.getHeight(), ENTRY_BACKGROUND_COLOR.getRGB());
            } else {
                fill(matrix, this.x, this.y, this.x + this.getWidth(), this.y + this.getHeight(), ENTRY_BACKGROUND_COLOR.brighter().brighter().getRGB());
            }

            //Render display name
            String name = this.factory.getDisplayName();
            if (name == null) {
                name = "Nameless Condition";
            }
            drawCenteredString(matrix, font, name, center, this.y + 10, -1);

            this.handleSelection();

        }

        protected void handleSelection() {

            if (!PopupHandler.isPopupActive() && !this.parentScreen.backButton.isHovered()) {
                if (MouseInput.isLeftMouseDown() && !this.isMouseDown) {
                    if (this.isHovered()) {
                        if (this.parentScreen.callback != null) {
                            this.parentScreen.callback.accept(this.factory);
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
