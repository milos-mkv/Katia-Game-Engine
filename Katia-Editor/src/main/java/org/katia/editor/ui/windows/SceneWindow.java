package org.katia.editor.ui.windows;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Matrix4f;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.TransformComponent;
import org.katia.editor.EditorUI;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.renderer.EditorCameraController;
import org.katia.editor.renderer.EditorSceneRenderer;
import org.katia.editor.renderer.Settings;

import static org.katia.Math.map;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL30.*;

public class SceneWindow extends Window {

    private ImVec2 mouseLastPosition;
    int manipulationOperation;

    public SceneWindow() {
        super("Scene");
        Logger.log(Logger.Type.INFO, "Creating scene window ...");
        mouseLastPosition = new ImVec2(0, 0);
        manipulationOperation = Operation.TRANSLATE;
    }

    @Override
    public void render() {
//        if (true) {
//            return;
//        }
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
        ImGui.setNextWindowClass(windowClass);
        ImGui.begin("Scene");
        var startCursorPosition = ImGui.getCursorPos();

        Settings.w = ImGui.getWindowWidth();
        Settings.h = ImGui.getWindowHeight();
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 3, 3);

        ImGui.textDisabled("SCENE");
        ImGui.beginChild("##SceneChild", -1, -1, true, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        var viewportOffset = ImGui.getCursorPos();

        ImGui.image(EditorSceneRenderer.getInstance().getDefaultframeBuffer().getTexture(), ImGui.getWindowWidth() -6, ImGui.getWindowHeight() - 6, 0, 1, 1, 0);

        var windowSize = ImGui.getWindowSize();
        var minBound = ImGui.getWindowPos();

        minBound.x += viewportOffset.x;
        minBound.y += viewportOffset.y;

        var maxBound = new ImVec2(minBound.x + windowSize.x, minBound.y + windowSize.y);
        ImVec2[] vbounds = new ImVec2[2];
        vbounds[0] = new ImVec2(minBound.x, minBound.y);
        vbounds[1] = new ImVec2(maxBound.x, maxBound.y);

        var m = ImGui.getMousePos();
        m.x -= vbounds[0].x;
        m.y -= vbounds[1].y;
        m.y *= -1;

        float finalX = map(m.x, 0, vbounds[1].x - vbounds[0].x, 0, Settings.w);
        float finalY = map(m.y, 0, vbounds[1].y - vbounds[0].y, 0, Settings.h);

        if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
            glBindFramebuffer(GL_FRAMEBUFFER, EditorSceneRenderer.getInstance().getSelectFrameBuffer().getId());
            int[] i = new int[1];
            glReadPixels((int) finalX, (int) finalY, 1, 1, GL_RED_INTEGER, GL_INT, i);
             GameObject gameObject = ProjectManager.getGame().getSceneManager().getActiveScene().findBySelectID(i[0]);
             if (gameObject != null) {
                 EditorUI.getInstance().getWindow(InspectorWindow.class).setGameObject(gameObject);
             }
            glBindFramebuffer(GL_FRAMEBUFFER, 0);

        }

        ImVec2 currentMouseCursor = ImGui.getMousePos();


        if (ImGui.isWindowHovered() && ImGui.isMouseDragging(ImGuiMouseButton.Right)) {
            EditorCameraController.getInstance()
                    .move((mouseLastPosition.x -currentMouseCursor.x), ( currentMouseCursor.y - mouseLastPosition.y ));
        }

        if (ImGui.isWindowHovered() && ImGui.getIO().getMouseWheel() != 0.0f) {
            EditorCameraController.getInstance().zoom((float) (ImGui.getIO().getMouseWheel() * 0.1));
        }

        mouseLastPosition = currentMouseCursor;

        manipulate();
        ImGui.setCursorPos(startCursorPosition.x + 10, startCursorPosition.y + 10);
        renderManipulationToolbar();
        ImGui.endChild();
        ImGui.popStyleVar();

        ImGui.end();
        ImGui.popStyleVar();
    }

    @Override
    protected void body() {

    }

    private void manipulate() {

        GameObject gameObject = EditorUI.getInstance().getWindow(InspectorWindow.class).getGameObject().get();
        if (gameObject == null) {
            return;
        }

        ImGuizmo.setOrthographic(true);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());

        var engineCamera = EditorCameraController.getInstance();
        var view = matrixToFloatBuffer(engineCamera.getViewMatrix().invert());
        var proj = matrixToFloatBuffer(engineCamera.getProjectionMatrix());

        var t = gameObject.getComponent(TransformComponent.class);

        var transform = matrixToFloatBuffer(t.getWorldTransformMatrix());

        float rot = gameObject.getComponent(TransformComponent.class).getRotation();
        ImGuizmo.manipulate(view, proj, transform, manipulationOperation, Mode.WORLD);

        if (ImGuizmo.isUsing()) {
            t.setWorldTransformMatrix(new Matrix4f().set(transform));

            // FIXME: For some reason when using scale it resets rotation. Find why is that.
            if (manipulationOperation == Operation.SCALE) {
                gameObject.getComponent(TransformComponent.class).setRotation(rot);
            }
        }
    }


    private void renderManipulationToolbar() {
        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.1f, 0.1f, 0.1f, 0.5f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.1f, 0.1f, 0.5f);

        ImGui.beginChildFrame(2, 44, 110);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 4);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        int mode = manipulationOperation;

        if (mode == Operation.TRANSLATE) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 1.0f, 0.5f, 1.0f);
        }
        if (ImGui.button(Icons.Translate)) {
            manipulationOperation = Operation.TRANSLATE;
        }
        if (mode == Operation.TRANSLATE) {
            ImGui.popStyleColor();
        }
        if (mode == Operation.ROTATE_Z) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 1.0f, 0.5f, 1.0f);
        }
        if (ImGui.button(Icons.Rotate)) {
            manipulationOperation = Operation.ROTATE_Z;
        }
        if (mode == Operation.ROTATE_Z) {
            ImGui.popStyleColor();
        }

        if (mode == Operation.SCALE) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 1.0f, 0.5f, 1.0f);
        }
        if (ImGui.button(Icons.Scale)) {
            manipulationOperation = Operation.SCALE;
        }
        if (mode == Operation.SCALE) {
            ImGui.popStyleColor();
        }
        ImGui.popStyleVar();
        ImGui.endChildFrame();
        ImGui.popStyleColor(2);
    }
    public static float[] matrixToFloatBuffer(Matrix4f matrix) {
        var buffer = new float[16];
        matrix.get(buffer);
        return buffer;
    }

}
