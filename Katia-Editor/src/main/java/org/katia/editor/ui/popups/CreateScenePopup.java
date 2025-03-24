package org.katia.editor.ui.popups;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.core.Scene;
import org.katia.editor.managers.ProjectManager;
import org.katia.factory.SceneFactory;

import java.nio.file.Paths;

/**
 * This class represents create scene popup.
 * @see org.katia.editor.ui.popups.Popup
 */
public class CreateScenePopup extends Popup {

    ImString sceneName;

    /**
     * Create Scene Popup.
     */
    public CreateScenePopup() {
        super("CREATE SCENE", 500, 130);
        Logger.log(Logger.Type.INFO, "Create Scene Popup Constructor");
    }

    @Override
    public void open(Object data) {
        super.open(data);
        this.sceneName = new ImString();
    }

    @Override
    public void body() {
        ImGui.columns(2);
        ImGui.setColumnWidth(-1, 80);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 2);
        ImGui.text(" Name");
        ImGui.nextColumn();
        ImGui.setNextItemWidth(-1);

        ImGui.inputText("##Scene name", sceneName, ImGuiInputTextFlags.CharsNoBlank);
        ImGui.columns(1);
        ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 50);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.6f, 0.8f, 0.8f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.4f, 0.4f, 1.0f);
        if (ImGui.button(" CREATE ")) {
            createButtonCallback();
        }
        ImGui.popStyleColor(2);
        ErrorPopup.render();
    }

    /**
     * Create scene button click callback.
     */
    private void createButtonCallback() {
        if (ProjectManager.getGame() == null) {
            ErrorPopup.open("There is no active project!");
        } else if (sceneName.toString().isEmpty()) {
            ErrorPopup.open("Scene name must not be empty!");
        } else {
            var conf = ProjectManager.getGame().getConfiguration();
            Scene scene = SceneFactory.createScene(sceneName.toString(), conf.getWidth(), conf.getHeight());
            String json = SceneFactory.generateJsonFromScene(scene);
            String path = Paths.get(ProjectManager.getGame().getDirectory(), "scenes", sceneName + ".scene").toString();
            FileSystem.saveToFile(path, json);

            ProjectManager.getGame()
                    .getResourceManager()
                    .getScenes()
                    .put(sceneName.toString(), path);
            ProjectManager.getGame().getSceneManager().setActiveScene(sceneName.toString());
            ImGui.closeCurrentPopup();
        }
    }
}
