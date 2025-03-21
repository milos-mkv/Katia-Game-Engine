package org.katia.editor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.katia.Logger;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.renderer.EditorSceneRenderer;
import org.katia.game.Game;
import org.lwjgl.glfw.GLFW;

/**
 * This class represents editor application window.
 */
@Data
public class Editor {

    @Getter
    static final Editor instance = new Editor();

    public Game runGame;

    /**
     * Editor constructor.
     */
    public Editor() {
        Logger.log(Logger.Type.INFO, "Editor Constructor");
        EditorWindow.getInstance();
        EditorAssetManager.getInstance();
        EditorUI.getInstance();
    }

    /**
     * Run editor.
     */
    public void run() {
        Logger.log(Logger.Type.INFO, "Editor run");

        ProjectManager.openProject("/home/mmilicevic/Documents/GitHub/Katia-Game-Engine/Tetris");
        ProjectManager.getGame().getSceneManager().setActiveScene("MainScene");

        GLFW.glfwSwapInterval(0);
        while (!GLFW.glfwWindowShouldClose(EditorWindow.getInstance().getHandle())) {
            GLFW.glfwPollEvents();
            EditorSceneRenderer.getInstance().render();
            EditorUI.getInstance().render();
            GLFW.glfwMakeContextCurrent(EditorWindow.getInstance().getHandle());

            if (runGame != null) {
                GLFW.glfwMakeContextCurrent(runGame.getWindow().getHandle());
                runGame.update(null);

                if (GLFW.glfwWindowShouldClose(runGame.getWindow().getHandle())) {
                    runGame.dispose();
                    runGame = null;
                }
            }
            GLFW.glfwMakeContextCurrent(EditorWindow.getInstance().getHandle());

        }

        dispose();
    }

    /**
     * Dispose of editor.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of Editor");
        ProjectManager.getGame().getAudioManager().dispose();
        EditorUI.getInstance().dispose();
        EditorAssetManager.getInstance().dispose();
        EditorWindow.getInstance().dispose();
    }
}
