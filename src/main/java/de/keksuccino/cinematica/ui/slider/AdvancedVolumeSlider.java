package de.keksuccino.cinematica.ui.slider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.GameSettingsSlider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Consumer;

public class AdvancedVolumeSlider extends GameSettingsSlider {

    protected Consumer<Integer> callback;
    protected String volumeDisplayName;

    public AdvancedVolumeSlider(int x, int y, int width, int height, String volumeDisplayName, int defaultPercent, Consumer<Integer> volumeCallback) {
        super(Minecraft.getInstance().gameSettings, x, y, width, height, getDoubleOfPercent(defaultPercent));
        this.volumeDisplayName = volumeDisplayName;
        this.callback = volumeCallback;
        this.func_230979_b_();
    }

    @Override
    protected void func_230979_b_() {
        ITextComponent percentText = ((float)this.sliderValue == (float)this.getYImage(false) ? DialogTexts.OPTIONS_OFF : new StringTextComponent((int)(this.sliderValue * 100.0D) + "%"));
        this.setMessage((new StringTextComponent(this.volumeDisplayName)).appendString(": ").appendSibling(percentText));
    }

    @Override
    protected void func_230972_a_() {
        this.callback.accept(getPercentOfDouble(this.sliderValue));
    }

    public static double getDoubleOfPercent(int percent) {
        return (((double)percent) / 100.0D);
    }

    public static int getPercentOfDouble(double d) {
        return ((int)(d * 100.0D));
    }

}
