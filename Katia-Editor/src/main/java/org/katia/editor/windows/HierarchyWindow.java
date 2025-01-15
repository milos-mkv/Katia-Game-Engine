package org.katia.editor.windows;

import imgui.ImGui;
import org.katia.Logger;

public class HierarchyWindow implements UIComponent {

    public HierarchyWindow() {
        Logger.log(Logger.Type.INFO, "Creating hierarchy window ...");
    }

    @Override
    public void render() {
        ImGui.begin("Hierarchy");

        ImGui.end();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of hierarchy window ...");

    }

}
