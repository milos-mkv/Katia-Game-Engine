package org.katia.managers;

import lombok.Getter;
import lombok.Setter;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.factory.SceneFactory;
import org.katia.game.Game;

import java.util.Objects;

/**
 * This class is responsible for managing current active game scene and current camera in use.
 * This class has to be exposed to Lua so that user can change active scene or camera.
 * @see org.katia.scripting.LuaScriptExecutioner
 */
public class SceneManager {

    private final Game game;

    @Getter
    private Scene activeScene;

    @Setter
    @Getter
    private GameObject camera;

    /**
     * Scene manager constructor.
     * @param game Game instance.
     */
    public SceneManager(Game game) {
        Logger.log(Logger.Type.INFO, "Create scene manager for game:", game.getDirectory());
        this.game = game;
    }

    /**
     * Set current active scene by its name. Scene must be loaded in resource manager.
     * @param name Scene name.
     * @see ResourceManager
     */
    public void setActiveScene(String name) {
        Logger.log(Logger.Type.INFO, "Setting current active scene:", name);
        ResourceManager resourceManager = game.getResourceManager();
        activeScene = SceneFactory.generateSceneFromJson(resourceManager.getScene(name));

        // Instant find main camera and set it for use.
        // This will be overwritten in Editor were we are going to set editor camera for use.
        camera = Objects.requireNonNull(activeScene).find("Main Camera");
    }
}