package org.katia.editor.windows;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;
import org.joml.Matrix4f;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.TransformComponent;
import org.katia.editor.Editor;
import org.katia.editor.managers.EditorInputManager;
import org.katia.editor.managers.EditorSceneManager;
import org.katia.editor.popups.ErrorPopup;
import org.katia.editor.renderer.EditorCameraController;
import org.katia.editor.renderer.EditorSceneRenderer;
import org.katia.editor.renderer.Settings;
import org.katia.factory.TextureFactory;
import org.lwjgl.glfw.GLFW;

import static org.katia.Math.map;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL30.*;

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

        float finalX = map(m.x, 0, vbounds[1].x - vbounds[0].x, 0, 1920);
        float finalY = map(m.y, 0, vbounds[1].y - vbounds[0].y, 0, 1080);

        if (EditorInputManager.getInstance().isMouseButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            glBindFramebuffer(GL_FRAMEBUFFER, EditorSceneRenderer.getInstance().getSelectFrameBuffer().getId());
            int[] i = new int[1];
            glReadPixels((int) finalX, (int) finalY, 1, 1, GL_RED_INTEGER, GL_INT, i);
             GameObject gameObject = EditorSceneManager.getInstance().getScene().findBySelectID(i[0]);
             if (gameObject != null) {
                 Editor.getInstance().getUiRenderer().get(InspectorWindow.class).setGameObject(gameObject);
             }
            glBindFramebuffer(GL_FRAMEBUFFER, 0);

        }
//        if (GLFW.glfwGetMouseButton(Editor.getInstance().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS &&
//                GLFW.glfwGetKey(Editor.getInstance().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS) {
//            for (Map.Entry<String, Model> mas : scene.getModels().entrySet()) {
//                if (mas.getValue().getId() == i[0]) {
//                    Scene.getInstance().setSelectedModel(mas.getKey());
//                    break;
//                }
//            }
//        }
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

        ImGui.endChild();
        ImGui.popStyleVar();

        ImGui.end();
        ImGui.popStyleVar();
    }

    private void manipulate() {

        GameObject gameObject = Editor.getInstance().getUiRenderer().get(InspectorWindow.class).getGameObject().get();
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
        ImGuizmo.manipulate(view, proj, transform, Operation.TRANSLATE, Mode.WORLD);

        if (ImGuizmo.isUsing()) {
            t.setTransformFromWorldMatrix(new Matrix4f().set(transform));

            // FIXME: For some reason when using scale it resets rotation. Find why is that.
//            if (manipulationOperation == Operation.SCALE) {
//                gameObject.getComponent(TransformComponent.class).setRotation(rot);
//            }
        }
    }

    public static float[] matrixToFloatBuffer(Matrix4f matrix) {
        var buffer = new float[16];
        matrix.get(buffer);
        return buffer;
    }
    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of scene window ...");

    }
}
