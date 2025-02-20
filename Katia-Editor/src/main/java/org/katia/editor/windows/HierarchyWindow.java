package org.katia.editor.windows;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;
import org.joml.Vector3f;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.TransformComponent;
import org.katia.editor.Editor;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.EditorSceneManager;
import org.katia.editor.renderer.EditorCameraController;
import org.katia.editor.renderer.EditorSceneRenderer;
import org.katia.factory.GameObjectFactory;

import java.util.UUID;

public class HierarchyWindow implements UIComponent {

    ImGuiWindowClass windowClass;
    GameObject gameObjectToMove;
    GameObject gameObjectToMoveTo;
    GameObject copyGameObject;
    GameObject gameObjectToDelete;
    int gameObjectToMoveNewIndex;
    int treeIndex;

    /**
     * Hierarchy window constructor.
     */
    public HierarchyWindow() {
        Logger.log(Logger.Type.INFO, "Creating hierarchy window ...");
        windowClass= new ImGuiWindowClass();
        windowClass.setDockNodeFlagsOverrideSet(
                ImGuiDockNodeFlags.NoDockingOverMe | ImGuiDockNodeFlags.NoDockingSplitMe | ImGuiDockNodeFlags.NoCloseButton | ImGuiDockNodeFlags.NoTabBar);
        gameObjectToMove = null;
        gameObjectToMoveTo = null;
        gameObjectToDelete = null;
        gameObjectToMoveNewIndex = 0;
        treeIndex = 0;
        copyGameObject = null;
    }

    /**
     * Render hierarchy window.
     */
    @Override
    public void render() {
        ImGui.setNextWindowClass(windowClass);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
        ImGui.begin("Hierarchy");

        ImGui.image(EditorSceneRenderer.getInstance().getIdFrameBuffer().getTexture(), 800, 600, 0, 1, 1, 0);

        ImGui.textDisabled("HIERARCHY");
        ImGui.beginChild("##HierarchyChild", -1, -1, true);
        displayWindowContextMenu();

        Scene scene = EditorSceneManager.getInstance().getScene();
        if (scene != null) {
            treeIndex = 0;
            ImGui.textDisabled(" " + scene.getName());
            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));
            int index = 0;
            for (GameObject gameObject : scene.getRootGameObject().getChildren()) {
                ImGui.separator();
                setDragTargetForGameObjectReorder(index, scene.getRootGameObject());
                displayGameObject(gameObject);
                index++;
            }
            ImGui.popFont();
        }

        ImGui.endChild();
        process();
        ImGui.end();
        ImGui.popStyleVar();
    }

    /**
     * Display game object.
     * @param gameObject Game Object.
     */
    private void displayGameObject(GameObject gameObject) {
        ImGui.pushID(gameObject.getId().toString());
        boolean isEvenRow = (++treeIndex) % 2 == 0;
        if (isEvenRow) {
            ImVec2 cursorPos = ImGui.getCursorScreenPos();
            ImVec2 availableSize = ImGui.getContentRegionAvail();
            ImGui.getWindowDrawList().addRectFilled(0, cursorPos.y - 5,
                    cursorPos.x + availableSize.x + 3, cursorPos.y + ImGui.getTextLineHeightWithSpacing(),
                    ImGui.getColorU32(0.1f, 0.1f, 0.1f, 0.3f));
        }
        int flag = (ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.OpenOnArrow);
        boolean open = ImGui.treeNodeEx(Icons.Cube + " " + gameObject.getName(), flag);

        // If mouse double click move camera to game object.
        if (ImGui.isItemHovered(ImGuiHoveredFlags.None)) {
            if (ImGui.isMouseDoubleClicked(0)) {
                var pos = gameObject.getComponent(TransformComponent.class).getPosition();
                EditorCameraController.getInstance().getCamera().getComponent(TransformComponent.class).setPosition(new Vector3f(pos.x, pos.y, 0));
            }
        }

        // If item was clicked display it in inspector window.
        if (ImGui.isItemClicked()) {
            Editor.getInstance().getUiRenderer().get(InspectorWindow.class).setGameObject(gameObject);
        }

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("GameObject", gameObject);
            ImGui.text(gameObject.getName());
            ImGui.endDragDropSource();
        }

        // NOTE: Moving object directly to another game object its just addition to list.
        setDragTargetForGameObjectReorder(gameObject.getChildren().size(), gameObject);
        if (open) {
            displayGameObjectContextMenu(gameObject);
            int childIndex = 0;
            for (GameObject childGameObject : gameObject.getChildren()) {
                ImGui.separator();
                setDragTargetForGameObjectReorder(childIndex, gameObject);
                displayGameObject(childGameObject);
                childIndex++;
            }
            ImGui.treePop();
        } else {
            displayGameObjectContextMenu(gameObject);
        }


        ImGui.popID();
    }

    /**
     * Set drag target for moving game objects.
     * @param index Index.
     * @param gameObject Game Object.
     */
    private void setDragTargetForGameObjectReorder(int index, GameObject gameObject) {
        if (ImGui.beginDragDropTarget()) {
            GameObject payload = ImGui.acceptDragDropPayload("GameObject");
            if (payload != null && payload != gameObject) {
                gameObjectToMoveTo = gameObject;
                gameObjectToMove = payload;
                gameObjectToMoveNewIndex = index;
            }
            ImGui.endDragDropTarget();
        }
    }

    /**
     * Process game object operations in window.
     */
    private void process() {
        if (gameObjectToMove != null) {
            if (gameObjectToMove.getParent().get() == gameObjectToMoveTo) {
                attachGameObjectToSameParent();
            } else {
                attachGameObjectToNewParent();
            }
        }

        if (gameObjectToDelete != null) {
            gameObjectToDelete.removeFromParent();
            Editor.getInstance().getUiRenderer().get(InspectorWindow.class).removeSelectedGameObjectEventCallback(gameObjectToDelete);
            gameObjectToDelete = null;
        }

        gameObjectToMoveTo = null;
        gameObjectToMove = null;
        gameObjectToMoveNewIndex = 0;
    }

    /**
     * Attach game object to new parent.
     */
    private void attachGameObjectToNewParent() {
        gameObjectToMoveTo.addChild(gameObjectToMove, gameObjectToMoveNewIndex);
    }

    /**
     * Attach game object to same parent / do reorder.
     */
    private void attachGameObjectToSameParent() {
        int index = gameObjectToMoveTo.getChildIndex(gameObjectToMove);
        if (gameObjectToMoveNewIndex == index || gameObjectToMoveNewIndex == index + 1) {
            return;
        }
        gameObjectToMoveTo.getChildren().set(index, null);
        gameObjectToMoveTo.addChild(gameObjectToMove, gameObjectToMoveNewIndex);
        System.out.println(gameObjectToMoveNewIndex);
        gameObjectToMoveTo.removeChild(null);
    }

    /**
     * Display context menu for hierarchy window.
     */
    private void displayWindowContextMenu() {
        Scene scene = EditorSceneManager.getInstance().getScene();
        if (ImGui.beginPopupContextWindow()) {
            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));
            String component = null;
            if (ImGui.menuItem(" New Game Object")) {
                scene.addGameObject(GameObjectFactory.createGameObject());
            }
            if (ImGui.menuItem(" New Sprite")) { component = "Sprite"; }
            if (ImGui.menuItem(" New Camera")) { component = "Camera"; }
            if (ImGui.menuItem(" New Script")) { component = "Script"; }
            if (ImGui.menuItem(" New Text"))   { component = "Text";   }
            if (component != null) {
                scene.addGameObject(GameObjectFactory.createGameObjectWithComponent(component));
            }
            ImGui.separator();
            if (copyGameObject == null) {
                ImGui.beginDisabled();
            }
            if (ImGui.menuItem(" Paste") && copyGameObject != null) {
                scene.addGameObject(GameObjectFactory.copy(copyGameObject));
            }
            if (copyGameObject == null) {
                ImGui.endDisabled();
            }
            ImGui.popFont();
            ImGui.endPopup();
        }
    }

    /**
     * Display context menu for game object.
     * @param gameObject Game Object.
     */
    private void displayGameObjectContextMenu(GameObject gameObject) {
        if (ImGui.beginPopupContextItem()) {
            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));

            String component = null;
            if (ImGui.menuItem(" New Game Object")) {
                gameObject.addChild(GameObjectFactory.createGameObject());
            }
            if (ImGui.menuItem(" New Sprite")) { component = "Sprite"; }
            if (ImGui.menuItem(" New Camera")) { component = "Camera"; }
            if (ImGui.menuItem(" New Script")) { component = "Script"; }
            if (ImGui.menuItem(" New Text"))   { component = "Text";   }
            if (component != null) {
                gameObject.addChild(GameObjectFactory.createGameObjectWithComponent(component));
            }
            ImGui.separator();
            if (ImGui.menuItem(" Copy")) {
                copyGameObject = gameObject;
            }
            if (copyGameObject == null) {
                ImGui.beginDisabled();
            }
            if (ImGui.menuItem(" Paste") && copyGameObject != null) {
                gameObject.addChild(GameObjectFactory.copy(copyGameObject));
            }
            if (copyGameObject == null) {
                ImGui.endDisabled();
            }
            if (ImGui.menuItem(" Delete")) {
                gameObjectToDelete = gameObject;
            }
            ImGui.popFont();
            ImGui.endPopup();
        }
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of hierarchy window ...");
    }
}
