package de.keksuccino.cinematica.ui.slider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.function.Consumer;

public class AdvancedVolumeSlider extends AbstractOptionSliderButton {

    protected Consumer<Integer> callback;
    protected String volumeDisplayName;

    public AdvancedVolumeSlider(int x, int y, int width, int height, String volumeDisplayName, int defaultPercent, Consumer<Integer> volumeCallback) {
        super(Minecraft.getInstance().options, x, y, width, height, getDoubleOfPercent(defaultPercent));
        this.volumeDisplayName = volumeDisplayName;
        this.callback = volumeCallback;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        Component percentText = ((float)this.value == (float)this.getYImage(false) ? CommonComponents.OPTION_OFF : new TextComponent((int)(this.value * 100.0D) + "%"));
        this.setMessage((new TextComponent(this.volumeDisplayName)).append(": ").append(percentText));
    }

    @Override
    protected void applyValue() {
        this.callback.accept(getPercentOfDouble(this.value));
    }

    public static double getDoubleOfPercent(int percent) {
        return (((double)percent) / 100.0D);
    }

    public static int getPercentOfDouble(double d) {
        return ((int)(d * 100.0D));
    }

}
