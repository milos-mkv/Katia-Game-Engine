package org.katia.managers;

import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.factory.FontFactory;
import org.katia.factory.TextureFactory;
import org.katia.game.Game;
import org.katia.gfx.Font;
import org.katia.gfx.Texture;

import java.nio.file.Files;
import java.util.HashMap;

/**
 * Asset manager class is responsible for loading all game assets featuring:
 *  - Texture/Images
 *  - Sounds
 *  - Prefabs
 *  - Fonts
 */
public class AssetManager {

    Game game;
    HashMap<String, Texture> textures;
    HashMap<String, Font> fonts;
    String assetPath;

    /**
     * Load all assets from game directory.
     * @param game Game instance.
     * @param assetsDirectory Assets directory.
     */
    public AssetManager(Game game, String assetsDirectory) {
        Logger.log(Logger.Type.INFO, "Creating asset manager:", assetsDirectory);
        this.game = game;
        this.assetPath = assetsDirectory;
        this.textures = new HashMap<>();
        this.fonts = new HashMap<>();

        loadTextures(this.assetPath);
        loadFonts(this.assetPath);
    }

    /**
     * Load all textures (images) from provided directory.
     * @param path Path.
     */
    private void loadTextures(String path) {
        Logger.log(Logger.Type.INFO, "Loading textures from:", path);
        FileSystem.readDirectoryData(path).stream().filter((entry) -> {
            if (Files.isDirectory(entry)) {
                return true;
            } else if (FileSystem.isImageFile(entry.toString())) {
                textures.put(entry.toString(), TextureFactory.createTexture(entry.toString()));
            }
            return false;
        }).toList().forEach((dir) -> loadTextures(dir.toString()));
    }

    /**
     * Load all fonts from provided directory.
     * @param path Path to directory.
     */
    private void loadFonts(String path) {
        Logger.log(Logger.Type.INFO, "Loading fonts from:", path);
        FileSystem.readDirectoryData(path).stream().filter((entry) -> {
            if (Files.isDirectory(entry)) {
                return true;
            } else if (FileSystem.isFontFile(entry.toString())) {
                fonts.put(entry.toString(), FontFactory.createFont(entry.toString(), 72, 512, 512));
            }
            return false;
        }).toList().forEach((dir) -> loadFonts(dir.toString()));
    }
}
