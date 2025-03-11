package org.katia.editor.ui;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import lombok.Data;
import org.katia.Logger;
import org.katia.editor.EditorUI;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.ui.menubar.MainMenuBar;
import org.katia.editor.ui.menubar.MenuAction;
import org.katia.editor.ui.widgets.ProjectDirectoryExplorerWidget;

@Data
public class ProjectWindow extends UICoreDockWindow {

    ProjectDirectoryExplorerWidget directoryExplorerWidget;

    public ProjectWindow() {
        super("Project");
        Logger.log(Logger.Type.INFO, "Creating project window ...");

        directoryExplorerWidget = new ProjectDirectoryExplorerWidget();
        directoryExplorerWidget.setRootDirectory("C:\\Users\\milos\\Desktop\\Demo Game");
    }

    @Override
    public void render() {
        ImGui.setNextWindowClass(windowClass);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);

        ImGui.begin("Project", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10, 5);

        ImGui.textDisabled("PROJECT");

        ImGui.beginChild("##ProjectChild", -1, -1, true);


        if (ProjectManager.getGame() != null) {
            renderProjectOpen();
        } else {
            renderProjectNotOpen();
        }
        ImGui.endChild();
        ImGui.popStyleVar();

        ImGui.end();
        ImGui.popStyleVar();

    }


    @Override
    protected void body() {

    }

    private void renderProjectOpen() {
        directoryExplorerWidget.render();
    }

    private void renderProjectNotOpen() {
        ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 90);
        ImGui.setCursorPosY(ImGui.getWindowHeight() / 2);

        if (ImGui.button(" Open Project ")) {
            EditorUI.getInstance().get(MainMenuBar.class).getActions().put(MenuAction.OPEN_PROJECT, true);
        }
    }
}
