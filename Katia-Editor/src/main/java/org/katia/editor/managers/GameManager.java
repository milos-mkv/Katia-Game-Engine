package org.katia.editor.managers;

import lombok.Data;
import lombok.Getter;
import org.katia.editor.Editor;
import org.katia.game.Game;
import org.lwjgl.glfw.GLFW;

@Data
public class GameManager {

    @Getter
    static GameManager instance = new GameManager();

    Game game;

    public GameManager() {

    }

    public void start() {
        game = new Game(ProjectManager.getInstance().getPath());
        GLFW.glfwMakeContextCurrent(Editor.getInstance().getHandle());

    }

    public void run() {
        if (game == null) {
            return;
        }
        GLFW.glfwMakeContextCurrent(game.getWindow().getHandle());
//        game.run();
        GLFW.glfwMakeContextCurrent(Editor.getInstance().getHandle());
    }

}
