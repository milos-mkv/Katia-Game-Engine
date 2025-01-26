package org.katia.editor.windows;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;
import org.katia.Logger;
import org.katia.editor.popups.ErrorPopup;
import org.katia.editor.renderer.EditorCameraController;
import org.katia.editor.renderer.EditorSceneRenderer;
import org.katia.editor.renderer.Settings;
import org.katia.factory.TextureFactory;

public class SceneWindow implements UIComponent {
    ImGuiWindowClass windowClass;
    private ImVec2 mouseLastPosition;

    public SceneWindow() {
        mouseLastPosition = new ImVec2(0, 0);
        Logger.log(Logger.Type.INFO, "Creating scene window ...");
        windowClass= new ImGuiWindowClass();
        windowClass.setDockNodeFlagsOverrideSet(
                ImGuiDockNodeFlags.NoDockingOverMe | ImGuiDockNodeFlags.NoDockingSplitMe | ImGuiDockNodeFlags.NoCloseButton | ImGuiDockNodeFlags.NoTabBar);
    }

    @Override
    public void render() {
        ImGui.setNextWindowClass(windowClass);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);

        ImGui.begin("Scene");
        Settings.w = ImGui.getWindowWidth();
        Settings.h = ImGui.getWindowHeight();
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 3, 3);

        ImGui.textDisabled("SCENE");
        ImGui.beginChild("##SceneChild", -1, -1, true, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImGui.image(EditorSceneRenderer.getInstance().getFrameBuffer().getTexture(), ImGui.getWindowWidth() -6, ImGui.getWindowHeight() - 6, 0, 1, 1, 0);


        ImVec2 currentMouseCursor = ImGui.getMousePos();


        if (ImGui.isWindowHovered() && ImGui.isMouseDragging(ImGuiMouseButton.Right)) {
            EditorCameraController.getInstance()
                    .move((mouseLastPosition.x -currentMouseCursor.x), ( currentMouseCursor.y - mouseLastPosition.y ));
        }

        if (ImGui.isWindowHovered() && ImGui.getIO().getMouseWheel() != 0.0f) {
            EditorCameraController.getInstance().zoom((float) (ImGui.getIO().getMouseWheel() * 0.1));
        }

        mouseLastPosition = currentMouseCursor;

        ImGui.endChild();
        ImGui.popStyleVar();

        ImGui.end();
        ImGui.popStyleVar();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of scene window ...");

    }
}
