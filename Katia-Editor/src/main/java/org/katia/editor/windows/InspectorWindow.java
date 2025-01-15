package org.katia.editor.windows;

import imgui.ImGui;
import org.katia.Logger;

public class InspectorWindow implements UIComponent {

    public InspectorWindow() {
        Logger.log(Logger.Type.INFO, "Creating inspector window ...");
    }

    @Override
    public void render() {
        ImGui.begin("Inspector");

        ImGui.end();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of inspector window ...");
    }
}
