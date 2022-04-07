package de.keksuccino.cinematica.cutscene;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.cinematica.audio.AudioCinematicHandler;
import de.keksuccino.cinematica.video.VideoRenderer;
import de.keksuccino.konkrete.input.KeyboardHandler;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.rendering.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;

public class CutScene extends Screen {

    private static ResourceLocation BLACK_TEXTURE = null;

    protected VideoRenderer renderer;

    protected int fadeTicks = 20;
    protected boolean fadeIn;
    protected int fadeInTicker = fadeTicks;
    protected boolean fadeOut;
    protected int fadeOutTicker = 0;
    protected float fadeInAlpha = 0.0F;
    protected float fadeOutAlpha = 1.0F;

    public boolean startedPlaying = false;
    public boolean isFinished = false;
    public boolean isPaused = false;

    protected ResourceLocation lastFrame = null;
    protected int lastFrameWidth = 0;
    protected int lastFrameHeight = 0;

    protected int keyPressedListenerId;
    protected int keyReleasedListenerId;
    protected boolean enterPressed = false;
    protected boolean enterPressedOnce = false;
    protected long enterPressedTime = 0;

    public boolean allowSkip = true;
    public boolean keepAspectRatio = true;

    public CutScene(VideoRenderer renderer, boolean fadeIn, boolean fadeOut) {

        super(new StringTextComponent(""));

        CutSceneHandler.activeCutScene = this;

        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;

        this.renderer = renderer;
        this.renderer.stop();
        this.renderer.setLooping(false);
        this.renderer.restart();

        //PRESSED listener
        this.keyPressedListenerId = KeyboardHandler.addKeyPressedListener((key) -> {
            if (Minecraft.getInstance().currentScreen == this) {
                //ENTER
                if (key.keycode == 257) {
                    if (this.allowSkip) {
                        if (!this.enterPressed) {
                            if (this.enterPressedOnce) {
                                this.closeCutScene();
                            }
                            this.enterPressedTime = System.currentTimeMillis();
                            this.enterPressedOnce = true;
                            this.enterPressed = true;
                        }
                    }
                }
            }
        });

        //RELEASED listener
        this.keyReleasedListenerId = KeyboardHandler.addKeyReleasedListener((key) -> {
            //ENTER
            if (key.keycode == 257) {
                this.enterPressed = false;
            }
        });

        CutSceneHandler.hideCursor();

    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        try {

            this.renderSceneBackground();

            this.renderFade(matrixStack);

            this.renderScene(matrixStack, 1.0F);

            if (!this.isPaused) {
                CutSceneHandler.hideCursor();
                this.handleEnterSkipping(matrixStack);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void renderFade(MatrixStack matrix) {

        //Handle fade-in
        if (this.fadeIn) {

            float f = 1.0F / this.fadeTicks;
            this.fadeInAlpha += f;
            if (this.fadeInAlpha > 1.0F) {
                this.fadeInAlpha = 1.0F;
            }

            Minecraft.getInstance().textureManager.bindTexture(getBlackTextureLocation());
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.fadeInAlpha);
            blit(matrix, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);

            if (this.fadeInTicker <= 0) {
                this.fadeIn = false;
            }
            if (!this.isPaused) {
                this.fadeInTicker--;
            }

        }

        //Handle fade-out + close screen when finished
        if (this.isFinished) {
            if (this.fadeOut) {

                float f = 1.0F / fadeTicks;
                this.fadeOutAlpha -= f;
                if (this.fadeOutAlpha < 0.0F) {
                    this.fadeOutAlpha = 0.0F;
                }

                Minecraft.getInstance().textureManager.bindTexture(getBlackTextureLocation());
                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.fadeOutAlpha);
                blit(matrix, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);

                if (this.fadeOutTicker >= this.fadeTicks) {
                    this.closeCutScene();
                }
                if (!this.isPaused) {
                    this.fadeOutTicker++;
                }

            } else {
                this.closeCutScene();
            }
        }

    }

    protected void renderScene(MatrixStack matrixStack, float alpha) {

        int xCenter = this.width / 2;

        if (!this.fadeIn && !this.isFinished) {
            if (this.renderer != null) {

                if (!this.isPaused) {
                    this.renderer.play();
                } else {
                    this.renderer.pause();
                    if (this.lastFrame != null) {
                        if (!this.keepAspectRatio) {
                            this.renderLastFrame(matrixStack, 0, 0, this.width, this.height, 1.0F);
                        } else {
                            int w = this.lastFrameWidth;
                            int h = this.lastFrameHeight;
                            double ratio = (double) w / (double) h;
                            int wFinal = (int)(this.height * ratio);
                            if (wFinal < this.width) {
                                this.renderLastFrame(matrixStack, 0, 0, this.width, this.height, alpha);
                            } else {
                                this.renderLastFrame(matrixStack, xCenter - (wFinal / 2), 0, wFinal, this.height, alpha);
                            }
                        }
                        RenderSystem.enableBlend();
                        AbstractGui.fill(matrixStack, 0, 0, this.width, this.height, new Color(0, 0, 0, 140).getRGB());
                    }
                }

                if (!this.isPaused) {
                    if (this.renderer.isPlaying() && this.renderer.canPlay()) {

                        this.startedPlaying = true;

                        if (!this.keepAspectRatio) {
                            this.renderer.render(matrixStack, 0, 0, this.width, this.height);
                        } else {
                            int w = (int) this.renderer.getVideoDimension().getWidth();
                            int h = (int) this.renderer.getVideoDimension().getHeight();
                            double ratio = (double) w / (double) h;
                            int wFinal = (int)(this.height * ratio);
                            if (wFinal < this.width) {
                                this.renderer.render(matrixStack, 0, 0, this.width, this.height);
                            } else {
                                this.renderer.render(matrixStack, xCenter - (wFinal / 2), 0, wFinal, this.height);
                            }
                            this.lastFrameWidth = w;
                            this.lastFrameHeight = h;
                        }

                        this.lastFrame = this.renderer.getLastFrame();

                    } else if (this.startedPlaying) {
                        this.isFinished = true;
                        this.renderFade(matrixStack);
                    }
                }

            }
        }

    }

    protected void renderLastFrame(MatrixStack matrixStack, int posX, int posY, int width, int height, float alpha) {
        if (this.lastFrame != null) {
            Minecraft.getInstance().textureManager.bindTexture(this.lastFrame);
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            AbstractGui.blit(matrixStack, posX, posY, 0.0F, 0.0F, width, height, width, height);
        }
    }

    protected void handleEnterSkipping(MatrixStack matrixStack) {
        if (this.allowSkip) {
            int xCenter = this.width / 2;
            if (this.enterPressedOnce) {
                long now = System.currentTimeMillis();
                if ((this.enterPressedTime + 4000) < now) {
                    this.enterPressedOnce = false;
                    this.enterPressedTime = 0;
                } else {
                    drawCenteredString(matrixStack, font, Locals.localize("cinematica.cutscene.confirmskip"), xCenter, this.height - 40, -1);
                }
            }
        }
    }

    protected void renderSceneBackground() {

        if (this.minecraft.world == null) {

            int vOffset = 0;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.minecraft.getTextureManager().bindTexture(BACKGROUND_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(0.0D, this.height, 0.0D).tex(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(this.width, this.height, 0.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(this.width, 0.0D, 0.0D).tex((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
            tessellator.draw();

        }

    }

    public void closeCutScene() {
        Minecraft.getInstance().displayGuiScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    //Used as "onEscapePressed"
    @Override
    public void closeScreen() {
        this.isPaused = true;
        Minecraft.getInstance().displayGuiScreen(new CutScenePauseMenu(this));
    }

    @Override
    public void onClose() {
        this.renderer.pause();
        if (!this.isPaused) {
            KeyboardHandler.removeKeyPressedListener(this.keyPressedListenerId);
            KeyboardHandler.removeKeyReleasedListener(this.keyReleasedListenerId);
            CutSceneHandler.activeCutScene = null;
        }
        this.enterPressedOnce = false;
    }

    private static ResourceLocation getBlackTextureLocation() {
        if (BLACK_TEXTURE != null) {
            return BLACK_TEXTURE;
        }
        if (Minecraft.getInstance().getTextureManager() == null) {
            return null;
        }
        NativeImage i = new NativeImage(1, 1, true);
        i.setPixelRGBA(0, 0, Color.BLACK.getRGB());
        ResourceLocation r = Minecraft.getInstance().getTextureManager().getDynamicTextureLocation("blackback", new DynamicTexture(i));
        BLACK_TEXTURE = r;
        return r;
    }

}
