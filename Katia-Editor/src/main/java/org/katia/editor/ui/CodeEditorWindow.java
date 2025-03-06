package org.katia.editor.ui;

import imgui.ImColor;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;
import imgui.flag.ImGuiCol;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CodeEditorWindow extends UICoreDockWindow {

    TextEditor editor;

    public CodeEditorWindow() {
        super("Code Editor");
        editor = new TextEditor();
        TextEditorLanguageDefinition lua = TextEditorLanguageDefinition.lua();

        // Apply the new keyword list
        lua.setKeywords(Arrays.asList(
                "and", "break", "do", "else", "elseif", "end", "false", "for",
                "function", "if", "in", "local", "nil", "not", "or", "repeat",
                "return", "then", "true", "until", "while",  // Default Lua keywords
                "myCustomKeyword1", "myCustomFunction", "specialVar" // Custom keywords
        ).toArray(new String[0]));
        editor.setLanguageDefinition(lua);
        int[] p = editor.getPalette();
        p[TextEditorPaletteIndex.Background] = ImColor.rgba(36, 41, 46, 255);
        editor.setPalette(p);

    }

    @Override
    protected void body() {
        editor.render("code editors");
    }
}
