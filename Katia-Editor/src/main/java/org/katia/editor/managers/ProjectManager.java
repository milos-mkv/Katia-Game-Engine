package org.katia.editor.managers;

import lombok.Data;
import lombok.Getter;
import org.joml.Vector3f;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.CameraComponent;
import org.katia.editor.renderer.EditorCameraController;
import org.katia.editor.ui.windows.ProjectWindow;
import org.katia.editor.EditorUI;
import org.katia.factory.GameFactory;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.katia.game.Configuration;
import org.katia.game.Game;

import java.util.Objects;

import static org.katia.editor.EditorUtils.Assert;

/**
 * This class is responsible for managing current active game project.
 * It should create windowless game that will be used by editor.
 * @see org.katia.game.Game
 */
public abstract class ProjectManager {

    @Getter
    static Game game;

    @Getter
    static String currentScenePath;

    public static boolean isPrefab = false;
    static String oldScenePath;
    /**
     * Open project from provided path to project directory.
     * @param path Path to project directory.
     * @throws RuntimeException Throws when project directory is not valid.
     */
    public static void openProject(String path) throws RuntimeException {
        Logger.log(Logger.Type.INFO, "Open project:", path);

        Assert(!FileSystem.doesDirectoryExists(path), "Directory does not exist! " + path);
        Assert(!validateProjectStructure(path), "Project structure is corrupted!");

        game = GameFactory.createWindowLessGame(path);
        EditorUI.getInstance()
                .getWindow(ProjectWindow.class)
                .getDirectoryExplorerWidget()
                .setRootDirectory(path);
    }

    /**
     * Create new project directory.
     * @param path Path to root directory of project.
     * @param name Project name (directory).
     * @throws RuntimeException Throws when failed to create project.
     */
    public static void createProject(String path, String name) throws RuntimeException {
        Logger.log(Logger.Type.INFO, "Creating new project:", name, "Path:", path);
        Assert(name.isEmpty(), "Name is not valid!");
        Assert(path.isEmpty(), "Path is not valid!");
        Assert(!FileSystem.doesDirectoryExists(path), "Provided path is not valid!");
        Assert(FileSystem.doesDirectoryExists(path + "/" + name), "Directory already exists: " + name);

        Configuration configuration = new Configuration();
        configuration.setWidth(800);
        configuration.setHeight(800);
        configuration.setResizable(false);
        configuration.setTitle("Game");
        configuration.setVSync(false);

        String projectPath = path + "/" + name;
        Assert(!FileSystem.createDirectory(projectPath), "Failed to create project!");

        String[] dirs = { "images", "sounds", "fonts", "prefabs" , "scripts", "scenes" };
        for (String dir : dirs) {
            Assert(!FileSystem.createDirectory(projectPath + "/" + dir), "Failed to create directory : " + dir);
        }
        String confJson = Objects.requireNonNull(Configuration.toJson(configuration));
        Assert(!FileSystem.saveToFile(projectPath + "/katia-conf.json", confJson), "Failed to create katia-conf.json file!");
        Logger.log(Logger.Type.SUCCESS, "Project:", name, "created at:", path);
        openProject(projectPath);
    }

    /**
     * Validate project structure.
     * @param path Project path.
     * @return boolean
     */
    private static boolean validateProjectStructure(String path) {
        Logger.log(Logger.Type.INFO, "Validating project structure:", path);
        Configuration configuration = Configuration.load(path + "/katia-conf.json");
        return configuration != null;
    }

    /**
     * Save current active scene.
     */
    public static void saveCurrentScene() throws RuntimeException {
        Logger.log(Logger.Type.INFO, "Saving current active scene ...");
        Assert(game == null, "There is not active project!");
        Scene scene = game.getSceneManager().getActiveScene();
        Assert(scene == null, "There is no active scene!");
        String json = SceneFactory.generateJsonFromScene(scene);
        Assert(json == null || json.isEmpty(), "Unable to parse scene to valid format!");
        FileSystem.saveToFile(currentScenePath, json);
    }

    /**
     * Set current active scene.
     * @param path Path to the scene file. (This can also use prefab files)
     */
    public static void setCurrentScene(String path) {
        Logger.log(Logger.Type.INFO, "Setting current active scene:", path);
        oldScenePath = currentScenePath;
        currentScenePath = path;
        Scene scene = SceneFactory.generateSceneFromJson(FileSystem.readFromFile(path));
        game.getSceneManager().setActiveScene(scene);

        Vector3f color;
        isPrefab = FileSystem.isPrefabFile(path);
        if (isPrefab) {
            color = new Vector3f(0.16f, 0.18f, 0.2f);
            Logger.log(Logger.Type.INFO, "Current active scene is prefab!");
        } else {
            color = new Vector3f(0.1f, 0.1f, 0.1f);
        }
        EditorCameraController.getInstance()
                .getCamera()
                .getComponent(CameraComponent.class)
                .setBackground(color);
    }

    public static void closePrefab() {
        setCurrentScene(oldScenePath);
    }
}
