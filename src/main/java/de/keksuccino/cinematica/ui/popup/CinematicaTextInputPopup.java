package de.keksuccino.cinematica.ui.popup;

import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.TextInputPopup;
import de.keksuccino.konkrete.input.CharacterFilter;

import java.awt.*;
import java.util.function.Consumer;

public class CinematicaTextInputPopup extends TextInputPopup {

	public CinematicaTextInputPopup(Color color, String title, CharacterFilter filter, int backgroundAlpha, Consumer<String> callback) {
		super(color, title, filter, backgroundAlpha, callback);
	}
	
	public CinematicaTextInputPopup(Color color, String title, CharacterFilter filter, int backgroundAlpha) {
		super(color, title, filter, backgroundAlpha);
	}
	
	@Override
	protected void colorizePopupButton(AdvancedButton b) {
		UIBase.colorizeButton(b);
	}

}
