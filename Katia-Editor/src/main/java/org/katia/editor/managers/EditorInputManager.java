package org.katia.editor.managers;

import lombok.Getter;
import org.joml.Vector2f;
import org.katia.editor.Editor;
import org.katia.editor.EditorWindow;
import org.katia.game.Game;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

public class EditorInputManager {

    @Getter
    static EditorInputManager instance = new EditorInputManager();

    final boolean[] keyStates;
    final boolean[] mouseButtonStates;
    final Vector2f lastCursorPosition;
    double scrollOffset = 0;

    /**
     * Editor input manager constructor.
     */
    public EditorInputManager() {
        this.keyStates = new boolean[GLFW.GLFW_KEY_LAST + 1];
        this.mouseButtonStates = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
        this.lastCursorPosition = new Vector2f(0, 0);

//        GLFW.glfwSetScrollCallback(Editor.getInstance().getHandle(), (win, xOffset, yOffset) -> {
//            scrollOffset = yOffset;
//        });
    }


    /**
     * Check if key is held down.
     * @param key Key code.
     * @return boolean
     */
    public boolean isKeyPressed(int key) {
        return GLFW.glfwGetKey(EditorWindow.getInstance().getHandle(), key) == GLFW.GLFW_PRESS;
    }

    /**
     * Check if key is just pressed.
     * @param key Key code.
     * @return boolean
     */
    public boolean isKeyJustPressed(int key) {
        boolean isCurrentlyPressed = GLFW.glfwGetKey(EditorWindow.getInstance().getHandle(), key) == GLFW.GLFW_PRESS;
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
        return GLFW.glfwGetMouseButton(EditorWindow.getInstance().getHandle(), button) == GLFW.GLFW_PRESS;
    }

    /**
     * Check if mouse button is just pressed.
     * @param button Mouse button code.
     * @return boolean
     */
    public boolean isMouseButtonJustPressed(int button) {
        boolean isCurrentlyPressed = GLFW.glfwGetMouseButton(EditorWindow.getInstance().getHandle(), button) == GLFW.GLFW_PRESS;
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
            GLFW.glfwGetCursorPos(EditorWindow.getInstance().getHandle(), xBuffer, yBuffer);
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
        GLFW.glfwSetInputMode(EditorWindow.getInstance().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
    }

    /**
     * Show cursor.
     */
    public void showCursor() {
        GLFW.glfwSetInputMode(EditorWindow.getInstance().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

    /**
     * Disable cursor.
     */
    public void disableCursor() {
        GLFW.glfwSetInputMode(EditorWindow.getInstance().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
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
