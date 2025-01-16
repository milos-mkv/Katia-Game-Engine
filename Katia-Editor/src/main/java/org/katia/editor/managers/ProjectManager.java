package org.katia.editor.managers;

import lombok.Getter;
import org.katia.FileSystem;
import org.katia.Logger;

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

    public ProjectManager() {
        Logger.log(Logger.Type.INFO, "Creating project manager ...");
    }

    /**
     * Open project from provided path to project directory.
     * @param path Path to project directory.
     * @throws RuntimeException Throws when project directory is not valid.
     */
    public void openProject(String path) throws RuntimeException {
        if (!FileSystem.doesDirectoryExists(path)) {
            throw new RuntimeException("Directory does not exist! " + path);
        }
    }

    /**
     * Create new project directory.
     * @param path Path to root directory of project.
     * @param name Project name (directory).
     * @throws RuntimeException Throws when failed to create project.
     */
    public void createProject(String path, String name) throws RuntimeException {
        Logger.log(Logger.Type.INFO, "Creating new project:", name, "Path:", path);
        
    }

}
