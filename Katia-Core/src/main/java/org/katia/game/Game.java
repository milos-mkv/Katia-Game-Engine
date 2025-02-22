package org.katia.game;

import lombok.Data;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.TextComponent;
import org.katia.factory.FontFactory;
import org.katia.scripting.LuaScriptExecutioner;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.katia.gfx.SceneRenderer;
import org.katia.managers.AssetManager;
import org.katia.managers.InputManager;
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

        SceneFactory.initialize();
        GameObjectFactory.initialize();
        FontFactory.initialize();

        assetManager = new AssetManager(this, directory);
        sceneManager = new SceneManager(this, directory + "/scenes");
        scriptManager = new ScriptManager(this, directory + "/scripts");
        inputManager = new InputManager(this);
        scriptExecutioner = new LuaScriptExecutioner(this);
    }

    /**
     * Run game.
     * @return Game
     */
    public void run() {
        Logger.log(Logger.Type.INFO, "Run game instance ...");

        Scene scene = sceneManager.getActiveScene();
        scriptExecutioner.init(scene);
        int fps = 0;
        float tim = 0;
        float previousTime = (float) GLFW.glfwGetTime();
        GLFW.glfwSwapInterval(0);

//        GameObject g = scene.find("Test");
//        TextComponent textComponent = new TextComponent();
//        textComponent.setText("HELLO WORLD!");
//        textComponent.setFont(FontFactory.createFont("./assets/RandyGGBold.ttf", 72, 512, 512));
//        g.addComponent(textComponent);

        System.out.println(SceneFactory.generateJsonFromScene(scene));

        while (!GLFW.glfwWindowShouldClose(window.getHandle())) {
            previousTime = calculateDeltaTime(previousTime);
            tim += deltaTime;
            fps++;
            if (tim >= 1) {
                System.out.println(fps);

                tim = 0;
                fps = 0;
            }
            GLFW.glfwPollEvents();
//            scriptExecutioner.update(deltaTime);
            SceneRenderer.getInstance().render(scene);

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
        window.dispose();
    }
}
