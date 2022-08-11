package de.keksuccino.cinematica.cutscene;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;

public class CutScenePauseMenu extends Screen {

    protected CutScene parent;

    protected CutScenePauseMenu(CutScene parent) {
        super(Component.literal(""));
        this.parent = parent;
        CutSceneHandler.showCursor(true);
    }

    @Override
    protected void init() {

        this.parent.init(Minecraft.getInstance(), this.width, this.height);

        //Back to Game
        this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 24 + 16, 204, 20, Component.translatable("menu.returnToGame"), (press) -> {
            this.parent.isPaused = false;
            this.minecraft.setScreen(this.parent);
            CutSceneHandler.hideCursor();
            Minecraft.getInstance().getSoundManager().pause();
        }));

        //Skip
        if (this.parent.allowSkip) {
            this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 24 + 16 + 25, 204, 20, Component.literal(Locals.localize("cinematica.cutscene.skip")), (press) -> {
                this.parent.isPaused = false;
                this.parent.removed();
                Minecraft.getInstance().setScreen(null);
            }));
        }

        //Quit Game
        int quitBtnY = this.height / 4 + 24 + 16 + 25;
        if (this.parent.allowSkip) {
            quitBtnY = this.height / 4 + 24 + 16 + 50;
        }
        Component quitBtnLabel = this.minecraft.isLocalServer() ? Component.translatable("menu.returnToMenu") : Component.translatable("menu.disconnect");
        this.addRenderableWidget(new Button(this.width / 2 - 102, quitBtnY, 204, 20, quitBtnLabel, (press) -> {
            boolean isLocalMP = this.minecraft.isLocalServer();
            boolean isRealm = this.minecraft.isConnectedToRealms();
            press.active = false;
            this.minecraft.level.disconnect();
            if (isLocalMP) {
                this.minecraft.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
            } else {
                this.minecraft.clearLevel();
            }

            TitleScreen titlescreen = new TitleScreen();
            if (isLocalMP) {
                this.minecraft.setScreen(titlescreen);
            } else if (isRealm) {
                this.minecraft.setScreen(new RealmsMainScreen(titlescreen));
            } else {
                this.minecraft.setScreen(new JoinMultiplayerScreen(titlescreen));
            }
        }));

    }

    @Override
    public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {

        //render background (paused cutscene)
        this.parent.render(PoseStack, mouseX, mouseY, partialTicks);

        //render buttons
        super.render(PoseStack, mouseX, mouseY, partialTicks);

    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        this.parent.isPaused = false;
        this.minecraft.setScreen(this.parent);
        CutSceneHandler.hideCursor();
        Minecraft.getInstance().getSoundManager().pause();
    }

}
