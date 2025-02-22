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
    HashMap<String, Scene> scenes;
    String path;

    @Getter
    Scene activeScene;

    /**
     * Scene manager constructor.
     */
    public SceneManager(Game game, String path) {
        Logger.log(Logger.Type.INFO, "Creating scene manager ...");

        this.path = path;
        this.game = game;
        scenes = new HashMap<>();
        activeScene = null;

        loadScenes(path);
    }

    /**
     * Get scene by name.
     * @param name Scene name.
     * @return Scene
     */
    public Scene getScene(String name) {
        return scenes.get(name);
    }

    /**
     * Set current active scene by its name.
     * @param name Scene name.
     */
    public void setActiveScene(String name) {
        Logger.log(Logger.Type.INFO, "Setting current active scene:", name);
        activeScene = scenes.get(name);
    }

    /**
     * Load all scenes from scenes directory.
     * @param path Path to scenes directory.
     */
    private void loadScenes(String path) {
        Logger.log(Logger.Type.INFO, "Scene manager loading scenes from scenes directory:", path);

        FileSystem.readDirectoryData(path).stream().filter((entry) -> {
            if (Files.isDirectory(entry)) {
                return true;
            } else if  (FileSystem.isSceneFile(entry.toString())) {
                var scene = SceneFactory.generateSceneFromJson(FileSystem.readFromFile(entry.toString()));
                if (scene != null) {
                    scenes.put(scene.getName(), scene);
                }
                if (activeScene == null) {
                    activeScene = scene;
                }
            }
            return false;
        }).toList().forEach((dir) -> loadScenes(dir.toString()));
    }
}