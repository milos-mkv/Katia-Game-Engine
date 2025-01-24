package org.katia.editor.managers;

import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGuiIO;
import imgui.internal.ImGui;
import lombok.Data;
import lombok.Getter;
import org.katia.factory.FontFactory;
import org.katia.factory.SceneFactory;
import org.katia.factory.TextureFactory;
import org.katia.gfx.Texture;

import java.util.HashMap;
import java.util.Map;

@Data
public class EditorAssetManager {

    @Getter
     static final EditorAssetManager instance = new EditorAssetManager();

     Map<String, ImFont> fonts;
     Map<String, Texture> images;

    public EditorAssetManager() {
        loadFonts();
        loadImages();
    }

    private void loadFonts() {
        fonts = new HashMap<>();

        ImGuiIO io = ImGui.getIO();

        ImFontConfig config = new ImFontConfig();
        config.setOversampleH(2);
        config.setOversampleV(2);
        config.setPixelSnapH(false);
        config.setMergeMode(false);

        fonts.put("Default", io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/JetBrainsMonoNL-ExtraBold.ttf", 25.0f, config));
        io.setFontDefault(fonts.get("Default"));
        config.setMergeMode(true);
        short[] iconRanges = { (short) 0xF000, (short) 0xF8FF, (short) 0xE000, (short) 0xE8FF, 0};  // Update according to the FontAwesome version
        io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/fa-solid-900.ttf", 25.0f, config, iconRanges);
        io.getFonts().build();

        fonts.put("Default25", io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/JetBrainsMono-Medium.ttf", 25.0f));
        fonts.put("Default15", io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/Roboto-Regular.ttf", 25.0f));
        fonts.put("Text15", io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/Roboto-Regular.ttf", 15));

        fonts.put("Default40", io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/Roboto-ExtraBold.ttf", 40.0f));
        io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/fa-solid-900.ttf", 40.0f, config, iconRanges);
        io.getFonts().build();

        FontFactory.initialize();
        SceneFactory.initialize();
    }

    private void loadImages() {
        images = new HashMap<>();
        images.put("LuaFileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/lua.png"));
        images.put("FolderIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/computer-folder.png"));
        images.put("FileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/file.png"));
        images.put("JsonFileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/json.png"));
        images.put("UnknownFileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/unknown.png"));
        images.put("BinaryFileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/binary.png"));
        images.put("SceneFileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/scene.png"));
        images.put("ImageFileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/image.png"));
        images.put("SoundFileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/sound.png"));
        images.put("PrefabFileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/prefab.png"));
        images.put("FontFileIcon", TextureFactory.createTexture("./Katia-Editor/src/main/resources/images/font.png"));

    }
}
