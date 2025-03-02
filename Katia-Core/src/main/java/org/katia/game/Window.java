package org.katia.game;

import lombok.Getter;
import org.joml.Vector2i;
import org.katia.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.glViewport;

/**
 * Handles creation of game window. This class also has to be exposed to lua vm so that user can access game
 * window size.
 * @see org.katia.scripting.LuaScriptExecutioner
 */
public class Window {

    private final Game game;
    @Getter
    private final long handle;
    @Getter
    private Vector2i size;

    /**
     * Create window.
     * @param game Game instance.
     * @throws RuntimeException When failing to create window.
     */
    public Window(Game game) throws RuntimeException {
        this.game = game;

        Configuration configuration = game.getConfiguration();
        size = new Vector2i(configuration.width, configuration.height);
        handle = GLFW.glfwCreateWindow(configuration.width, configuration.height, configuration.title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window!");
        }

        GLFWVidMode videoMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(handle, (videoMode.width() - configuration.width) / 2, (videoMode.height() - configuration.height) / 2);

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
        Logger.log(Logger.Type.DISPOSE, "Disposing of game window:", String.valueOf(handle));
        GLFW.glfwDestroyWindow(handle);
    }
}
