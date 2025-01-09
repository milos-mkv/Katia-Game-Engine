package org.katia.game;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.katia.Logger;
import org.lwjgl.glfw.GLFW;

@Data
@JsonDeserialize
public class Game {

    Configuration configuration;
    Window window;

    /**
     * Crete new game instance.
     */
    public Game() {
        Logger.log(Logger.Type.INFO, "Creating new game instance!");
        configuration = Configuration.load();
        window = new Window(configuration.title, configuration.width, configuration.height);
    }

    /**
     * Run game.
     * @return Game
     */
    public Game run() {
        Logger.log(Logger.Type.INFO, "Run game instance!");
        while (!GLFW.glfwWindowShouldClose(window.getHandle())) {
            GLFW.glfwPollEvents();
            GLFW.glfwSwapBuffers(window.getHandle());
        }
        return this;
    }

    /**
     * Dispose of game.
     */
    public void dispose() {
        Logger.log(Logger.Type.INFO, "Dispose of game instance!");
        window.dispose();
    }

}
