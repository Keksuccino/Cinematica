package de.keksuccino.cinematica.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.keksuccino.cinematica.engine.cinematic.Cinematic;
import de.keksuccino.cinematica.engine.cinematic.CinematicHandler;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class CinematicCommand {

    public static void register(CommandDispatcher<CommandSource> d) {
        d.register(Commands.literal("cinematic")
                .then(Commands.argument("identifier", StringArgumentType.string())
                .then(Commands.argument("ignore_only_trigger_once", BoolArgumentType.bool())
                .executes((stack) -> {
                    return triggerCinematic(stack.getSource(), StringArgumentType.getString(stack, "identifier"), BoolArgumentType.getBool(stack, "ignore_only_trigger_once"));
                })
                )
                )
        );
    }

    private static int triggerCinematic(CommandSource stack, String cinematicIdentifier, boolean forceTrigger) {
        try {
            Cinematic cTemp = null;
            for (Cinematic cin : CinematicHandler.getCinematics()) {
                if (cin.getIdentifier().equals(cinematicIdentifier)) {
                    cTemp = cin;
                }
            }
            if (cTemp != null) {
                final Cinematic c = cTemp;
                Minecraft.getInstance().execute(() -> {
                    CinematicHandler.forceTriggerCinematic(c, forceTrigger);
                });
            } else {
                stack.sendErrorMessage(new StringTextComponent(Locals.localize("cinematica.command.cinematic.invalid")));
            }
        } catch (Exception e) {
            stack.sendErrorMessage(new StringTextComponent(Locals.localize("cinematica.command.cinematic.error")));
            e.printStackTrace();
        }
        return 1;
    }

}