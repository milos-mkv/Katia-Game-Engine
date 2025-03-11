package org.katia.editor.managers;

import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGuiIO;
import imgui.internal.ImGui;
import lombok.Data;
import lombok.Getter;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.factory.TextureFactory;
import org.katia.gfx.resources.Texture;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class hold all assets used in game editor.
 */
@Data
public class EditorAssetManager {

    @Getter
    static final EditorAssetManager instance = new EditorAssetManager();

    Map<String, ImFont> fonts;
    Map<String, Texture> images;

    public EditorAssetManager() {
        Logger.log(Logger.Type.INFO, "Editor Asset Manager Constructor");
        loadFonts();
        loadImages();
    }

    /**
     * Load all fonts from resources directory.
     */
    private void loadFonts()  {
        fonts = new HashMap<>();

        ImGuiIO io = ImGui.getIO();

        ImFontConfig config = new ImFontConfig();
        config.setOversampleH(2);
        config.setOversampleV(2);
        config.setPixelSnapH(false);

        String path = null;
        try {
            path = Objects.requireNonNull(getClass().getResource("/fonts")).toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        fonts.put("Default", io.getFonts().addFontFromFileTTF(path + "/JetBrainsMonoNL-ExtraBold.ttf", 25.0f, config));
        config.setMergeMode(true);
        // Update according to the FontAwesome version
        short[] iconRanges = { (short) 0xF000, (short) 0xF8FF, (short) 0xE000, (short) 0xE8FF, 0 };
        io.getFonts().addFontFromFileTTF(path + "/fa-solid-900.ttf", 25.0f, config, iconRanges);
        config.setMergeMode(false);
        fonts.put("Text15", io.getFonts().addFontFromFileTTF(path + "/Roboto-Regular.ttf", 15));
        fonts.put("Text20", io.getFonts().addFontFromFileTTF(path + "/Roboto-Regular.ttf", 20));
        fonts.put("Default25", io.getFonts().addFontFromFileTTF(path + "/JetBrainsMono-Medium.ttf", 25.0f));
        config.setMergeMode(true);
        io.getFonts().addFontFromFileTTF(path + "/fa-solid-900.ttf", 25.0f, config, iconRanges);
        io.setFontDefault(fonts.get("Default25"));

        io.getFonts().build();
    }

    /**
     * Load all images from resources directory.
     */
    private void loadImages() {
        try {
            images = new HashMap<>();
            // Load all image files from resources/images
            Files.list(Paths.get(Objects.requireNonNull(getClass().getResource("/images")).toURI()))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String key = FileSystem.getFilenameWithoutExtension(path.getFileName().toString());
                        images.put(key, TextureFactory.createTexture(path.toString()));
                    });
        } catch (IOException | URISyntaxException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
    }

    /**
     * Get font.
     * @param name Font name.
     * @return ImFont
     */
    public ImFont getFont(String name) {
        return fonts.get(name);
    }

    /**
     * Get image.
     * @param path Path to image.
     * @return Texture
     */
    public Texture getImage(String path) {
        Texture texture = images.get(path);
        if (texture == null) {
            texture = TextureFactory.createTexture(path);
            images.put(path, texture);
        }
        return texture;
    }

    /**
     * Dispose of all editor assets.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Editor Asset Manager Dispose");
        images.forEach((_, image) -> TextureFactory.dispose(image));
        fonts.forEach((key, font) -> {
            Logger.log(Logger.Type.DISPOSE, "Disposing of font:", key);
            font.destroy();
        });
    }
}
