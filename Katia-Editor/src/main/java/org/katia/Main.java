package org.katia;


import org.katia.editor.Editor;

public class Main {
    public static void main(String[] args) {
        try {
            EngineCore.initialize();
        } catch (RuntimeException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
            return;
        }
        Editor.getInstance().run();
        Editor.getInstance().dispose();
        EngineCore.dispose();
    }
}