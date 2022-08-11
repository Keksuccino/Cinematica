package de.keksuccino.cinematica.ui.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.FilePickerPopup;
import de.keksuccino.konkrete.gui.screens.popup.Popup;
import net.minecraft.client.gui.screens.Screen;

import java.io.File;
import java.util.function.Consumer;

public class CinematicaFilePickerPopup extends FilePickerPopup {

	public CinematicaFilePickerPopup(String directory, String home, Popup fallback, boolean checkForLastPath, Consumer<File> callback, String[] filetypes) {
		super(directory, home, fallback, checkForLastPath, callback, filetypes);
	}
	
	public CinematicaFilePickerPopup(String directory, String home, Popup fallback, boolean checkForLastPath, Consumer<File> callback) {
		super(directory, home, fallback, checkForLastPath, callback);
	}
	
	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, Screen renderIn) {
		super.render(matrix, mouseX, mouseY, renderIn);
	}
	
	@Override
	protected void colorizePopupButton(AdvancedButton b) {
		UIBase.colorizeButton(b);
	}

}
