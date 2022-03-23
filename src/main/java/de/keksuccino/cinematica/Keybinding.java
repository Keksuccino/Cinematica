package de.keksuccino.cinematica;

import de.keksuccino.konkrete.config.exceptions.InvalidValueException;
import de.keksuccino.konkrete.input.KeyboardHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keybinding {

	public static KeyBinding KeyToggleUI;
	
	public static void init() {

		KeyToggleUI = new KeyBinding("Toggle UI Visibility | CTRL + ALT + ", 67, "Cinematica");
		ClientRegistry.registerKeyBinding(KeyToggleUI);
		
		initGuiClickActions();

	}
	
	private static void initGuiClickActions() {

		KeyboardHandler.addKeyPressedListener((c) -> {

			if ((KeyToggleUI.getKey().getKeyCode() == c.keycode) && KeyboardHandler.isCtrlPressed() && KeyboardHandler.isAltPressed()) {
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
