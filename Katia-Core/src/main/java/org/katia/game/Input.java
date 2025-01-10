package org.katia.game;

import org.lwjgl.glfw.GLFW;

public class Input {

    public static boolean isKeyDown( int key) {
        int keyState = GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), key);
        return keyState == GLFW.GLFW_PRESS || keyState == GLFW.GLFW_REPEAT;
    }

    public static boolean isKeyPressed( int key) {
        int keyState = GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), key);
        return keyState == GLFW.GLFW_PRESS;
    }

    public static boolean isKeyReleased( int key) {
        int keyState = GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), key);
        return keyState == GLFW.GLFW_RELEASE;
    }
}
