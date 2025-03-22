package org.katia.editor.ui.menubar;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.Data;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.core.Scene;
import org.katia.editor.Editor;
import org.katia.editor.EditorUI;
import org.katia.editor.EditorUtils;
import org.katia.editor.EditorWindow;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.ui.windows.*;
import org.katia.editor.ui.menubar.menus.*;
import org.katia.editor.ui.popups.CreateProjectPopup;
import org.katia.editor.ui.popups.CreateScenePopup;
import org.katia.editor.ui.popups.OpenScenePopup;
import org.katia.editor.ui.popups.PopupManager;
import org.katia.factory.GameFactory;
import org.katia.factory.SceneFactory;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * This class represents editors main menu bar.
 */
@Data
public class MainMenuBar {

    HashMap<MenuAction, Boolean> actions = new HashMap<MenuAction, Boolean>();
    List<Menu> menus;

    /**
     * Main menu bar constructor.
     */
    public MainMenuBar() {
        Logger.log(Logger.Type.INFO, "Creating main menu bar ...");
        for (MenuAction action : MenuAction.values()) {
            this.actions.put(action, false);
        }
        menus = new ArrayList<>();
        menus.add(new FileMenu(this));
        menus.add(new ViewMenu(this));
        menus.add(new SceneMenu(this));
        menus.add(new HelpMenu(this));
    }

    /**
     * Render main menu bar in editor window.
     */
    public void render() {
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 5, 8);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 15, 10);
        if (ImGui.beginMainMenuBar()) {

            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default"));
            ImGui.textDisabled(" KATIA ");
            ImGui.popFont();

            ImGui.sameLine();

            menus.forEach(Menu::render);

            ImGui.sameLine();


            renderToolbar();
            ImGui.sameLine();


            ImGui.getIO().getDeltaTime();
            ImGui.setCursorPosY(0);
            ImGui.setCursorPosX(ImGui.getWindowWidth() - 130);
            ImGui.textDisabled("FPS: " + (int) ImGui.getIO().getFramerate());
            ImGui.endMainMenuBar();
        }
        ImGui.popStyleVar(3);
        if (this.actions.get(MenuAction.CREATE_NEW_PROJECT)) {
            PopupManager.getInstance().openPopup(CreateProjectPopup.class);
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
        if (this.actions.get(MenuAction.CREATE_NEW_SCENE)) {
            PopupManager.getInstance().openPopup(CreateScenePopup.class);
        }
        if (this.actions.get(MenuAction.OPEN_SCENE)) {
            PopupManager.getInstance().openPopup(OpenScenePopup.class);
        }
        if (this.actions.get(MenuAction.SAVE_SCENE)) {
            try {
                ProjectManager.saveCurrentScene();
            } catch (RuntimeException e) {
                Logger.log(Logger.Type.ERROR, e.toString());
            }
        }
        if (this.actions.get(MenuAction.TOGGLE_HIERARCHY_WINDOW)) {
            EditorUI.getInstance().getWindow(HierarchyWindow.class).setVisible(
                    !EditorUI.getInstance().getWindow(HierarchyWindow.class).isVisible()
            );
        }
        if (this.actions.get(MenuAction.TOGGLE_INSPECTOR_WINDOW)) {
            EditorUI.getInstance().getWindow(InspectorWindow.class).setVisible(
                    !EditorUI.getInstance().getWindow(InspectorWindow.class).isVisible()
            );
        }
        if (this.actions.get(MenuAction.TOGGLE_PROJECT_WINDOW)) {
            EditorUI.getInstance().getWindow(ProjectWindow.class).setVisible(
                    !EditorUI.getInstance().getWindow(ProjectWindow.class).isVisible()
            );
        }
        if (this.actions.get(MenuAction.RUN_GAME)) {
            Editor.getInstance().runGame = GameFactory.createGame(ProjectManager.getGame().getDirectory());
            Editor.getInstance().runGame.setDebug(true);
//            Editor.getInstance().runGame.getSceneManager().setActiveScene(ProjectManager.getGame().getSceneManager().getActiveScene().getName());
            Editor.getInstance().runGame.getSceneManager().setCustomScene(ProjectManager.getGame().getSceneManager().getActiveScene());
            Editor.getInstance().runGame.getScriptExecutioner().init();
            GLFW.glfwMakeContextCurrent(EditorWindow.getInstance().getHandle());
            GLFW.glfwSwapInterval(0);
        }

        actions.replaceAll((key, value) -> false);
    }

    /**
     * Render toolbar.
     */
    private void renderToolbar() {
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 340);
        ImGui.setCursorPosY(8);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.beginChild("Run toolbar", 163, 30, true, ImGuiWindowFlags.NoScrollbar);
        EditorAssetManager.getInstance().getFonts().get("Default").setScale(0.8f);
        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default"));

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 5);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.0f, 0.0f, 0.0f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);

        ImGui.setCursorPosX(ImGui.getCursorPosX() + 2);
        var cursor = ImGui.getCursorPos();
        if (Editor.getInstance().getRunGame() != null ) {
            ImGui.beginDisabled();
        }
        if (ImGui.button("##PLAY", 40, 30)) {
            actions.put(MenuAction.RUN_GAME, true);
        }
        if (Editor.getInstance().getRunGame() != null ) {
            ImGui.endDisabled();
        }
        ImGui.setCursorPos(cursor.x + 13, cursor.y + 6);
        ImGui.pushStyleColor(ImGuiCol.Text, 0.4f, 0.8f, 0.4f, 0.8f);
        ImGui.text(Icons.Play);
        ImGui.popStyleColor();
        ImGui.sameLine();
        ImGui.setCursorPos(ImGui.getCursorPosX()+5, cursor.y);
        cursor = ImGui.getCursorPos();
        if (Editor.getInstance().getRunGame() == null ) {
            ImGui.beginDisabled();
        }
        if (ImGui.button("##PAUSE", 40, 30)) {
            GLFW.glfwSetWindowShouldClose(Editor.getInstance().getRunGame().getWindow().getHandle(), true);
        }
        ImGui.setCursorPos(cursor.x + 14, cursor.y + 6);
        ImGui.text(Icons.Pause);
        if (Editor.getInstance().getRunGame() == null ) {
            ImGui.endDisabled();
        }
        ImGui.sameLine();
        ImGui.setCursorPos(cursor.x + 40, cursor.y);

        cursor = ImGui.getCursorPos();

        if (ImGui.button("##SCRIPT", 40, 30)) {
            EditorUI.getInstance().getWindow(CodeEditorWindow.class).setVisible(true);
            EditorUI.getInstance().getWindow(SceneWindow.class).setVisible(false);
        }

        ImGui.setCursorPos(cursor.x + 9, cursor.y + 6);
        ImGui.text(Icons.Script);

        ImGui.sameLine();
        ImGui.setCursorPos(cursor.x + 40, cursor.y);

        cursor = ImGui.getCursorPos();
        if (ImGui.button("##SCENE", 40, 30)) {
            EditorUI.getInstance().getWindow(CodeEditorWindow.class).setVisible(false);
            EditorUI.getInstance().getWindow(SceneWindow.class).setVisible(true);
        }
        ImGui.setCursorPos(cursor.x + 9, cursor.y + 6);
        ImGui.text(Icons.Display);


        EditorAssetManager.getInstance().getFonts().get("Default").setScale(1.0f);

        ImGui.popFont();
        ImGui.popStyleVar(2);
            ImGui.popStyleColor(3);

        ImGui.endChild();
        ImGui.popStyleVar();
    }

    private void openProjectAction() {
        String directory = EditorUtils.openFolderDialog();
        if (directory != null && !directory.isEmpty()) {
            try {
                ProjectManager.openProject(directory);
            } catch (RuntimeException e) {
                Logger.log(Logger.Type.ERROR, e.getMessage());
            }
        }
    }

    private void saveProjectAction() {
      String json = SceneFactory.generateJsonFromScene(ProjectManager.getGame().getSceneManager().getActiveScene());
      System.out.println(json);
    }

    private void exitAction() {
        GLFW.glfwSetWindowShouldClose(EditorWindow.getInstance().getHandle(), true);
    }

}
