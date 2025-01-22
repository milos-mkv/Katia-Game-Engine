package org.katia.editor.windows;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.flag.ImGuiDockNodeFlags;
import org.katia.Logger;
import org.katia.factory.TextureFactory;

public class SceneWindow implements UIComponent {
    ImGuiWindowClass windowClass;
    public SceneWindow() {
        Logger.log(Logger.Type.INFO, "Creating scene window ...");
        windowClass= new ImGuiWindowClass();
        windowClass.setDockNodeFlagsOverrideSet(
                ImGuiDockNodeFlags.NoDockingOverMe | ImGuiDockNodeFlags.NoDockingSplitMe | ImGuiDockNodeFlags.NoCloseButton | ImGuiDockNodeFlags.NoTabBar);
    }

    @Override
    public void render() {
        ImGui.setNextWindowClass(windowClass);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);

        ImGui.begin("Scene");
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10, 5);

        ImGui.textDisabled("SCENE");
        ImGui.beginChild("##SceneChild", -1, -1, true);

        ImGui.endChild();
        ImGui.popStyleVar();

        ImGui.end();
        ImGui.popStyleVar();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of scene window ...");

    }
}
