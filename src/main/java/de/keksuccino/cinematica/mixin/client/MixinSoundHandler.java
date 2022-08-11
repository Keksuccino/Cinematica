package de.keksuccino.cinematica.mixin.client;

import de.keksuccino.cinematica.audio.VanillaAudioHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundManager.class)
public abstract class MixinSoundHandler {

    //TODO replace redirects with less aggressive mixins

    //Disable ALL sounds in cutscenes
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundEngine;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"), method = "play")
    private void onPlayInPlay(SoundEngine instance, SoundInstance flag1) {
        this.handlePlay(flag1, () -> {
            instance.play(flag1);
        });
    }

    //Disable ALL sounds in cutscenes
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundEngine;queueTickingSound(Lnet/minecraft/client/resources/sounds/TickableSoundInstance;)V"), method = "queueTickingSound")
    private void onPlayOnNextTickInPlayOnNextTick(SoundEngine instance, TickableSoundInstance tickableSound) {
        this.handlePlay(tickableSound, () -> {
            instance.queueTickingSound(tickableSound);
        });
    }

    //Disable ALL sounds in cutscenes
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundEngine;playDelayed(Lnet/minecraft/client/resources/sounds/SoundInstance;I)V"), method = "playDelayed")
    private void onPlayDelayedInPlayDelayed(SoundEngine instance, SoundInstance sound, int delay) {
        this.handlePlay(sound, () -> {
            instance.playDelayed(sound, delay);
        });
    }

    private void handlePlay(SoundInstance sound, Runnable playSound) {
        Screen s = Minecraft.getInstance().screen;
        if (s != null) {
            if (s.getClass().getName().startsWith("de.keksuccino.cinematica.cutscene.")) {
                if (sound.getSource() == SoundSource.MUSIC) {
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

    private boolean shouldPlaySound(SoundInstance sound) {
        if (VanillaAudioHandler.isSuppressWorldMusic() && (sound.getSource() == SoundSource.MUSIC)) {
            return false;
        }
        return true;
    }

}
