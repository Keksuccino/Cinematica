package de.keksuccino.cinematica.ui.popup;

import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.YesNoPopup;

import java.awt.*;
import java.util.function.Consumer;

public class CinematicaYesNoPopup extends YesNoPopup {

	public CinematicaYesNoPopup(int width, Color color, int backgroundAlpha, Consumer<Boolean> callback, String... text) {
		super(width, color, backgroundAlpha, callback, text);
	}
	
	@Override
	protected void colorizePopupButton(AdvancedButton b) {
		UIBase.colorizeButton(b);
	}

}
