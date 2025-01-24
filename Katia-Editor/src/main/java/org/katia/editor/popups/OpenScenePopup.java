package org.katia.editor.popups;

import imgui.ImGui;
import imgui.ImVec2;
import lombok.Getter;
import org.katia.FileSystem;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.editor.managers.EditorSceneManager;
import org.katia.editor.managers.ProjectManager;

import java.nio.file.Path;
import java.util.List;

public class OpenScenePopup extends Popup {

    @Getter
    static OpenScenePopup instance = new OpenScenePopup();

    public OpenScenePopup() {
        super("Open Scene", "OPEN SCENE", 500, 200);
    }

    @Override
    public void body() {
        ImGui.beginChild("##Child", -1, -1);
        var pm = ProjectManager.getInstance();
        if (!pm.isActive()) {
            String text = "There is no opened project!";
            ImVec2 size = ImGui.calcTextSize(text);
            ImGui.setCursorPos(500 / 2 - size.x /2, 400 / 2 - size.y - 20);
            ImGui.textDisabled(text);
        } else {
            ImGui.separator();
            List<Path> items = FileSystem.readDirectoryData(pm.getPath() + "/scenes");
            for (Path path : items) {
                ImGui.columns(2);
                ImGui.setColumnWidth(-1, 390);
                ImGui.text(" " + Icons.FileVideo + " " + path.getFileName().toString());
                ImGui.nextColumn();
                if (ImGui.button(" OPEN ##"+path.toString())  ) {
                    try {
                        EditorSceneManager.getInstance().openScene(path.toAbsolutePath().toString());
                        ImGui.closeCurrentPopup();
                    } catch (RuntimeException e) {
                        Logger.log(Logger.Type.ERROR, e.toString());
                        ErrorPopup.open(e.getMessage());
                    }
                }
                ImGui.columns(1);

                ImGui.separator();
            }
            ErrorPopup.render();

        }
        ImGui.endChild();
    }
}
