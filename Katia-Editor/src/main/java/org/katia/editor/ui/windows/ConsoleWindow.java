package org.katia.editor.ui.windows;

import imgui.ImGui;
import org.katia.editor.Editor;
import org.katia.editor.managers.ProjectManager;
import org.katia.game.Game;

public class ConsoleWindow extends Window {

    public ConsoleWindow() {
        super("Console");
    }


    @Override
    protected void body() {
        Game game = Editor.getInstance().getRunGame();
        if (game == null) return;

        for (String text : game.getScriptExecutioner().getConsole().getLogs()) {
            ImGui.text(text);
        }
    }

}
