package org.katia.managers;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.core.Scene;
import org.katia.factory.FontFactory;
import org.katia.factory.SceneFactory;
import org.katia.factory.TextureFactory;
import org.katia.game.Game;
import org.katia.gfx.Font;
import org.katia.gfx.Texture;

import java.nio.file.Files;
import java.util.HashMap;

@Data
@NoArgsConstructor
public class ResourceManager {

    String directory;
    HashMap<String, Texture> textures;
    HashMap<String, Font> fonts;
    HashMap<String, String> scripts;
    HashMap<String, String> scenes;

    public ResourceManager(String directory) {
        this.directory = directory;

        textures = new HashMap<>();
        fonts = new HashMap<>();
        scripts = new HashMap<>();
        scenes = new HashMap<>();

        loadResources(directory);
    }

    /**
     * Get texture.
     * @param key Texture key.
     * @return Texture
     */
    public Texture getTexture(String key) {
        Texture texture = textures.get(key);
        if (texture == null) {
            String path = directory + "/" + key;
            texture = TextureFactory.createTexture(path);
            textures.put(key, texture);
        }
        return texture;
    }

    /**
     * Load all game resources.
     * @param path Path from which to load resources.
     */
    private void loadResources(String path) {
        Logger.log(Logger.Type.INFO, "Loading resources from:", path);
        FileSystem.readDirectoryData(path).stream().filter((entry) -> {
            String key = FileSystem.relativize(directory, entry.toString());
            if (Files.isDirectory(entry)) {
                return true;
            } else if (FileSystem.isImageFile(entry.toString())) {
                textures.put(key, TextureFactory.createTexture(entry.toString()));
            } else if (FileSystem.isFontFile(entry.toString())) {
                fonts.put(key, FontFactory.createFont(entry.toString()));
            } else if (FileSystem.isLuaFile(entry.toString())) {
                scripts.put(key, entry.toString());
            } else if (FileSystem.isSceneFile(entry.toString())) {
                scenes.put(FileSystem.getFilenameWithoutExtension(FileSystem.getFileName(entry.toString())),
                        FileSystem.readFromFile(entry.toString()));
            }
            return false;
        }).toList().forEach((dir) -> loadResources(dir.toString()));
    }

}
