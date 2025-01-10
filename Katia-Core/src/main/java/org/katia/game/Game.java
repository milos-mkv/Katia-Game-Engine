package org.katia.game;

import lombok.Data;
import lombok.Getter;
import lombok.extern.java.Log;
import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.ScriptExecutioner;
import org.katia.core.components.ScriptComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.katia.gfx.SceneRenderer;
import org.katia.managers.SceneManager;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.lwjgl.glfw.GLFW;

@Data
public class Game {

    @Getter
    static Game instance = new Game();

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
    void test(ScriptComponent scriptComponent, GameObject a2, Scene scene) {
        var initMethod = scriptComponent.getBehaviourTable().get("init");
        LuaTable params = new LuaTable();
        params.set("gameObject", CoerceJavaToLua.coerce(a2));
        params.set("scene", CoerceJavaToLua.coerce(scene));
        initMethod.call(scriptComponent.getBehaviourTable(), params);
    }
    /**
     * Run game.
     * @return Game
     */
    public Game run() {
        Logger.log(Logger.Type.INFO, "Run game instance!");

        Scene scene = SceneFactory.createScene("Main Scene", configuration.width, configuration.height);
        SceneFactory.generateJsonFromScene(scene);
        var go = GameObjectFactory.createGameObject("Test");
        SpriteComponent sp = new SpriteComponent();
        sp.setTexture("C:\\Users\\milos\\OneDrive\\Pictures\\Screenshots\\Screenshot 2024-03-10 192344.png");
        go.addComponent(sp);
        go.getComponent(TransformComponent.class).setScale(new Vector3f(400, 400, 1));
        go.getComponent(TransformComponent.class).setPosition(new Vector3f(100, 100, 1));
        scene.addGameObject(go);
        scene.find("Main Camera").getComponent(TransformComponent.class).setScale(new Vector3f(1, 1, 1));


        ScriptComponent scriptComponent = new ScriptComponent();
        scriptComponent.addScriptFile(scene, "C:\\Users\\milos\\Documents\\Katia-Game-Engine\\Katia-Core\\src\\main\\resources\\scripts\\Behaviour.lua");
        go.addComponent(scriptComponent);

        go = null;
        SceneManager.getInstance().loadScenesDirectory("C:\\Users\\milos\\OneDrive\\Desktop\\scenes");
        ScriptExecutioner.getInstance().initialize(scene);

        float previousTime = (float) GLFW.glfwGetTime();
        float deltaTime;
        while (!GLFW.glfwWindowShouldClose(window.getHandle())) {
            float currentTime = (float) GLFW.glfwGetTime();
            deltaTime = currentTime - previousTime;
            previousTime = currentTime;

            GLFW.glfwPollEvents();
            ScriptExecutioner.getInstance().update(deltaTime);
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
