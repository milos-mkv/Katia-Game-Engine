package org.katia;

import org.katia.game.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        // NOTE: Initialize Engine Core only when game is running as standalone application.
        try {
            EngineCore.initialize();
        } catch (RuntimeException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }

        Game game = new Game(".");
        game.run();
        game.dispose();

        EngineCore.dispose();
    }
}