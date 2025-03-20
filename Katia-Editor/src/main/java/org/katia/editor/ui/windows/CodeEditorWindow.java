package org.katia.editor.ui.windows;

import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;
import imgui.flag.ImGuiCol;
import org.katia.FileSystem;
import org.katia.editor.EditorUI;
import org.katia.editor.managers.EditorInputManager;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class CodeEditorWindow extends Window {

    TextEditor editor;
    private final Set<String> keywords;
    private String currentSuggestion = null;
    private String file = "";
    boolean toSave = false;
    String oldData = "";

    public CodeEditorWindow() {
        super("Code Editor");
        editor = new TextEditor();
        TextEditorLanguageDefinition lua = TextEditorLanguageDefinition.lua();

        // Define keywords for autocompletion
        keywords = new HashSet<>(Arrays.asList(
                "and", "break", "do", "else", "elseif", "end", "false", "for",
                "function", "if", "in", "local", "nil", "not", "or", "repeat",
                "return", "then", "true", "until", "while",
                "coroutine", "string", "table", "math", "io", "os", "debug", "self"

        ));

        lua.setKeywords(keywords.toArray(new String[0]));

        // Define custom identifiers
        // Define custom identifiers (variable & function names)
        Map<String, String> luaIdentifiers = new HashMap<>();

// Global variables
        luaIdentifiers.put("_G", "Global environment table");
        luaIdentifiers.put("_VERSION", "Lua version string");

// Basic functions
        luaIdentifiers.put("assert", "Raises an error if the first argument is false/nil");
        luaIdentifiers.put("collectgarbage", "Controls garbage collection");
        luaIdentifiers.put("dofile", "Executes a Lua file");
        luaIdentifiers.put("error", "Terminates execution with an error message");
        luaIdentifiers.put("getmetatable", "Gets the metatable of a table");
        luaIdentifiers.put("ipairs", "Iterates over array indices in order");
        luaIdentifiers.put("next", "Iterates over table elements");
        luaIdentifiers.put("pairs", "Iterates over table key-value pairs");
        luaIdentifiers.put("pcall", "Calls a function in protected mode");
        luaIdentifiers.put("print", "Prints values to the console");
        luaIdentifiers.put("rawequal", "Performs raw equality comparison");
        luaIdentifiers.put("rawget", "Gets a table value without invoking metamethods");
        luaIdentifiers.put("rawset", "Sets a table value without invoking metamethods");
        luaIdentifiers.put("select", "Returns selected arguments");
        luaIdentifiers.put("setmetatable", "Sets the metatable of a table");
        luaIdentifiers.put("tonumber", "Converts a value to a number");
        luaIdentifiers.put("tostring", "Converts a value to a string");
        luaIdentifiers.put("type", "Returns the type of a value");
        luaIdentifiers.put("xpcall", "Calls a function in protected mode with error handling");

        luaIdentifiers.put("coroutine", "Lua coroutine library");
        luaIdentifiers.put("string", "Lua string library");
        luaIdentifiers.put("table", "Lua table library");
        luaIdentifiers.put("math", "Lua math library");
        luaIdentifiers.put("io", "Lua I/O library");
        luaIdentifiers.put("os", "Lua OS library");
        luaIdentifiers.put("debug", "Lua debug library");

        luaIdentifiers.put("Behaviour", """
                Parent class of game object behaviour.
                Fields:
                 - self.params - Params passed from game engine to script.
                 - self.gameObject - This game object.
                 - self.scene - Current scene.
                Methods:
                 - update(dt)
                """);
        luaIdentifiers.put("Input", "Lua debug library");
        luaIdentifiers.put("SceneManager", "Lua debug library");
        luaIdentifiers.put("AudioManager", """
                Audio manager.
                Methods:
                 - play(audio) - Play audio.
                 - stop(audio) - Stop audio.
                """);


        // Apply the custom identifiers
        lua.setIdentifiers(luaIdentifiers);

        editor.setLanguageDefinition(lua);
        int[] p = editor.getPalette();
        p[TextEditorPaletteIndex.Background] = ImColor.rgba(36, 41, 46, 255);
        editor.setPalette(p);

    }

    public void openFile(String file) {
        EditorUI.getInstance().getWindow(SceneWindow.class).setVisible(false);
        visible = true;
        this.file = file;

        String data = FileSystem.readFromFile(file);
        editor.setText(data);
        oldData = data;
        editor.setShowWhitespaces(false);
    }

    @Override
    protected void header() {
        ImVec2 size = ImGui.calcTextSize((toSave ? "* " : "  ") + FileSystem.getFileName(file) );
        ImGui.setCursorPosX(ImGui.getWindowWidth() - size.x - 5);
        ImGui.textDisabled((toSave ? "* " : "  ") + FileSystem.getFileName(file));

    }

    @Override
    protected void body() {
        var cursor = ImGui.getCursorPos();
        editor.render("code editors");

//        ImGui.setCursorPos(cursor.x, cursor.y);
//        ImVec2 size = ImGui.calcTextSize(FileSystem.getFileName(file) + (toSave ? "*" : ""));
//
//        ImGui.setCursorPosX(ImGui.getWindowWidth() - size.x - 25);
//        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5);
//        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0, 0, 0, 0.4f);
//        ImGui.beginChild("FileName", size.x + 10, 35, true);
//        ImGui.textDisabled(FileSystem.getFileName(file) + (toSave ? "*" : ""));
//        ImGui.endChild();
//        ImGui.popStyleColor();
        handleAutocomplete();

        if (EditorInputManager.getInstance().isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) &&
        EditorInputManager.getInstance().isKeyJustPressed(GLFW.GLFW_KEY_S)) {
            FileSystem.saveToFile(file, editor.getText());
            toSave = false;
            oldData = editor.getText();
        }

        if (toSave == false) {
            if (!oldData.equals(editor.getText())) {
                toSave = true;
            }
        }
    }

    private void handleAutocomplete() {
        if (ImGui.isWindowHovered()) {
            if (EditorInputManager.getInstance().isKeyJustPressed(GLFW.GLFW_KEY_TAB)) {
                String currentWord = getCurrentWord();
                if (!currentWord.isEmpty()) {
                    String suggestion = getMatchingKeyword(currentWord);
                    if (suggestion != null) {
                        replaceCurrentWord(suggestion);
                    }
                }
            }
        }
    }

    private String getCurrentWord() {
        String text = editor.getText();

        int cursorPos = editor.getCursorPositionColumn();

        if (cursorPos <= 0 || text.isEmpty()) return "";

        int start = cursorPos - 1;
        while (start >= 0 && Character.isLetterOrDigit(text.charAt(start))) {
            start--;
        }
        return text.substring(start + 1, cursorPos);
    }

    private String getMatchingKeyword(String currentWord) {
        return keywords.stream()
                .filter(keyword -> keyword.startsWith(currentWord))
                .findFirst()
                .orElse(null);
    }

    private void replaceCurrentWord(String suggestion) {
        String text = editor.getText();
        int cursorPos = editor.getCursorPositionColumn();

        if (cursorPos <= 0 || text.isEmpty()) return;

        int start = cursorPos - 1;
        while (start >= 0 && Character.isLetterOrDigit(text.charAt(start))) {
            start--;
        }

        // Construct new text
        String newText = text.substring(0, start + 1) + suggestion + text.substring(cursorPos);
        editor.setText(newText);
        editor.setCursorPosition(start + 1 + suggestion.length(), 0);
    }
}
