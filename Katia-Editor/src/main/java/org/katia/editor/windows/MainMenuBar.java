package org.katia.editor.windows;

import imgui.ImGui;
import org.katia.Logger;
import org.katia.editor.Editor;
import org.katia.editor.popups.CreateNewProjectPopup;
import org.lwjgl.glfw.GLFW;

public class MainMenuBar implements UIComponent {

    int fps;
    float time;
    public MainMenuBar() {
        Logger.log(Logger.Type.INFO, "Creating main menu bar ...");
        fps = 0;
        time = 0;
    }

    @Override
    public void render() {
        boolean newProjectAction = false;

        time += ImGui.getIO().getDeltaTime();
        if (time > 1) {
            time = 0;
            fps = (int) ImGui.getIO().getFramerate();
        }
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                newProjectAction = ImGui.menuItem("New Project", "Ctrl+N");
                if (ImGui.menuItem("Open Project", "Ctrl+O")) {

                }
                ImGui.separator();
                if (ImGui.menuItem("Exit", "Ctrl+W")) {
                    GLFW.glfwSetWindowShouldClose(Editor.getInstance().getHandle(), true);
                }
                ImGui.endMenu();
            }
            ImGui.getIO().getDeltaTime();
            ImGui.setCursorPosX(ImGui.getWindowWidth() - 130);
            ImGui.textDisabled("FPS: " + fps);
            ImGui.endMainMenuBar();

        }
        if (newProjectAction) {
            ImGui.openPopup("Create New Project Popup");

        }
        CreateNewProjectPopup.render();

    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of main menu bar ...");
    }
}
