package de.keksuccino.cinematica.video;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.konkrete.rendering.RenderUtils;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.MediaPlayerHandler;
import nick1st.fancyvideo.api.mediaPlayer.SimpleMediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.awt.*;

public class VideoRenderer {

    private static final Logger LOGGER = LogManager.getLogger("cinematica/VideoRenderer");

    protected String mediaPath;
    protected SimpleMediaPlayer player;
    protected DynamicResourceLocation playerResourceLocation;

    protected boolean playing = false;
    protected int baseVolume = 100;

    protected ResourceLocation lastFrame = null;

    public VideoRenderer(String mediaPathOrLink) {

        this.mediaPath = mediaPathOrLink;
        this.playerResourceLocation = new DynamicResourceLocation("cinematica", this.mediaPath);
        MediaPlayerHandler.getInstance().registerPlayerOnFreeResLoc(this.playerResourceLocation, SimpleMediaPlayer.class);
        this.player = (SimpleMediaPlayer) MediaPlayerHandler.getInstance().getMediaPlayer(this.playerResourceLocation);
        if (this.player != null) {
            this.player.api().media().prepare(mediaPathOrLink);
        } else {
            LOGGER.error("ERROR: Unable to initialize player for media: " + this.mediaPath);
        }

    }

    public void render(PoseStack matrix, int posX, int posY, int width, int height) {

        try {

            if (this.player != null) {
                this.lastFrame = this.player.renderToResourceLocation();
            }

            if (this.lastFrame != null) {
                RenderUtils.bindTexture(this.lastFrame);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                GuiComponent.blit(matrix, posX, posY, 0.0F, 0.0F, width, height, width, height);
                RenderSystem.disableBlend();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void play() {
        if (!isPlaying()) {
            if (this.player != null) {
                this.playing = true;
                this.player.api().controls().play();
            }
        }
    }

    public void pause() {
        if (isPlaying()) {
            if (this.player != null) {
                this.playing = false;
                this.player.api().controls().pause();
            }
        }
    }

    public void stop() {
        if (this.player != null) {
            this.playing = false;
            this.player.api().controls().stop();
        }
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void setLooping(boolean b) {
        if (this.player != null) {
            this.player.api().controls().setRepeat(b);
        }
    }

    public boolean isLooping() {
        if (this.player != null) {
            return this.player.api().controls().getRepeat();
        }
        return false;
    }

    /**
     * <b>FOR INTERNAL USE ONLY!</b><br>
     * Use {@link VideoRenderer#setBaseVolume(int)} instead, if you want to set the video volume.<br><br>
     *
     * @param volume Value between 0 and 200.
     */
    public void setVolume(int volume) {
        if (this.player != null) {
            if (volume < 0) {
                volume = 0;
            }
            if (volume > 200) {
                volume = 200;
            }
            this.player.api().audio().setVolume(volume);
        }
    }

    public int getVolume() {
        if (this.player != null) {
            return this.player.api().audio().volume();
        }
        return -1;
    }

    public void setBaseVolume(int vol) {
        this.baseVolume = vol;
        VideoVolumeHandler.updateRendererVolume(this);
    }

    public int getBaseVolume() {
        return this.baseVolume;
    }

    public void setTime(long time) {
        if (this.player != null) {
            this.player.api().controls().setTime(time);
        }
    }

    public void restart() {
        this.setTime(0L);
    }

    public ResourceLocation getLastFrame() {
        return this.lastFrame;
    }

    public boolean canPlay() {
        try {
            Dimension d = this.getVideoDimension();
            if (d != null) {
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    public String getMediaPath() {
        return this.mediaPath;
    }

    @Nullable
    public Dimension getVideoDimension() {
        if (this.player != null) {
            return this.player.api().video().videoDimension();
        }
        return null;
    }

    public SimpleMediaPlayer getPlayer() {
        return this.player;
    }

    public void destroy() {
        if (this.player != null) {
            this.stop();
            this.player.cleanup();
        }
    }

}
