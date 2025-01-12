package org.katia;

import org.katia.game.Game;

public class Main {

    public static void main(String[] args) {
        // NOTE: Initialize Engine Core only when game is running as stand alone application.
        try {
            EngineCore.initialize();
        } catch (RuntimeException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }

        Game game = new Game(".");
        game.run().dispose();

        EngineCore.dispose();
    }
}