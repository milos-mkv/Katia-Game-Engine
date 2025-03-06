package org.katia.editor.ui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.katia.Logger;

public class DockSpace implements UIComponent{

    public DockSpace() {
        Logger.log(Logger.Type.INFO, "Dock Space Constructor");
    }

    @Override
    public void render() {
        ImVec2 viewportPos = ImGui.getMainViewport().getPos();
        ImVec2 viewportSize = ImGui.getMainViewport().getSize();

        float padding = 10.0f; // Adjust dock space bounds
        ImGui.setNextWindowPos(viewportPos.x + padding, viewportPos.y + padding + 40);
        ImGui.setNextWindowSize(viewportSize.x - 2 * padding, viewportSize.y - 2 * padding - 40);
        ImGui.setNextWindowBgAlpha(0.0f); // Make it transparent if needed

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 1, 1);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleColor(ImGuiCol.Separator, 0.0f, 0.0f, 0.0f, 0.0f);

        ImGui.begin("DockSpace", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
        ImGui.dockSpace(ImGui.getID("DockSpace"));
        ImGui.end();

        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
    }
}
