package org.katia.game;

import lombok.Data;
import org.katia.Logger;
import org.katia.core.Scene;
import org.katia.core.ScriptExecutioner;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.katia.gfx.SceneRenderer;
import org.katia.managers.AssetManager;
import org.katia.managers.SceneManager;
import org.katia.managers.ScriptManager;
import org.lwjgl.glfw.GLFW;

@Data
public class Game {

    Configuration configuration;
    Window window;
    AssetManager assetManager;
    SceneManager sceneManager;
    ScriptManager scriptManager;


    /**
     * Crete new game instance.
     */
    public Game(String directory) {
        Logger.log(Logger.Type.INFO, "Creating new game instance!");
        configuration = Configuration.load(directory + "/katia-conf.json");
        window = new Window(this, configuration.title, configuration.width, configuration.height);

        SceneFactory.initialize();
        GameObjectFactory.initialize();

        // Load resources
        assetManager = new AssetManager(this, directory + "/assets");
        sceneManager = new SceneManager(this, directory + "/scenes");
        scriptManager = new ScriptManager(this, directory + "/scripts");
    }

    /**
     * Run game.
     * @return Game
     */
    public Game run() {
        Logger.log(Logger.Type.INFO, "Run game instance!");

        Scene scene = sceneManager.getScene("Main Scene");
//        ScriptExecutioner.getInstance().initialize(scene);

        float previousTime = (float) GLFW.glfwGetTime();
        float deltaTime;
        while (!GLFW.glfwWindowShouldClose(window.getHandle())) {
            float currentTime = (float) GLFW.glfwGetTime();
            deltaTime = currentTime - previousTime;
            previousTime = currentTime;

            GLFW.glfwPollEvents();
//            ScriptExecutioner.getInstance().update(deltaTime);
            SceneRenderer.getInstance().render(scene);

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
