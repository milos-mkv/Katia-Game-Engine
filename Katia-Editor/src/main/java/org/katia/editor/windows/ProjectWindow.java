package org.katia.editor.windows;

import imgui.ImGui;
import org.katia.Logger;
import org.katia.editor.Editor;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.menubar.MainMenuBar;
import org.katia.editor.menubar.MenuAction;
import org.katia.editor.widgets.DirectoryExplorerWidget;

public class ProjectWindow implements UIComponent {

    ProjectManager pm;
    DirectoryExplorerWidget directoryExplorerWidget;

    public ProjectWindow() {
        Logger.log(Logger.Type.INFO, "Creating project window ...");
        pm = ProjectManager.getInstance();
        directoryExplorerWidget = new DirectoryExplorerWidget();
        directoryExplorerWidget.setRootDirectory("C:\\Users\\milos\\Documents\\GitHub\\Katia-Game-Engine");
    }

    @Override
    public void render() {
        ImGui.begin("Project");
        if (!pm.isActive()) {
            renderProjectOpen();
        } else {
            renderProjectNotOpen();
        }
        ImGui.end();
    }

    private void renderProjectOpen() {
        directoryExplorerWidget.render();
    }

    private void renderProjectNotOpen() {
        ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 90);
        ImGui.setCursorPosY(ImGui.getWindowHeight() / 2);

        if (ImGui.button(" Open Project ")) {
            Editor.getInstance().getUiRenderer().get(MainMenuBar.class).getActions().put(MenuAction.OPEN_PROJECT, true);
        }
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of project window ...");

    }
}
