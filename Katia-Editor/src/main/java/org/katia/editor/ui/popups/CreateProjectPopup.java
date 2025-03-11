package org.katia.editor.ui.popups;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.editor.EditorUtils;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;

/**
 * This class represents create project popup.
 * @see org.katia.editor.ui.popups.Popup
 */
public class CreateProjectPopup extends Popup {

    ImString name = new ImString();
    ImString path = new ImString();

    /**
     * Create Project Constructor.
     */
    public CreateProjectPopup() {
        super("CREATE PROJECT", 800, 170);
    }

    /**
     * Render popup body.
     */
    @Override
    public void body() {
        ImGui.columns(2, "##COL", false);
        ImGui.setColumnWidth(-1, 60);

        ImGui.text("Name");
        ImGui.nextColumn();
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##Name", name);

        ImGui.nextColumn();
        ImGui.text("Path");
        ImGui.nextColumn();

        ImGui.beginDisabled();
        ImGui.setNextItemWidth(-40);
        ImGui.inputText("##Path", path);
        ImGui.endDisabled();

        ImGui.sameLine();
        renderFolderButton();

        ImGui.columns(1);;

        renderCreateButton();
    }

    /**
     * Render folder button.
     */
    private void renderFolderButton() {
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);

        ImVec2 cursor = ImGui.getCursorPos();
        if (ImGui.button("##Open", 35, 30)) {
            String selectedPath = EditorUtils.openFolderDialog();
            path.set(selectedPath == null ? "" : selectedPath);
        }
        if (ImGui.isItemActive()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.4f, 0.4f, 1.0f);
        } else if (ImGui.isItemHovered()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.3f, 0.6f, 0.8f, 0.8f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.8f, 0.8f, 1.0f);
        }
        ImGui.setCursorPos(cursor.x + 5, cursor.y + 3);
        ImGui.text(Icons.Folder);
        ImGui.popStyleColor();
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);
    }

    /**
     * Render create project button.
     */
    private void renderCreateButton() {
        ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 50);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5);
        ImGui.pushFont(EditorAssetManager.getInstance().getFont("Default"));
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.6f, 0.8f, 0.8f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.4f, 0.4f, 1.0f);
        if (ImGui.button(" CREATE ")) {
            try {
                ProjectManager.createProject(path.get(), name.get());
                ImGui.closeCurrentPopup();
            } catch (RuntimeException e) {
                ErrorPopup.open(e.getLocalizedMessage());
                Logger.log(Logger.Type.ERROR, e.getMessage());
            }
        }
        ImGui.popStyleColor(2);
        ImGui.popFont();
    }
}