package org.katia.editor.windows;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.flag.ImGuiDockNodeFlags;
import lombok.Data;
import org.katia.Logger;
import org.katia.editor.Editor;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.menubar.MainMenuBar;
import org.katia.editor.menubar.MenuAction;
import org.katia.editor.widgets.DirectoryExplorerWidget;

@Data
public class ProjectWindow implements UIComponent {

    ProjectManager pm;
    DirectoryExplorerWidget directoryExplorerWidget;
    ImGuiWindowClass windowClass;
    public ProjectWindow() {
        Logger.log(Logger.Type.INFO, "Creating project window ...");
        pm = ProjectManager.getInstance();
        directoryExplorerWidget = new DirectoryExplorerWidget();
//        directoryExplorerWidget.setRootDirectory("C:\\Users\\milos\\Documents\\GitHub\\Katia-Game-Engine");
        windowClass= new ImGuiWindowClass();
        windowClass.setDockNodeFlagsOverrideSet(
                ImGuiDockNodeFlags.NoDockingOverMe | ImGuiDockNodeFlags.NoDockingSplitMe | ImGuiDockNodeFlags.NoCloseButton | ImGuiDockNodeFlags.NoTabBar);

    }

    @Override
    public void render() {
        ImGui.setNextWindowClass(windowClass);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);

        ImGui.begin("Project");
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10, 5);

        ImGui.textDisabled("PROJECT");
        ImGui.beginChild("##ProjectChild", -1, -1, true);


        if (pm.isActive()) {
            renderProjectOpen();
        } else {
            renderProjectNotOpen();
        }
        ImGui.endChild();
        ImGui.popStyleVar();

        ImGui.end();
        ImGui.popStyleVar();

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
