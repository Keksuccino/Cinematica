package de.keksuccino.cinematica.ui.popup;

import de.keksuccino.cinematica.ui.UIBase;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.NotificationPopup;

import java.awt.*;

public class CinematicaNotificationPopup extends NotificationPopup {

	public CinematicaNotificationPopup(int width, Color color, int backgroundAlpha, Runnable callback, String... text) {
		super(width, color, backgroundAlpha, callback, text);
	}

	@Override
	protected void colorizePopupButton(AdvancedButton b) {
		UIBase.colorizeButton(b);
	}

}
