package org.katia.editor.menubar;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.Data;
import org.katia.Icons;
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
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 5, 10);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
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

            ImGui.sameLine();
            ImGui.setCursorPosX(ImGui.getWindowWidth() - 300);
            ImGui.setCursorPosY(5);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
            ImGui.beginChild("Run toolbar", 150, 35, true, ImGuiWindowFlags.NoScrollbar);
            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default"));

//            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.0f, 0.0f, 0.0f, 0.0f);
//            ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
//            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
            var cursor = ImGui.getCursorPos();
            if (ImGui.button("##PLAY", 40, 35)) {

            }
            ImGui.setCursorPos(cursor.x + 11, cursor.y + 7);
            ImGui.pushStyleColor(ImGuiCol.Text, 0.4f, 0.8f, 0.4f, 0.8f);
            ImGui.text(Icons.Play);
            ImGui.popStyleColor();
            ImGui.sameLine();
//            ImGui.setCursorPos(cursor.x, cursor.y);
            ImGui.setCursorPos(ImGui.getCursorPosX()+5, cursor.y);
            cursor = ImGui.getCursorPos();
            if (ImGui.button("##PAUSE", 40, 35)) {

            }
            ImGui.setCursorPos(cursor.x + 12, cursor.y + 7);
            ImGui.text(Icons.Pause);
            ImGui.popFont();
            ImGui.popStyleVar();
//            ImGui.popStyleColor(3);

            ImGui.endChild();
            ImGui.popStyleVar();
            ImGui.sameLine();


            ImGui.getIO().getDeltaTime();
            ImGui.setCursorPosY(0);
            ImGui.setCursorPosX(ImGui.getWindowWidth() - 130);
            ImGui.textDisabled("FPS: " + (int) ImGui.getIO().getFramerate());
            ImGui.endMainMenuBar();
        }
        ImGui.popStyleVar(2);
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
