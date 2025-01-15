package org.katia.editor.windows;

import imgui.ImGui;
import org.katia.Logger;

public class ConsoleWindow implements UIComponent {

    public ConsoleWindow() {
        Logger.log(Logger.Type.INFO, "Creating console window ...");

    }

    @Override
    public void render() {
        ImGui.begin("Console");

        ImGui.end();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of console window ...");
    }
}
