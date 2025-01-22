package org.katia.editor.project;

import lombok.Getter;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.factory.TextureFactory;
import org.katia.gfx.Texture;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ProjectAssetManager {

    @Getter
    static ProjectAssetManager instance = new ProjectAssetManager();

    String path;
    Map<String, Texture> images;

    public ProjectAssetManager() {
        Logger.log(Logger.Type.INFO, "Creating project asset manager ...");
    }

    /**
     * Load all assets from provided project.
     * @param path Root path of project.
     */
    public void loadProject(String path) {
        Logger.log(Logger.Type.INFO, "Loading project:", path);
        this.path = path;

        this.images = new HashMap<>();
        loadImages(Paths.get(path, "images").toString());
    }

    /**
     * Load images from provided path.
     * @param path Path.
     */
    public void loadImages(String path) {
        Logger.log(Logger.Type.INFO, "Loading images from directory:", path);
        FileSystem.readDirectoryData(path).stream().filter((entry) -> {
            if (Files.isDirectory(entry)) {
                return true;
            } else if (FileSystem.isImageFile(entry.toString())) {
                images.put(entry.toString(), TextureFactory.createTexture(entry.toString()));
            }
            return false;
        }).toList().forEach((dir) -> loadImages(dir.toString()));
    }

    /**
     * Get image.
     * @param path Image path.
     * @return Texture
     */
    public Texture getImage(String path) {
        Texture image = images.get(path);
        if (image == null) {
            image = TextureFactory.createTexture(path);
            images.put(path, image);
        }
        return image;
    }

}
