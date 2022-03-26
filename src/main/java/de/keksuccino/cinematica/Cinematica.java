package de.keksuccino.cinematica;

import java.io.File;

import de.keksuccino.cinematica.audio.AudioCinematicVolumeHandler;
import de.keksuccino.cinematica.audio.VanillaAudioHandler;
import de.keksuccino.cinematica.engine.cinematic.CinematicHandler;
import de.keksuccino.cinematica.engine.condition.ConditionFactoryRegistry;
import de.keksuccino.cinematica.video.VideoVolumeHandler;
import de.keksuccino.cinematica.engine.condition.conditions.ConditionFactories;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Pair;

import de.keksuccino.konkrete.Konkrete;
import de.keksuccino.konkrete.config.Config;
import de.keksuccino.konkrete.config.exceptions.InvalidValueException;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO server IP condition

//TODO is Multiplayer condition

//TODO is Singleplayer condition

//TODO "name" value zu cinematic base adden (damit man einen anzeigenamen für seine cinematics setzen kann)

//TODO alle conditions von notes file adden

//TODO eventuell volume handling in Auudio zurück zu alter logik

@Mod("cinematica")
public class Cinematica {

	public static final String VERSION = "1.0.0";

	public static final Logger LOGGER = LogManager.getLogger();

	public static final File MOD_DIRECTORY = new File("config/cinematica");
	public static final File CINEMATICA_TEMP_DIR = new File("cinematica_temp_data");

	public static Config config;

	public Cinematica() {
		try {

			ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

			//Check if mod was loaded client- or serverside
	    	if (FMLEnvironment.dist == Dist.CLIENT) {

				if (!MOD_DIRECTORY.isDirectory()) {
					MOD_DIRECTORY.mkdirs();
				}
				if (!CINEMATICA_TEMP_DIR.isDirectory()) {
					CINEMATICA_TEMP_DIR.mkdirs();
				}

	    		updateConfig();

				ConditionFactoryRegistry.init();

				ConditionFactories.registerAll();

				if (config.getOrDefault("enable_keybinds", true)) {
					Keybinding.init();
				}

	        	Konkrete.addPostLoadingEvent("cinematica", this::onClientSetup);

				MinecraftForge.EVENT_BUS.register(new EventHandler());

//				MinecraftForge.EVENT_BUS.register(new Test());

	    	} else {
	    		System.out.println("## WARNING ## 'Cinematica' is a client mod and has no effect when loaded on a server!");
	    	}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void onClientSetup() {
		try {

			initLocals();

			VanillaAudioHandler.init();

			CinematicHandler.init();

			VideoVolumeHandler.init();

			AudioCinematicVolumeHandler.init();
	    	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void initLocals() {
		String baseResDir = "locals/";
		File f = new File(MOD_DIRECTORY.getPath() + "/locals");
		if (!f.exists()) {
			f.mkdirs();
		}
		
		Locals.copyLocalsFileToDir(new ResourceLocation("cinematica", baseResDir + "en_us.local"), "en_us", f.getPath());
//		Locals.copyLocalsFileToDir(new ResourceLocation("cinematica", baseResDir + "de_de.local"), "de_de", f.getPath());
		
		Locals.getLocalsFromDir(f.getPath());
	}

	public static void updateConfig() {
    	try {

    		config = new Config(MOD_DIRECTORY.getPath() + "/config.cfg");

			config.registerValue("enable_keybinds", true, "general");

			config.registerValue("uiscale", 1.0F, "ui");
			config.registerValue("show_controls_in_pause_screen", true, "ui");
			config.registerValue("add_slider_to_sound_controls", true, "ui");
			
			config.syncConfig();
			
			config.clearUnusedValues();

		} catch (InvalidValueException e) {
			e.printStackTrace();
		}
	}

	public static boolean isFancyMenuLoaded() {
		try {
			Class.forName("de.keksuccino.fancymenu.FancyMenu");
			return true;
		} catch (Exception e) {}
		return false;
	}

}
