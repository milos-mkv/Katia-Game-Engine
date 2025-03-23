package org.katia.managers;

import lombok.Data;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.factory.AudioFactory;
import org.katia.factory.FontFactory;
import org.katia.factory.TextureFactory;
import org.katia.game.Game;
import org.katia.gfx.resources.Audio;
import org.katia.gfx.resources.Font;
import org.katia.gfx.resources.Texture;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * This class is responsible for loading all game resources.
 */
@Data
public class ResourceManager {

    private final Game game;
    HashMap<String, Texture> textures;
    HashMap<String, Font> fonts;
    HashMap<String, String> scripts;
    HashMap<String, String> scenes;
    HashMap<String, Audio> audios;

    /**
     * Resource manager constructor.
     * @param game Game instance.
     */
    public ResourceManager(Game game) {
        Logger.log(Logger.Type.INFO, "Creating resource manager for game:", game.getDirectory());
        this.game = game;

        textures = new HashMap<>();
        fonts = new HashMap<>();
        scripts = new HashMap<>();
        scenes = new HashMap<>();
        audios = new HashMap<>();

        loadResources(game.getDirectory());
    }

    /**
     * Get texture.
     * @param key Texture key.
     * @return Texture
     */
    public Texture getTexture(String key) {
        Texture texture = textures.get(key);
        if (texture == null) {
            String path = game.getDirectory() + "/" + key;
            texture = TextureFactory.createTexture(path);
            textures.put(key, texture);
        }
        return texture;
    }

    /**
     * Get lua script.
     * @param key Script key.
     * @return String
     */
    public String getScript(String key) {
        String script = scripts.get(key);
        if (script == null) {
            String path = game.getDirectory() + "/" + key;
            File file = new File(path);
            if (file.exists()) {
                script = path;
                scripts.put(key, path);
            }
        }
        return script;
    }

    /**
     * Get font.
     * @param key Font key.
     * @return Font
     */
    public Font getFont(String key) {
        Font font = fonts.get(key);
        if (font == null) {
            String path = game.getDirectory() + "/" + key;
            font = FontFactory.createFont(path);
            fonts.put(key, font);
        }
        return font;
    }

    /**
     * Get game scene if it was loaded.
     * @param key Scene name.
     * @return String
     */
    public String getScene(String key) {
        String sceneJson = scenes.get(key);
        return FileSystem.readFromFile(sceneJson);
    }

    /**
     * Load all game resources.
     * @param path Path from which to load resources.
     */
    private void loadResources(String path) {
        Logger.log(Logger.Type.INFO, "Loading resources from:", path);
        FileSystem.readDirectoryData(path).stream().filter((entry) -> {
            String key = FileSystem.relativize(game.getDirectory(), entry.toString());
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
                       entry.toString());
            } else if (FileSystem.isSoundFile(entry.toString())) {
                audios.put(key, AudioFactory.createAudio(entry.toString()));
            }
            return false;
        }).toList().forEach((dir) -> loadResources(dir.toString()));
    }
}