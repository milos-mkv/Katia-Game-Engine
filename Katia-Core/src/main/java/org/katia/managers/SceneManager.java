package org.katia.managers;

import lombok.Getter;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.core.Scene;
import org.katia.factory.SceneFactory;
import org.katia.game.Game;

import java.nio.file.Files;
import java.util.HashMap;

public class SceneManager {

    Game game;
    @Getter
    Scene activeScene;

    /**
     * Scene manager constructor.
     */
    public SceneManager(Game game) {
        Logger.log(Logger.Type.INFO, "Creating scene manager ...");
        this.game = game;
        this.activeScene = null;
    }

    /**
     * Set current active scene by its name.
     * @param name Scene name.
     */
    public void setActiveScene(String name) {
        Logger.log(Logger.Type.INFO, "Setting current active scene:", name);
        ResourceManager resourceManager = game.getResourceManager();
        activeScene = SceneFactory.generateSceneFromJson(resourceManager.getScenes().get(name));
    }
}