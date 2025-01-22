package org.katia.editor.windows;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.flag.ImGuiDockNodeFlags;
import org.katia.Logger;
import org.katia.editor.managers.EditorAssetManager;

public class HierarchyWindow implements UIComponent {
    ImGuiWindowClass windowClass;
    public HierarchyWindow() {
        Logger.log(Logger.Type.INFO, "Creating hierarchy window ...");
        windowClass= new ImGuiWindowClass();
        windowClass.setDockNodeFlagsOverrideSet(
                ImGuiDockNodeFlags.NoDockingOverMe | ImGuiDockNodeFlags.NoDockingSplitMe | ImGuiDockNodeFlags.NoCloseButton | ImGuiDockNodeFlags.NoTabBar);

    }

    @Override
    public void render() {
        ImGui.setNextWindowClass(windowClass);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
        ImGui.begin("Hierarchy");

        ImGui.textDisabled("HIERARCHY");
        ImGui.beginChild("##HierarchyChild", -1, -1, true);

        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));

        ImGui.popFont();
        ImGui.endChild();
        ImGui.end();
        ImGui.popStyleVar();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of hierarchy window ...");

    }

}
