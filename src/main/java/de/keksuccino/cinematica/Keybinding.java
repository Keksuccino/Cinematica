package de.keksuccino.cinematica;

import de.keksuccino.konkrete.config.exceptions.InvalidValueException;
import de.keksuccino.konkrete.input.KeyboardHandler;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Keybinding {

	public static KeyMapping KeyToggleUI;

	public static boolean initialized = false;

	public static void init() {

		FMLJavaModLoadingContext.get().getModEventBus().register(Keybinding.class);

	}

	@SubscribeEvent
	public static void registerKeyBinds(RegisterKeyMappingsEvent e) {

		if (!initialized) {
			KeyToggleUI = new KeyMapping("Toggle UI Visibility | CTRL + ALT + ", 67, "Cinematica");
			initGuiClickActions();
			initialized = true;
		}

		e.register(KeyToggleUI);

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
