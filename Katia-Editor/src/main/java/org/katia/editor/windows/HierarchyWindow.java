package org.katia.editor.windows;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
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
import org.katia.factory.GameObjectFactory;

public class HierarchyWindow implements UIComponent {
    ImGuiWindowClass windowClass;
    private GameObject gameObjectToMove;
    private GameObject gameObjectToMoveTo;
    private int gameObjectToMoveNewIndex;
    private int treeIndex;

    private GameObject gameObjectToDelete;
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
    }

    @Override
    public void render() {
        ImGui.setNextWindowClass(windowClass);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
        ImGui.begin("Hierarchy");

        ImGui.textDisabled("HIERARCHY");
        ImGui.beginChild("##HierarchyChild", -1, -1, true);
        displayWindowContextMenu();

        Scene scene = EditorSceneManager.getInstance().getScene();
        if (scene != null) {
            treeIndex = 0;
            ImGui.text(scene.getName());
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

    private void displayGameObject(GameObject gameObject) {
        treeIndex++;
        ImGui.pushID(gameObject.getId().toString());
        boolean isEvenRow = treeIndex % 2 == 0;

        if (isEvenRow) {
            ImVec2 cursorPos = ImGui.getCursorScreenPos();
            ImVec2 availableSize = ImGui.getContentRegionAvail();
            ImGui.getWindowDrawList().addRectFilled(0, cursorPos.y - 5,
                    cursorPos.x + availableSize.x + 3, cursorPos.y + ImGui.getTextLineHeightWithSpacing(),
                    ImGui.getColorU32(0.1f, 0.1f, 0.1f, 0.3f)); // Dark gray color
        }
        int flag = (ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.OpenOnArrow);
        boolean open = ImGui.treeNodeEx(Icons.Cube + " " + gameObject.getName(), flag);

        if (ImGui.isItemHovered(ImGuiHoveredFlags.None)) {
            if (ImGui.isMouseDoubleClicked(0)) {
                EditorCameraController.getInstance().getCamera().getComponent(TransformComponent.class).setPosition(
                       new Vector3f(
                               gameObject.getComponent(TransformComponent.class).getPosition().x,
                               gameObject.getComponent(TransformComponent.class).getPosition().y,
                               0
                       )
                );
//                EngineCameraController
//                        .getInstance().getTransform().getPosition().x = gameObject.getComponent(TransformComponent.class).getPosition().x;
//                EngineCameraController
//                        .getInstance().getTransform().getPosition().y = gameObject.getComponent(TransformComponent.class).getPosition().y;
            }
        }
        if (ImGui.isItemClicked()) {
            System.out.println(gameObject);
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

//            EngineEventManager.dispatch("GameObjectDeleted", gameObjectToDelete);
            gameObjectToDelete = null;
        }

        gameObjectToMoveTo = null;
        gameObjectToMove = null;
        gameObjectToMoveNewIndex = 0;
    }

    private void attachGameObjectToNewParent() {
        gameObjectToMoveTo.addChild(gameObjectToMove, gameObjectToMoveNewIndex);
    }

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

    private void displayWindowContextMenu() {
        Scene scene = EditorSceneManager.getInstance().getScene();
        if (ImGui.beginPopupContextWindow()) {
            if (ImGui.menuItem("New Game Object")) {
                scene.addGameObject(GameObjectFactory.createGameObject());
            }
            ImGui.endPopup();
        }
    }

    private void displayGameObjectContextMenu(GameObject gameObject) {
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("Delete")) {
                gameObjectToDelete = gameObject;
            }
            ImGui.endPopup();
        }
    }


    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of hierarchy window ...");
    }

}
