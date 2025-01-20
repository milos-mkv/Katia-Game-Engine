package org.katia.editor.popups;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.katia.Icons;
import org.katia.editor.managers.EditorAssetManager;

public class ErrorPopup {

    static String errorMessage = "";
    public static void setMessage(String message) {
        errorMessage = message;
    }

    public static void render() {
        ImVec2 workSize = ImGui.getMainViewport().getWorkSize();
        ImVec2 modalSize = new ImVec2(400, 250);

        ImBoolean unusedOpen = new ImBoolean(true);
        ImGui.setNextWindowPos(workSize.x / 2 - modalSize.x / 2, workSize.y / 2 - modalSize.y / 2);
        ImGui.setNextWindowSize(modalSize.x, modalSize.y);

        int flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoTitleBar;
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 10);
        if (ImGui.beginPopupModal("Error Popup", unusedOpen, flags)) {
            ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 65);
            ImGui.textColored(0.8f, 0.4f, 0.4f, 1.0f, Icons.TriangleError + " ERROR " + Icons.TriangleError);
            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));
            ImGui.pushStyleColor(ImGuiCol.ScrollbarGrab, 0.8f, 0.4f, 0.4f, 1.0f);
            ImGui.beginChild("Error message body", -1, -35);
            ImGui.setCursorPosX(10);
            ImGui.textWrapped(errorMessage);
            ImGui.endChild();
            ImGui.popStyleColor();
            ImGui.popFont();
            ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 50);
            if (ImGui.button(" CLOSE ")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
        ImGui.popStyleVar();
    }

}
