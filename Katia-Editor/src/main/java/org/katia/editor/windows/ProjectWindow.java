package org.katia.editor.windows;

import imgui.ImGui;
import org.katia.Logger;

public class ProjectWindow implements UIComponent {

    public ProjectWindow() {
        Logger.log(Logger.Type.INFO, "Creating project window ...");
    }

    @Override
    public void render() {
        ImGui.begin("Project");

        ImGui.end();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of project window ...");

    }
}
