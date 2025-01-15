package org.katia;


import org.katia.editor.Editor;

public class Main {
    public static void main(String[] args) {
        EngineCore.initialize();
        Editor.getInstance().run();
        Editor.getInstance().dispose();
        EngineCore.dispose();
    }
}