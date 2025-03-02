package org.katia;

import org.katia.factory.GameFactory;
import org.katia.game.Game;
import org.lwjgl.glfw.GLFW;

public class Main {

    public static void main(String[] args) {
        try {
            EngineCore.initialize();
        } catch (RuntimeException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
            return;
        }

        Game game = GameFactory.createGame("/home/mmilicevic/Desktop/test");
        game.setDebug(true);
        game.getSceneManager().setActiveScene("MainScene");
        GLFW.glfwSwapInterval(0);
        game.getScriptExecutioner().init();

        while (!GLFW.glfwWindowShouldClose(game.getWindow().getHandle())) {
            game.update(null);
        }
        game.dispose();

        EngineCore.dispose();
    }
}