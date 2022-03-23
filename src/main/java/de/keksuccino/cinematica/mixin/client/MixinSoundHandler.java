package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.audio.VanillaAudioHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundHandler.class)
public abstract class MixinSoundHandler {

    //Disable ALL sounds in cutscenes
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundEngine;play(Lnet/minecraft/client/audio/ISound;)V"), method = "play")
    private void onPlayInPlay(SoundEngine instance, ISound flag1) {
        this.handlePlay(flag1, () -> {
            instance.play(flag1);
        });
    }

    //Disable ALL sounds in cutscenes
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundEngine;playOnNextTick(Lnet/minecraft/client/audio/ITickableSound;)V"), method = "playOnNextTick")
    private void onPlayOnNextTickInPlayOnNextTick(SoundEngine instance, ITickableSound tickableSound) {
        this.handlePlay(tickableSound, () -> {
            instance.playOnNextTick(tickableSound);
        });
    }

    //Disable ALL sounds in cutscenes
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundEngine;playDelayed(Lnet/minecraft/client/audio/ISound;I)V"), method = "playDelayed")
    private void onPlayDelayedInPlayDelayed(SoundEngine instance, ISound sound, int delay) {
        this.handlePlay(sound, () -> {
            instance.playDelayed(sound, delay);
        });
    }

    private void handlePlay(ISound sound, Runnable playSound) {
        Screen s = Minecraft.getInstance().currentScreen;
        if (s != null) {
            if (s.getClass().getName().startsWith("de.keksuccino.cinematica.cutscene.")) {
                if (sound.getCategory() == SoundCategory.MUSIC) {
                    if (!VanillaAudioHandler.isSuppressWorldMusic()) {
                        playSound.run();
                    }
                }
            } else {
                if (shouldPlaySound(sound)) {
                    playSound.run();
                }
            }
        } else {
            if (shouldPlaySound(sound)) {
                playSound.run();
            }
        }
    }

    private boolean shouldPlaySound(ISound sound) {
        if (VanillaAudioHandler.isSuppressWorldMusic() && (sound.getCategory() == SoundCategory.MUSIC)) {
            return false;
        }
        return true;
    }

}
