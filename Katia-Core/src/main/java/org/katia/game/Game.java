package org.katia.game;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.katia.Logger;
import org.katia.gfx.resources.FrameBuffer;
import org.katia.managers.AudioManager;
import org.katia.managers.ResourceManager;
import org.katia.scripting.LuaScriptExecutioner;
import org.katia.gfx.SceneRenderer;
import org.katia.managers.InputManager;
import org.katia.managers.SceneManager;
import org.lwjgl.glfw.GLFW;

@Data
@NoArgsConstructor
public class Game {

    Configuration configuration;
    Window window;
    SceneManager sceneManager;
    ResourceManager resourceManager;
    InputManager inputManager;
    LuaScriptExecutioner scriptExecutioner;

    String directory;
    SceneRenderer sceneRenderer;
    boolean debug;
    float deltaTime = 0;
    float previousTime = 0;
    AudioManager audioManager;

    /**
     * Crete new game instance.
     */
    public Game(String directory) {
        Logger.log(Logger.Type.INFO, "Creating new game instance:", directory);
    }

    /**
     * Update every game frame.
     */
    public void update(FrameBuffer frameBuffer) {
        // NOTE: I window exists game is run as standalone game. If not we already have context from game editor.
        if (window != null) {
            GLFW.glfwMakeContextCurrent(window.getHandle());
        }
        GLFW.glfwSwapInterval(0);
        previousTime = calculateDeltaTime(previousTime);
        GLFW.glfwPollEvents();

        // NOTE: Run scripts only is we run game in separate window.
        if (window != null) {
            scriptExecutioner.update(deltaTime);
        }

        sceneRenderer.render(frameBuffer);

        if (window != null) {
            GLFW.glfwSwapBuffers(window.getHandle());
        }
    }

    /**
     * Calculate delta time.
     * @param previousTime Previous time.
     * @return float
     */
    private float calculateDeltaTime(float previousTime) {
        float currentTime = (float) GLFW.glfwGetTime();
        deltaTime = currentTime - previousTime;
        return currentTime;
    }

    /**
     * Dispose of game.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of game instance ...");
        audioManager.dispose();
        window.dispose();
    }
}
