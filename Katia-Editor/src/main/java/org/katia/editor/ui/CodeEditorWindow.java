package org.katia.editor.ui;

import imgui.ImColor;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiKey;
import org.katia.editor.managers.EditorInputManager;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class CodeEditorWindow extends UICoreDockWindow {

    TextEditor editor;
    private final Set<String> keywords;
    private String currentSuggestion = null;

    public CodeEditorWindow() {
        super("Code Editor");
        editor = new TextEditor();
        TextEditorLanguageDefinition lua = TextEditorLanguageDefinition.lua();

        // Define keywords for autocompletion
        keywords = new HashSet<>(Arrays.asList(
                "and", "break", "do", "else", "elseif", "end", "false", "for",
                "function", "if", "in", "local", "nil", "not", "or", "repeat",
                "return", "then", "true", "until", "while",
                "coroutine", "string", "table", "math", "io", "os", "debug",
                "Behaviour"
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

//        luaIdentifiers.put("Behaviour", "Game Object Behaviour Instance");


        // Apply the custom identifiers
        lua.setIdentifiers(luaIdentifiers);

        editor.setLanguageDefinition(lua);
        int[] p = editor.getPalette();
        p[TextEditorPaletteIndex.Background] = ImColor.rgba(36, 41, 46, 255);
        editor.setPalette(p);

    }

    @Override
    protected void body() {
        editor.render("code editors");
        handleAutocomplete();
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
