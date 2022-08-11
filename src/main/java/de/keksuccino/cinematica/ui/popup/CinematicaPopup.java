package de.keksuccino.cinematica.ui.popup;

import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.Popup;

public class CinematicaPopup extends Popup {

	public CinematicaPopup(int backgroundAlpha) {
		super(backgroundAlpha);
	}
	
	@Override
	protected void colorizePopupButton(AdvancedButton b) {
		UIBase.colorizeButton(b);
	}

}
