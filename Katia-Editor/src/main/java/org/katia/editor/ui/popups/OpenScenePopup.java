package org.katia.editor.ui.popups;

import imgui.ImGui;
import imgui.ImVec2;
import org.katia.FileSystem;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.editor.managers.ProjectManager;

import java.nio.file.Path;
import java.util.List;

/**
 * This class represents open scene popup.
 * @see org.katia.editor.ui.popups.Popup
 */
public class OpenScenePopup extends Popup {

    /**
     * Open Scene Popup Constructor.
     */
    public OpenScenePopup() {
        super("OPEN SCENE", 500, 200);
    }

    /**
     * Render body for open scene popup.
     */
    @Override
    public void body() {
        ImGui.beginChild("##Child", -1, -1, true);
        if (ProjectManager.getGame() == null) {
            renderNoProjectOpen();
        } else {
            renderScenesList();
        }
        ImGui.endChild();
    }

    private void renderNoProjectOpen() {
        String text = "There is no opened project!";
        ImVec2 size = ImGui.calcTextSize(text);
        ImGui.setCursorPos((float) 500 / 2 - size.x /2, (float) 400 / 2 - size.y - 20);
        ImGui.textDisabled(text);
    }

    private void renderScenesList() {
        List<Path> items = FileSystem.readDirectoryData(ProjectManager.getGame().getDirectory() + "/scenes");
        for (Path path : items) {
            ImGui.columns(2);
            ImGui.setColumnWidth(-1, 390);
            float y = ImGui.getCursorPosY();
            ImGui.setCursorPosY(y + 4);
            ImGui.text(" " + Icons.FileVideo + " ");
            ImGui.sameLine();
            ImGui.setCursorPosY(y);
            ImGui.text(path.getFileName().toString());
            ImGui.nextColumn();
            if (ImGui.button(" OPEN ##" + path)) {
                try {
                    ProjectManager.getGame()
                            .getSceneManager()
                            .setActiveScene(path.getFileName().toString());
                    ImGui.closeCurrentPopup();
                } catch (RuntimeException e) {
                    Logger.log(Logger.Type.ERROR, e.toString());
//                    ErrorPopup.open(e.getMessage());
                }
            }
            ImGui.columns(1);
            ImGui.separator();
        }
    }
}
