package de.keksuccino.cinematica.cutscene;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class CutSceneHandler {

    public static CutScene activeCutScene = null;

    public static void hideCursor() {
        GLFW.glfwSetInputMode(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        double mouseX = Minecraft.getInstance().getMainWindow().getWidth() / 2;
        double mouseY = Minecraft.getInstance().getMainWindow().getHeight() / 2;
        GLFW.glfwSetCursorPos(Minecraft.getInstance().getMainWindow().getHandle(), mouseX, mouseY);
    }

    public static void showCursor(boolean setCursorToCenter) {
        GLFW.glfwSetInputMode(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        if (setCursorToCenter) {
            double mouseX = Minecraft.getInstance().getMainWindow().getWidth() / 2;
            double mouseY = Minecraft.getInstance().getMainWindow().getHeight() / 2;
            GLFW.glfwSetCursorPos(Minecraft.getInstance().getMainWindow().getHandle(), mouseX, mouseY);
        }
    }

}
