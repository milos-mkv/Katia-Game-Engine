package org.katia.editor.ui.popups;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import org.katia.Icons;

public class ErrorPopup {

    static String errorMessage = "";

    public static void open(String message) {
        errorMessage = message;
        ImGui.openPopup("Error Popup");
    }

    public static void render() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);

        if (ImGui.beginPopup("Error Popup")) {
            ImGui.setCursorPosX(86);
            ImGui.textColored(0.8f, 0.4f, 0.4f, 1.0f, Icons.TriangleError + " ERROR " + Icons.TriangleError);
            ImGui.beginChild("ErrorChild", 300, 150, true);
            ImGui.textWrapped(errorMessage);
            ImGui.endChild();
            ImGui.endPopup();
        }
        ImGui.popStyleVar();
    }

}
