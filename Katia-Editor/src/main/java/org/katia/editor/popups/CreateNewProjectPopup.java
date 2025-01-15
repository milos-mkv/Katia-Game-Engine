package org.katia.editor.popups;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;

public class CreateNewProjectPopup {

    static ImString name = new ImString();
    static ImString path = new ImString();


    public static void render() {
        ImVec2 workSize = ImGui.getMainViewport().getWorkSize();
        ImVec2 modalSize = new ImVec2(500, 160);
        ImBoolean unusedOpen = new ImBoolean(true);
        ImGui.setNextWindowPos(workSize.x / 2 - modalSize.x / 2, workSize.y / 2 - modalSize.y / 2);
        ImGui.setNextWindowSize(modalSize.x, modalSize.y);

        var flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoTitleBar;
        if (ImGui.beginPopupModal("Create New Project Popup", unusedOpen, flags)) {
            ImGui.text("Create new project");
            ImGui.separator();
            ImGui.columns(2);
            ImGui.setColumnWidth(-1, 60);
            ImGui.text("Name");
            ImGui.nextColumn();
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##Name", name);
            ImGui.nextColumn();
            ImGui.text("Path");
            ImGui.nextColumn();
            ImGui.beginDisabled();
            ImGui.setNextItemWidth(-50);
            ImGui.inputText("##Path", path);
            ImGui.endDisabled();
            ImGui.sameLine();
            if (ImGui.button("F")) {
//                String path = Utils.openFolderDialog();
//                if (path != null) {
//                    CreateNewProjectPopup.path.set(path);
//                }
            }
            ImGui.columns(1);
            ImGui.separator();
            ImGui.setCursorPosX(ImGui.getWindowWidth() - 165);
            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.sameLine();
            if (ImGui.button("Create")) {
//                ProjectManager.getInstance().createProject(name.get(), path.get());
            }
            ImGui.endPopup();
        }
    }

}
