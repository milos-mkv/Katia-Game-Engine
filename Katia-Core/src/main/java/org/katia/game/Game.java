package org.katia.game;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.core.Scene;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.katia.gfx.Renderer;
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

        Scene scene = SceneFactory.createScene("Main Scene", configuration.width, configuration.height);
        Renderer renderer = new Renderer();

        var go = GameObjectFactory.createGameObject("Test");
        SpriteComponent sp = new SpriteComponent();
        sp.setTexture("C:\\Users\\milos\\OneDrive\\Pictures\\Screenshots\\Screenshot 2024-03-10 192344.png");
        go.addComponent(sp);
        go.getComponent(TransformComponent.class).setScale(new Vector3f(400, 400, 1));
scene.addGameObject(go);
        scene.find("Main Camera").getComponent(TransformComponent.class).setScale(new Vector3f(1, 1, 1));

        while (!GLFW.glfwWindowShouldClose(window.getHandle())) {
            GLFW.glfwPollEvents();

            renderer.render(scene,window.getWidth(), window.getHeight());

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
