package de.keksuccino.cinematica.gui;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.konkrete.config.ConfigEntry;
import de.keksuccino.konkrete.gui.screens.ConfigScreen;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.gui.screens.Screen;

public class CinematicaConfigScreen extends ConfigScreen {

    public CinematicaConfigScreen(Screen parent) {
        super(Cinematica.config, Locals.localize("cinematica.config"), parent);
    }

    @Override
    protected void init() {
        super.init();

        for (String s : this.config.getCategorys()) {
            this.setCategoryDisplayName(s, Locals.localize("cinematica.config.categories." + s));
        }

        for (ConfigEntry e : this.config.getAllAsEntry()) {
            this.setValueDisplayName(e.getName(), Locals.localize("cinematica.config." + e.getName()));
            this.setValueDescription(e.getName(), Locals.localize("cinematica.config." + e.getName() + ".desc"));
        }

    }
}
