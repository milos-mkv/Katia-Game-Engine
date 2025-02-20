package org.katia;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public abstract class EngineCore {

    /**
     * Initialize glfw.
     * @throws RuntimeException When failing to initialize GLFW.
     */
    public static void initialize() throws RuntimeException {
        Utils.initialize();
        Logger.log(Logger.Type.INFO, "Initialize engine core ...");
        GLFWErrorCallback.createPrint(System.err).free();
        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW!");
        }
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        }
    }

    /**
     * Dispose of GLFW.
     */
    public static void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of engine core ...");
        GLFW.glfwTerminate();
    }
}
