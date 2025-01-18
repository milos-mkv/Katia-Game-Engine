package org.katia.editor.windows;

import imgui.ImGui;
import org.katia.Logger;
import org.katia.editor.Editor;
import org.katia.editor.EditorUtils;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
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
                    String directory = EditorUtils.openFolderDialog();
                    if (directory != null && !directory.isEmpty()) {
                        try {
                            ProjectManager.getInstance().openProject(directory);
                        } catch (RuntimeException e) {
                            Logger.log(Logger.Type.ERROR, e.getMessage());
                        }
                    }
                }
                if (ImGui.menuItem("Save", "Ctrl+S")) {

                }
                ImGui.separator();
                if (ImGui.menuItem("Exit", "Ctrl+W")) {
                    GLFW.glfwSetWindowShouldClose(Editor.getInstance().getHandle(), true);
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Scene")) {
                if (ImGui.menuItem("New Scene")) {
                }
                if (ImGui.menuItem("Open Scene")) {
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Project")) {
                if (ImGui.menuItem("Settings/Configuration")) {
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Editor")) {
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Help")) {
                if (ImGui.menuItem("About")) {
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
