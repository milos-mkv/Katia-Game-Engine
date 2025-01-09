package org.katia.game;

import lombok.Data;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

/**
 * Handles creation of GLFW type window. Used for creating Engine window same for Game window.
 */
@Data
public class Window {

    private long handle;

    /**
     * Create window.
     * @param title Window title.
     * @param width Window width.
     * @param height Window height.
     * @throws RuntimeException When failing to create window.
     */
    public Window(String title, int width, int height) throws RuntimeException {
        handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window!");
        }

        GLFWVidMode videoMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(handle, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);

        GLFW.glfwMakeContextCurrent(handle);
        GLFW.glfwShowWindow(handle);
        GL.createCapabilities();
    }

    /**
     * Dispose of GLFW window handle.
     */
    public void dispose() {
        GLFW.glfwDestroyWindow(handle);
    }
}
