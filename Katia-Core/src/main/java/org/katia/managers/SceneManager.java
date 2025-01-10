package org.katia.managers;

import lombok.Getter;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.core.Scene;
import org.katia.factory.SceneFactory;

import java.util.HashMap;
import java.util.List;

public class SceneManager {

    @Getter
    static SceneManager instance = new SceneManager();

    HashMap<String, Scene> scenes;
    @Getter
    Scene activeScene;

    /**
     * Scene manager constructor.
     */
    public SceneManager() {
        Logger.log(Logger.Type.INFO, "Creating scene manager!");
        scenes = new HashMap<>();
        activeScene = null;
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
    public void loadScenesDirectory(String path) {
        Logger.log(Logger.Type.INFO, "Scene manager loading scenes from scenes directory:", path);
        List<Scene> scenes = FileSystem.readDirectoryData(path)
                .stream()
                .filter((filePath) -> FileSystem.isJsonFile(filePath.toString()))
                .map((filePath) -> SceneFactory.generateSceneFromJson(FileSystem.readFromFile(filePath.toString())))
                .toList();
        for (Scene scene : scenes) {
            this.scenes.put(scene.getName(), scene);
        }
    }
}