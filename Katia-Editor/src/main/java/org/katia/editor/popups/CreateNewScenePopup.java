package org.katia.editor.popups;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import lombok.Getter;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.core.Scene;
import org.katia.editor.managers.ProjectManager;
import org.katia.factory.SceneFactory;

import java.nio.file.Paths;


public class CreateNewScenePopup extends Popup {

    @Getter
    static CreateNewScenePopup instance = new CreateNewScenePopup();

    ImString sceneName;

    public CreateNewScenePopup() {
        super("Create Scene", "CREATE SCENE", 500, 130);
    }

    @Override
    public void open() {
        super.open();
        sceneName = new ImString();
    }

    @Override
    public void body() {
        ImGui.beginChild("##Child", -1, -45, true);

        ImGui.columns(2);
        ImGui.setColumnWidth(-1, 80);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 2);
        ImGui.text(" Name");
        ImGui.nextColumn();
        ImGui.setNextItemWidth(-1);

        ImGui.inputText("##Scene name", sceneName, ImGuiInputTextFlags.CharsNoBlank);
        ImGui.columns(1);
        ImGui.endChild();
        ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 50);
        if (ImGui.button(" CREATE ")) {
            createButtonCallback();
        }

        ErrorPopup.render();
    }

    /**
     * Create scene button click callback.
     */
    private void createButtonCallback() {
        var pm = ProjectManager.getInstance();
        if (!pm.isActive()) {
            ErrorPopup.open("There is no active project!");
        } else if (sceneName.toString().isEmpty()) {
            ErrorPopup.open("Scene name must not be empty!");
        } else {
            var conf = pm.getConfiguration();
            Scene scene = SceneFactory.createScene(sceneName.toString(), conf.getWidth(), conf.getHeight());
            String json = SceneFactory.generateJsonFromScene(scene);
            FileSystem.saveToFile(Paths.get(pm.getPath(), "scenes", sceneName + ".scene").toString(), json);
        }
    }
}
