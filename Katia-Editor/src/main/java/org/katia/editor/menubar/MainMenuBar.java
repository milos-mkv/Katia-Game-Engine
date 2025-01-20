package org.katia.editor.menubar;

import imgui.ImGui;
import lombok.Data;
import org.katia.Logger;
import org.katia.editor.Editor;
import org.katia.editor.EditorUtils;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.popups.CreateNewProjectPopup;
import org.katia.editor.popups.ErrorPopup;
import org.katia.editor.windows.UIComponent;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

@Data
public class MainMenuBar implements UIComponent {

    HashMap<MenuAction, Boolean> actions;

    /**
     * Main menu bar constructor.
     */
    public MainMenuBar() {
        Logger.log(Logger.Type.INFO, "Creating main menu bar ...");
        this.actions = new HashMap<>();

        for (MenuAction action : MenuAction.values()) {
            this.actions.put(action, false);
        }
    }

    /**
     * Render main menu bar in editor window.
     */
    @Override
    public void render() {
        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));

        if (ImGui.beginMainMenuBar()) {
            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default"));
            ImGui.textDisabled(" KATIA ");
            ImGui.popFont();

            ImGui.sameLine();
            renderFileMenuBar();
            renderSceneMenu();
            renderProjectMenu();
            renderEditorMenu();
            renderHelpMenu();
            ImGui.getIO().getDeltaTime();
            ImGui.setCursorPosX(ImGui.getWindowWidth() - 130);
            ImGui.textDisabled("FPS: " + (int) ImGui.getIO().getFramerate());
            ImGui.endMainMenuBar();
        }
        ImGui.popFont();
        if (this.actions.get(MenuAction.CREATE_NEW_PROJECT)) {
            ImGui.openPopup("Create New Project Popup");
        }
        if (this.actions.get(MenuAction.OPEN_PROJECT)) {
            openProjectAction();
        }
        if (this.actions.get(MenuAction.SAVE_PROJECT)) {
            saveProjectAction();
            ImGui.openPopup("Error Popup");
        }
        if (this.actions.get(MenuAction.EXIT)) {
            exitAction();
        }
        CreateNewProjectPopup.render();

        actions.replaceAll((key, value) -> false);
        ErrorPopup.render();
    }

    /**
     * Render file menu bar.
     */
    private void renderFileMenuBar() {
        if (ImGui.beginMenu("File")) {
            this.actions.put(MenuAction.CREATE_NEW_PROJECT, ImGui.menuItem("New Project", "Ctrl+N"));
            this.actions.put(MenuAction.OPEN_PROJECT, ImGui.menuItem("Open Project", "Ctrl+O"));
            this.actions.put(MenuAction.SAVE_PROJECT, ImGui.menuItem("Save", "Ctrl+S"));
            ImGui.separator();
            this.actions.put(MenuAction.EXIT, ImGui.menuItem("Exit", "Ctrl+W"));
            ImGui.endMenu();
        }
    }

    private void renderSceneMenu() {
        if (ImGui.beginMenu("Scene")) {
            this.actions.put(MenuAction.CREATE_NEW_SCENE, ImGui.menuItem("New Scene"));
            this.actions.put(MenuAction.OPEN_SCENE, ImGui.menuItem("Open Scene"));
            ImGui.endMenu();
        }
    }

    private void renderProjectMenu() {
        if (ImGui.beginMenu("Project")) {
            this.actions.put(MenuAction.PROJECT_SETTINGS, ImGui.menuItem("Settings"));
            ImGui.endMenu();
        }
    }

    private void renderEditorMenu() {
        if (ImGui.beginMenu("Editor")) {
            ImGui.endMenu();
        }
    }

    private void renderHelpMenu() {
        if (ImGui.beginMenu("Help")) {
            this.actions.put(MenuAction.ABOUT, ImGui.menuItem("About"));
            ImGui.endMenu();
        }
    }

    private void openProjectAction() {
        String directory = EditorUtils.openFolderDialog();
        if (directory != null && !directory.isEmpty()) {
            try {
                ProjectManager.getInstance().openProject(directory);
            } catch (RuntimeException e) {
                Logger.log(Logger.Type.ERROR, e.getMessage());
            }
        }
    }

    private void saveProjectAction() {

    }

    private void exitAction() {
        GLFW.glfwSetWindowShouldClose(Editor.getInstance().getHandle(), true);
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of main menu bar ...");
    }
}
