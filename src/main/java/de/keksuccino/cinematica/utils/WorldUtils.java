package de.keksuccino.cinematica.utils;

import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

public class WorldUtils {

    @Nullable
    public static String getCurrentDimensionKey() {
        try {
            if (Minecraft.getInstance().world != null) {
                String ns = Minecraft.getInstance().player.world.getDimensionKey().getLocation().getNamespace();
                String path = Minecraft.getInstance().player.world.getDimensionKey().getLocation().getPath();
                return ns + ":" + path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
