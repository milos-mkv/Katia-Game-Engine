package org.katia.core;

import org.katia.Utils;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public abstract class EngineCore {

    /**
     * Initialize glfw.
     * @throws RuntimeException When failing to initialize GLFW.
     */
    public static void initialize() throws RuntimeException {
        Utils.initialize();
        GLFWErrorCallback.createPrint(System.err).free();
        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW!");
        }
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
//        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
//        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        }

        SceneFactory.initialize();
        GameObjectFactory.initialize();
    }

    /**
     * Dispose of GLFW.
     */
    public static void dispose() {
        GLFW.glfwTerminate();
    }
}
