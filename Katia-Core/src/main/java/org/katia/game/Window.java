package org.katia.game;

import lombok.Data;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.katia.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.glViewport;

/**
 * Handles creation of GLFW type window. Used for creating Engine window same for Game window.
 */
@Data
public class Window {

    Game game;
    long handle;
    Vector2i size;

    /**
     * Create window.
     * @param game Game instance.
     * @param title Window title.
     * @param width Window width.
     * @param height Window height.
     * @throws RuntimeException When failing to create window.
     */
    public Window(Game game, String title, int width, int height) throws RuntimeException {
        this.game = game;
        this.size = new Vector2i(width, height);
        handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window!");
        }

        GLFWVidMode videoMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(handle, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);

        GLFW.glfwMakeContextCurrent(handle);
        GLFW.glfwShowWindow(handle);
        GLFW.glfwSetFramebufferSizeCallback(handle, (long handle, int w, int h) -> {
            size.set(w, h);
            glViewport(0, 0, w, h);
        });
        GL.createCapabilities();
    }

    /**
     * Dispose of GLFW window handle.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of game window ...");
        GLFW.glfwDestroyWindow(handle);
    }
}
