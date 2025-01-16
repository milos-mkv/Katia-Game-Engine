package org.katia.editor.managers;

import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGuiIO;
import imgui.internal.ImGui;
import lombok.Data;
import lombok.Getter;
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
//        loadImages();
    }

    private void loadFonts() {
        fonts = new HashMap<>();

        ImGuiIO io = ImGui.getIO();

        ImFontConfig config = new ImFontConfig();
        config.setOversampleH(4);
        config.setOversampleV(4);
        config.setPixelSnapH(false);
        config.setMergeMode(false);

        fonts.put("Default", io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/JetBrainsMonoNL-ExtraBold.ttf", 25.0f, config));
        io.setFontDefault(fonts.get("Default"));
        config.setMergeMode(true);
        short[] iconRanges = { (short) 0xF000, (short) 0xF8FF, (short) 0xE000, (short) 0xE8FF, 0};  // Update according to the FontAwesome version
        io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/fa-solid-900.ttf", 25.0f, config, iconRanges);
        io.getFonts().build();


        fonts.put("Default15", io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/Roboto-Regular.ttf", 25.0f));
        fonts.put("Default40", io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/Roboto-ExtraBold.ttf", 40.0f));
        io.getFonts().addFontFromFileTTF("./Katia-Editor/src/main/resources/fonts/fa-solid-900.ttf", 40.0f, config, iconRanges);
        io.getFonts().build();
    }
}
