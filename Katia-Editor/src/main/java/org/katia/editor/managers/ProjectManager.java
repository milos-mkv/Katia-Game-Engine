package org.katia.editor.managers;

import lombok.Data;
import lombok.Getter;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.editor.Editor;
import org.katia.editor.windows.ProjectWindow;
import org.katia.game.Configuration;

import java.util.Objects;

@Data
/**
 * Project directory structure:
 *  - [folder] assets  - Keep all asset files in this folder (images, sounds, textures, prefabs)
 *  - [folder] scripts - Keep all lua scripts in this folder.
 *  - [folder] scenes  - Keep all scene files in this folder.
 *  - [ file ] katia-conf.json
 */
public class ProjectManager {

    @Getter
    static ProjectManager instance = new ProjectManager();

    String name;
    String path;
    Configuration configuration;
    boolean active;

    /**
     * Project manager constructor.
     */
    public ProjectManager() {
        Logger.log(Logger.Type.INFO, "Creating project manager ...");
        name = null;
        path = null;
        configuration = null;
        active = false;
    }

    /**
     * Open project from provided path to project directory.
     * @param path Path to project directory.
     * @throws RuntimeException Throws when project directory is not valid.
     */
    public void openProject(String path) throws RuntimeException {
        Logger.log(Logger.Type.INFO, "Open project:", path);

        if (!FileSystem.doesDirectoryExists(path)) {
            throw new RuntimeException("Directory does not exist! " + path);
        }
        if (!validateProjectStructure(path)) {
            throw new RuntimeException("Project structure is corrupted!");
        }

        this.active = true;
        Editor.getInstance().getUiRenderer().get(ProjectWindow.class).getDirectoryExplorerWidget().setRootDirectory(path);
    }

    /**
     * Create new project directory.
     * @param path Path to root directory of project.
     * @param name Project name (directory).
     * @param configuration Game configuration.
     * @throws RuntimeException Throws when failed to create project.
     */
    public void createProject(String path, String name, Configuration configuration) throws RuntimeException {
        Logger.log(Logger.Type.INFO, "Creating new project:", name, "Path:", path);
        validateInput("Name is not valid!", name.isEmpty());
        validateInput("Path is not valid!", path.isEmpty());
        validateInput("Provided path is not valid!", !FileSystem.doesDirectoryExists(path));
        validateInput("Directory already exists: " + name, FileSystem.doesDirectoryExists(path + "/" + name));
        validateInput("Configuration is not valid!", !configuration.isValid());

        String projectPath = path + "/" + name;
        if (!FileSystem.createDirectory(projectPath)) {
            throw new RuntimeException("Failed to create project!");
        }

        String[] dirs = { "assets", "scripts", "scenes" };
        for (String dir : dirs) {
            if (!FileSystem.createDirectory(projectPath + "/" + dir)) {
                throw new RuntimeException("Failed to create directory : " + dir);
            }
        }
        if (!FileSystem.saveToFile(projectPath + "/katia-conf.json", Objects.requireNonNull(Configuration.toJson(configuration)))) {
            throw new RuntimeException("Failed to create katia-conf.json file!");
        }
        Logger.log(Logger.Type.SUCCESS, "Project:", name, "created at:", path);
    }

    /**
     * Helper method to validate input and throw an exception with a specific message.
     */
    private void validateInput(String errorMessage, boolean condition) {
        if (condition) {
            throw new RuntimeException(errorMessage);
        }
    }

    private boolean validateProjectStructure(String path) {
        Logger.log(Logger.Type.INFO, "Validating project structure:", path);
        Configuration configuration = Configuration.load(path + "/katia-conf.json");
        if (configuration == null) {
            return false;
        }
        String[] dirs = { "assets", "scripts", "scenes" };
        for (String dir : dirs) {
            if (!FileSystem.doesDirectoryExists(path + "/" + dir)) {
                return false;
            }
        }
        return true;
    }
}
