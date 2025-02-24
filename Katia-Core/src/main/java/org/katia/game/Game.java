package org.katia.game;

import lombok.Data;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.factory.GameObjectFactory;
import org.katia.managers.ResourceManager;
import org.katia.scripting.LuaScriptExecutioner;
import org.katia.factory.SceneFactory;
import org.katia.gfx.SceneRenderer;
import org.katia.managers.InputManager;
import org.katia.managers.SceneManager;
import org.lwjgl.glfw.GLFW;

@Data
public class Game {

    Configuration configuration;
    Window window;
    SceneManager sceneManager;
    ResourceManager resourceManager;
    InputManager inputManager;
    LuaScriptExecutioner scriptExecutioner;
    String directory;
    float deltaTime = 0;

    /**
     * Crete new game instance.
     * @param directory Game root directory.
     */
    public Game(String directory) {
        Logger.log(Logger.Type.INFO, "Creating new game instance:", directory);
        this.directory = directory;
        configuration = Configuration.load(directory + "/katia-conf.json");
        window = new Window(this, configuration.title, configuration.width, configuration.height);

        resourceManager = new ResourceManager(directory);
        sceneManager = new SceneManager(this);
        inputManager = new InputManager(this);
        scriptExecutioner = new LuaScriptExecutioner(this);
        Global.resourceManager = resourceManager;

        sceneManager.setActiveScene("MainScene");
    }

    /**
     * Run game.
     * @return Game
     */
    public void run() {
        Logger.log(Logger.Type.INFO, "Run game instance ...");

        Scene scene = sceneManager.getActiveScene();
        scriptExecutioner.init(scene);

//        scene.dispose();
//        scene.setRootGameObject(null);
//        System.gc();

        float previousTime = (float) GLFW.glfwGetTime();
        GLFW.glfwSwapInterval(0);

        while (!GLFW.glfwWindowShouldClose(window.getHandle())) {
            previousTime = calculateDeltaTime(previousTime);

            GLFW.glfwPollEvents();
            scriptExecutioner.update(deltaTime);

            GameObject camera = scene.find("Main Camera");

            SceneRenderer.getInstance().render(scene, camera, false);

            GLFW.glfwSwapBuffers(window.getHandle());
        }
    }

    public void execute() {

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
        window.dispose();
    }
}
