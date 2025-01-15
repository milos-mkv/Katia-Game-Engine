package org.katia.editor.windows;

import imgui.ImGui;
import org.katia.Logger;

public class SceneWindow implements UIComponent {

    public SceneWindow() {
        Logger.log(Logger.Type.INFO, "Creating scene window ...");
    }

    @Override
    public void render() {
        ImGui.begin("Scene");

        ImGui.end();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of scene window ...");

    }
}
