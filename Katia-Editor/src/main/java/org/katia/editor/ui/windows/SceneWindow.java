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
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.katia.FileSystem;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TransformComponent;
import org.katia.editor.EditorUI;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.EditorInputManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.renderer.EditorCameraController;
import org.katia.editor.renderer.EditorSceneRenderer;
import org.katia.editor.renderer.Settings;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.Arrays;

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
        windowFlags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;
    }

    @Override
    protected void header() {
        if (!ProjectManager.isPrefab) {
            ImGui.newLine();
            title = "SCENE";
            return;
        }
        title = "PREFAB";
        ImGui.textDisabled(" (" + ProjectManager.getGame().getSceneManager()
                .getActiveScene().getName() + ")");
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 30);
        var cursor = ImGui.getCursorPos();
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        if (ImGui.button("  ##OpenConsole", 25, 25)) {
            ProjectManager.closePrefab();
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
        ImGui.text(Icons.SquareX);
        ImGui.popStyleColor();
        EditorAssetManager.getInstance().getFont("Default25").setScale(1f);

        ImGui.popFont();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 3);
    }

    @Override
    protected void body() {
        if (ProjectManager.getGame() == null || ProjectManager.getGame().getSceneManager().getActiveScene() == null) {
            return;
        }

        Settings.w = ImGui.getWindowWidth();
        Settings.h = ImGui.getWindowHeight();
        var startCursorPosition = ImGui.getCursorPos();

        var viewportOffset = ImGui.getCursorPos();

        ImGui.image(EditorSceneRenderer.getInstance().getDefaultframeBuffer().getTexture(), ImGui.getWindowWidth() - 11, ImGui.getWindowHeight() - 9, 0, 1, 1, 0);

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

        // Mouse cursor in window
        float finalX = map(m.x, 0, vbounds[1].x - vbounds[0].x, 0, Settings.w);
        float finalY = map(m.y, 0, vbounds[1].y - vbounds[0].y, 0, Settings.h);

        Vector2f worldPos = EditorCameraController.getInstance().getCursorWorldPosition(finalX, finalY);

        if (ImGui.beginDragDropTarget()) {
            Path payload = ImGui.acceptDragDropPayload("ImageFile");
            if (payload != null) {
                GameObject gameObject = GameObjectFactory.createGameObjectWithComponent("Sprite");
                gameObject.getComponent(SpriteComponent.class).setPath(
                        FileSystem.relativize(ProjectManager.getGame().getDirectory(), payload.toString())
                );

                gameObject.getComponent(TransformComponent.class).setPosition(new Vector3f(worldPos.x, worldPos.y, 0));
                ProjectManager.getGame().getSceneManager().getActiveScene().addGameObject(gameObject);
                System.out.println(payload);
                EditorUI.getInstance().getWindow(InspectorWindow.class).setGameObject(gameObject);
//                textComponent.setPath(FileSystem.relativize(ProjectManager.getGame().getDirectory(), payload.toString()));
            }
            ImGui.endDragDropTarget();
        }
        if (ImGui.beginDragDropTarget()) {
            Object payload = ImGui.acceptDragDropPayload("Prefab");
            if (payload instanceof Path) {
                Scene scene = SceneFactory.generateSceneFromJson(
                        FileSystem.readFromFile(
                                payload.toString()));
                GameObject g = scene.getRootGameObject().getChildren().get(0);
                g.getComponent(TransformComponent.class).setPosition(new Vector3f(worldPos.x, worldPos.y, 0));
                ProjectManager.getGame().getSceneManager().getActiveScene().addGameObject(g);
            }
            ImGui.endDragDropTarget();
        }
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
                    .move((mouseLastPosition.x - currentMouseCursor.x), (currentMouseCursor.y - mouseLastPosition.y));
        }

        if (ImGui.isWindowHovered() && ImGui.getIO().getMouseWheel() != 0.0f) {
            EditorCameraController.getInstance().zoom((float) (ImGui.getIO().getMouseWheel() * 0.1));
        }

        mouseLastPosition = currentMouseCursor;

        manipulate();
        ImGui.setCursorPos(startCursorPosition.x + 10, startCursorPosition.y + 10);
        renderManipulationToolbar();

        if (EditorInputManager.getInstance().isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) &&
                EditorInputManager.getInstance().isKeyJustPressed(GLFW.GLFW_KEY_S)) {
            ProjectManager.saveCurrentScene();
        }
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
        var tt = t.getWorldTransformMatrix();
        var transform = matrixToFloatBuffer(tt);

        float rot = gameObject.getComponent(TransformComponent.class).getRotation();
        ImGuizmo.manipulate(view, proj, transform, manipulationOperation, Mode.WORLD);

        if (ImGuizmo.isUsing()) {
            t.setWorldTransformMatrix(new Matrix4f().set(transform));
            System.out.println(new Matrix4f().set(transform));
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
