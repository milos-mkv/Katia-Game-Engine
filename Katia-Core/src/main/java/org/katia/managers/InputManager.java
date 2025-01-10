package org.katia.managers;

import lombok.Getter;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

public class InputManager {

    @Getter
    static InputManager instance = new InputManager();

    private final boolean[] keyStates;
    private final boolean[] mouseButtonStates;
    private final Vector2f lastCursorPosition;
    private double scrollOffset = 0;

    /**
     * Input manager constructor.
     */
    public InputManager() {
        this.keyStates = new boolean[GLFW.GLFW_KEY_LAST + 1];
        this.mouseButtonStates = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
        this.lastCursorPosition = new Vector2f(0, 0);

        GLFW.glfwSetScrollCallback(GLFW.glfwGetCurrentContext(), (win, xOffset, yOffset) -> {
            scrollOffset = yOffset;
        });
    }

    /**
     * Check if key is held down.
     * @param key Key code.
     * @return boolean
     */
    public boolean isKeyPressed(int key) {
        return GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), key) == GLFW.GLFW_PRESS;
    }

    /**
     * Check if key is just pressed.
     * @param key Key code.
     * @return boolean
     */
    public boolean isKeyJustPressed(int key) {
        boolean isCurrentlyPressed = GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), key) == GLFW.GLFW_PRESS;
        if (isCurrentlyPressed && !keyStates[key]) {
            keyStates[key] = true;
            return true;
        }
        if (!isCurrentlyPressed) {
            keyStates[key] = false;
        }
        return false;
    }

    /**
     * Check if mouse button is held down.
     * @param button Mouse button code.
     * @return boolean
     */
    public boolean isMouseButtonPressed(int button) {
        return GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), button) == GLFW.GLFW_PRESS;
    }

    /**
     * Check if mouse button is just pressed.
     * @param button Mouse button code.
     * @return boolean
     */
    public boolean isMouseButtonJustPressed(int button) {
        boolean isCurrentlyPressed = GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), button) == GLFW.GLFW_PRESS;
        if (isCurrentlyPressed && !mouseButtonStates[button]) {
            mouseButtonStates[button] = true;
            return true;
        }
        if (!isCurrentlyPressed) {
            mouseButtonStates[button] = false;
        }
        return false;
    }

    /**
     * Get cursor position.
     * @return Vector2f
     */
    public Vector2f getCursorPosition() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer xBuffer = stack.mallocDouble(1);
            DoubleBuffer yBuffer = stack.mallocDouble(1);
            GLFW.glfwGetCursorPos(GLFW.glfwGetCurrentContext(), xBuffer, yBuffer);
            return new Vector2f((float) xBuffer.get(0), (float) yBuffer.get(0));
        }
    }

    /**
     * Get cursor delta movement.
     * @return Vector2f
     */
    public Vector2f getCursorDelta() {
        Vector2f currentPos = this.getCursorPosition();
        Vector2f delta = new Vector2f(currentPos.x - lastCursorPosition.x, currentPos.y -lastCursorPosition.y);
        lastCursorPosition.set(currentPos);
        return delta;
    }

    /**
     * Hide cursor.
     */
    public void hideCursor() {
        GLFW.glfwSetInputMode(GLFW.glfwGetCurrentContext(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
    }

    /**
     * Show cursor.
     */
    public void showCursor() {
        GLFW.glfwSetInputMode(GLFW.glfwGetCurrentContext(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

    /**
     * Disable cursor.
     */
    public void disableCursor() {
        GLFW.glfwSetInputMode(GLFW.glfwGetCurrentContext(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    /**
     * Get scroll offset.
     * @return double
     */
    public double getScrollOffset() {
        double offset = scrollOffset;
        scrollOffset = 0;
        return offset;
    }
}