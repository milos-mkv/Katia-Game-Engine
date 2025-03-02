package org.katia.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.Data;
import lombok.Getter;
import org.katia.Logger;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.renderer.EditorSceneRenderer;
import org.katia.editor.ui.UI;
import org.katia.game.Game;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

@Data
public class Editor extends EditorWindow {

    @Getter
    static final Editor instance = new Editor();

    private UI ui;
    public Game runGame;

    public Editor() {
        super();
        Logger.log(Logger.Type.INFO, "Editor Constructor");
        ui = new UI();
    }

    /**
     * Run editor.
     */
    public void run() {
        Logger.log(Logger.Type.INFO, "Editor run");
        GLFW.glfwSwapInterval(0);

        ProjectManager.openProject("/home/mmilicevic/Desktop/test");
        ProjectManager.getGame().getSceneManager().setActiveScene("MainScene");
//        EditorSceneManager.getInstance().openScene("/home/mmilicevic/Desktop/test/scenes/MainScene.scene");
        while (!GLFW.glfwWindowShouldClose(handle)) {

            GLFW.glfwPollEvents();

            EditorSceneRenderer.getInstance().render();

            glViewport(0, 0, windowSize.x, windowSize.y);
            glClearColor(0.14f, 0.16f, 0.18f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();
            ImGuizmo.beginFrame();

            ImVec2 viewportPos = ImGui.getMainViewport().getPos();
            ImVec2 viewportSize = ImGui.getMainViewport().getSize();

            float padding = 10.0f;// Adjust dock space bounds
            ImGui.setNextWindowPos(viewportPos.x + padding, viewportPos.y + padding + 40);
            ImGui.setNextWindowSize(viewportSize.x - 2 * padding, viewportSize.y - 2 * padding - 50);
            ImGui.setNextWindowBgAlpha(0.0f); // Make it transparent if needed
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 1, 1);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
            ImGui.pushStyleColor(ImGuiCol.Separator, 0.0f, 0.0f, 0.0f, 0.0f);

            ImGui.begin("DockSpace", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
            ImGui.dockSpace(ImGui.getID("DockSpace"));//, 0, 0,  ImGuiDockNodeFlags.AutoHideTabBar);
            ImGui.end();

            ImGui.popStyleColor();
            ImGui.popStyleVar(2);
            ui.render();
            ImGui.render();
            imGuiImplGl3.renderDrawData(ImGui.getDrawData());
            GLFW.glfwSwapBuffers(handle);

            if (runGame != null) {
                GLFW.glfwMakeContextCurrent(runGame.getWindow().getHandle());
                runGame.update(null);

                if (GLFW.glfwWindowShouldClose(runGame.getWindow().getHandle())) {
                    runGame.dispose();
                    runGame = null;
                }
            }
            GLFW.glfwMakeContextCurrent(handle);

        }
    }

    /**
     * Dispose of editor.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of Editor");
        GLFW.glfwDestroyWindow(handle);
    }
}
