package org.katia.editor.ui.windows;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.katia.Icons;
import org.katia.editor.Editor;
import org.katia.editor.EditorUI;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.game.Game;
import org.katia.scripting.LuaConsole;

public class ConsoleWindow extends Window {

    LuaConsole luaConsole;
    private int counter = 0;
    ImString text = new ImString();
    public ConsoleWindow() {
        super("Console");
    }

    @Override
    protected void header() {
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 30);
        var cursor = ImGui.getCursorPos();
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        if (ImGui.button("  ##OpenConsole", 25, 25)) {
            EditorUI.getInstance().getWindow(ProjectWindow.class).setVisible(true);
            EditorUI.getInstance().getWindow(ConsoleWindow.class).setVisible(false);
        }
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);
        EditorAssetManager.getInstance().getFont("Default25").setScale(0.7f);
        ImGui.pushFont(EditorAssetManager.getInstance().getFont("Default25"));
        ImGui.setCursorPos(cursor.x + 3, cursor.y + 5);

        if (ImGui.isItemActive()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.4f, 0.4f, 1.0f);
        } else if (ImGui.isItemHovered()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.3f, 0.6f, 0.8f, 0.8f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.6f, 0.6f, 0.6f, 1.0f);
        }
        ImGui.text(Icons.TreeFolder);
        ImGui.popStyleColor();
        EditorAssetManager.getInstance().getFont("Default25").setScale(1f);

        ImGui.popFont();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 3);


    }

    @Override
    protected void body() {
        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.1f, 0.1f, 0.1f, 0.5f);
        Game game = Editor.getInstance().getRunGame();
        if (game != null)
        {
             luaConsole = game.getScriptExecutioner().getConsole();
        }
        if (luaConsole != null) {
            if (luaConsole.getLogs().size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String text : luaConsole.getLogs()) {
                    stringBuilder.append(text).append("\n");
                }
                this.text.set(stringBuilder.toString(), true);
            }

//            if (counter != luaConsole.getLogs().size()) {
//                counter = luaConsole.getLogs().size();
//                ImGui.setScrollHereY();
//            }
        }


        ImGui.inputTextMultiline("##TEXT", text, -1, -1, ImGuiInputTextFlags.ReadOnly);
        ImGui.popStyleColor();
    }

}
