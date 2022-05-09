package de.keksuccino.cinematica;

import de.keksuccino.konkrete.config.exceptions.InvalidValueException;
import de.keksuccino.konkrete.input.KeyboardHandler;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class Keybinding {

	public static KeyMapping KeyToggleUI;
	
	public static void init() {

		KeyToggleUI = new KeyMapping("Toggle UI Visibility | CTRL + ALT + ", 67, "Cinematica");
		ClientRegistry.registerKeyBinding(KeyToggleUI);
		
		initGuiClickActions();

	}
	
	private static void initGuiClickActions() {

		KeyboardHandler.addKeyPressedListener((c) -> {

			if ((KeyToggleUI.getKey().getValue() == c.keycode) && KeyboardHandler.isCtrlPressed() && KeyboardHandler.isAltPressed()) {
				try {
					if (Cinematica.config.getOrDefault("show_controls_in_pause_screen", true)) {
						Cinematica.config.setValue("show_controls_in_pause_screen", false);
					} else {
						Cinematica.config.setValue("show_controls_in_pause_screen", true);
					}
					Cinematica.config.syncConfig();
				} catch (InvalidValueException e) {
					e.printStackTrace();
				}
			}

		});

	}
}
