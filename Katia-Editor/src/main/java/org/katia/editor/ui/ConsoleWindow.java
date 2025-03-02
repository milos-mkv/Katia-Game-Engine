package org.katia.editor.ui;

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
}
