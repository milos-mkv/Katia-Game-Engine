package org.katia.editor.ui.windows;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.Data;
import org.katia.FileSystem;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.editor.EditorUI;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.ui.menubar.MainMenuBar;
import org.katia.editor.ui.menubar.MenuAction;
import org.katia.editor.ui.widgets.ProjectDirectoryExplorerWidget;
import org.katia.factory.GameObjectFactory;

import java.nio.file.Path;

@Data
public class ProjectWindow extends Window {

    ProjectDirectoryExplorerWidget directoryExplorerWidget;

    public ProjectWindow() {
        super("Project");
        Logger.log(Logger.Type.INFO, "Creating project window ...");

        directoryExplorerWidget = new ProjectDirectoryExplorerWidget();
        directoryExplorerWidget.setRootDirectory("C:\\Users\\milos\\Desktop\\Demo Game");
    }
//
//    @Override
//    public void render() {

    @Override
    protected void header() {
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 30);
        var cursor = ImGui.getCursorPos();
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        if (ImGui.button("  ##OpenConsole", 25, 25)) {
            EditorUI.getInstance().getWindow(ProjectWindow.class).setVisible(false);
            EditorUI.getInstance().getWindow(ConsoleWindow.class).setVisible(true);
        }
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);
        EditorAssetManager.getInstance().getFont("Default25").setScale(0.7f);
        ImGui.pushFont(EditorAssetManager.getInstance().getFont("Default25"));
        ImGui.setCursorPos(cursor.x + 3, cursor.y + 5);

        if (ImGui.isItemActive()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.4f, 0.4f, 1.0f);
        } else if (ImGui.isItemHovered()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.3f, 0.6f, 0.8f, 0.8f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.6f, 0.6f, 0.6f, 1.0f);
        }
        ImGui.text(Icons.Terminal);
        ImGui.popStyleColor();
        EditorAssetManager.getInstance().getFont("Default25").setScale(1f);

        ImGui.popFont();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 3);


    }

    ////        if (!visible)
////            return;
////        ImGui.setNextWindowClass(windowClass);
////        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
////
////        ImGui.begin("Project", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
////        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10, 5);
////
////        ImGui.textDisabled("PROJECT");
////
////        ImGui.beginChild("##ProjectChild", -1, -1, true);
////
////
////        ImGui.endChild();
////        ImGui.popStyleVar();
////
////        ImGui.end();
////        ImGui.popStyleVar();
//
//    }



    @Override
    protected void body() {

        if (ProjectManager.getGame() != null) {
            renderProjectOpen();
        } else {
            renderProjectNotOpen();
        }
    }

    private void renderProjectOpen() {
        directoryExplorerWidget.render();
        if (ImGui.beginDragDropTarget()) {
            GameObject payload = ImGui.acceptDragDropPayload("GameObject");
            if (payload != null ) {
                String json = GameObjectFactory.generateJsonFromGameObject(payload);
                String file = directoryExplorerWidget.getPath() + "/" + payload.getName() + ".prefab";
                if (FileSystem.doesDirectoryExists(file)) {
                    Logger.log(Logger.Type.ERROR, "File already exists!");
                } else {
                    FileSystem.saveToFile(file, json);
                    directoryExplorerWidget.loadDirectory(directoryExplorerWidget.getPath());
                }            }
            ImGui.endDragDropTarget();
        }
    }

    private void renderProjectNotOpen() {
        ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 90);
        ImGui.setCursorPosY(ImGui.getWindowHeight() / 2);

        if (ImGui.button(" Open Project ")) {
            EditorUI.getInstance().getWindow(MainMenuBar.class).getActions().put(MenuAction.OPEN_PROJECT, true);
        }
    }
}
