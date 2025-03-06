package org.katia;

import org.katia.editor.Editor;

public class Main {
    public static void main(String[] args) {
        try {
            EngineCore.initialize();
            Editor.getInstance().run();
            EngineCore.dispose();
        } catch (RuntimeException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
    }
}