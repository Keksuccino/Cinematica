package de.keksuccino.cinematica.cutscene;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CutScenePauseMenu extends Screen {

    protected CutScene parent;

    protected CutScenePauseMenu(CutScene parent) {
        super(new StringTextComponent(""));
        this.parent = parent;
        CutSceneHandler.showCursor(true);
    }

    @Override
    protected void init() {

        this.parent.init(Minecraft.getInstance(), this.width, this.height);

        //Back to Game
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 + 16, 204, 20, new TranslationTextComponent("menu.returnToGame"), (press) -> {
            this.parent.isPaused = false;
            this.minecraft.displayGuiScreen(this.parent);
            CutSceneHandler.hideCursor();
            Minecraft.getInstance().getSoundHandler().pause();
        }));

        //Skip
        if (this.parent.allowSkip) {
            this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 + 16 + 25, 204, 20, new StringTextComponent(Locals.localize("cinematica.cutscene.skip")), (press) -> {
                this.parent.isPaused = false;
                this.parent.onClose();
                Minecraft.getInstance().displayGuiScreen(null);
            }));
        }

        //Quit Game
        int quitBtnY = this.height / 4 + 24 + 16 + 25;
        if (this.parent.allowSkip) {
            quitBtnY = this.height / 4 + 24 + 16 + 50;
        }
        Button quitBtn = this.addButton(new Button(this.width / 2 - 102, quitBtnY, 204, 20, new TranslationTextComponent("menu.returnToMenu"), (press) -> {
            boolean isLocalMultiplayer = this.minecraft.isIntegratedServerRunning();
            boolean isRealms = this.minecraft.isConnectedToRealms();
            press.active = false;
            this.minecraft.world.sendQuittingDisconnectingPacket();
            if (isLocalMultiplayer) {
                this.minecraft.unloadWorld(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
            } else {
                this.minecraft.unloadWorld();
            }
            if (isLocalMultiplayer) {
                this.minecraft.displayGuiScreen(new MainMenuScreen());
            } else if (isRealms) {
                RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                realmsbridgescreen.func_231394_a_(new MainMenuScreen());
            } else {
                this.minecraft.displayGuiScreen(new MultiplayerScreen(new MainMenuScreen()));
            }
            this.parent.isPaused = false;
            this.parent.onClose();
        }));
        if (!this.minecraft.isIntegratedServerRunning()) {
            quitBtn.setMessage(new TranslationTextComponent("menu.disconnect"));
        }

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        //render background (paused cutscene)
        this.parent.render(matrixStack, mouseX, mouseY, partialTicks);

        //render buttons
        super.render(matrixStack, mouseX, mouseY, partialTicks);

    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void closeScreen() {
        this.parent.isPaused = false;
        this.minecraft.displayGuiScreen(this.parent);
        CutSceneHandler.hideCursor();
        Minecraft.getInstance().getSoundHandler().pause();
    }

}
