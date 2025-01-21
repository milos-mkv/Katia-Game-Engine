package org.katia.editor.windows;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImString;
import lombok.Data;
import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.Component;
import org.katia.core.components.TransformComponent;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.factory.GameObjectFactory;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class InspectorWindow implements UIComponent {

    WeakReference<GameObject> gameObject;
    GameObject gameObject1;
     Map<String, Runnable> components;
     ImGuiWindowClass windowClass;
    public InspectorWindow() {
        Logger.log(Logger.Type.INFO, "Creating inspector window ...");
        gameObject = new WeakReference<GameObject>(null);
        gameObject1 = GameObjectFactory.createGameObject("Test 1");
        gameObject = new WeakReference<>(gameObject1);
        components = new LinkedHashMap<>();
        components.put("Transform", this::renderTransformComponent);
        components.put("Sprite", this::renderSpriteComponent);
        components.put("Script", this::renderScriptComponent);
        components.put("Camera", this::renderCameraComponent);

        windowClass= new ImGuiWindowClass();
        windowClass.setDockNodeFlagsOverrideSet(
                ImGuiDockNodeFlags.NoDockingOverMe | ImGuiDockNodeFlags.NoDockingSplitMe | ImGuiDockNodeFlags.NoCloseButton | ImGuiDockNodeFlags.NoTabBar);

    }

    private void renderSpriteComponent() {
    }

    private void renderScriptComponent() {
        
    }

    private void renderCameraComponent() {
    }

    private void renderTransformComponent() {
        TransformComponent transformComponent = gameObject.get().getComponent(TransformComponent.class);
        if (ImGui.collapsingHeader("Transform Component")) {
            ImGui.columns(2);

            float[] positionX = new float[] { transformComponent.getPosition().x };
            float[] positionY = new float[] { transformComponent.getPosition().y };
            ImGui.text("Position");
            ImGui.nextColumn();
            ImGui.button(" X ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##X", positionX);

            ImGui.button(" Y ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##Y",positionY);

            transformComponent.setPosition(new Vector3f(positionX[0], positionY[0], 0));

            ImGui.nextColumn();
            float[] rot = new float[] { transformComponent.getRotation() };

            ImGui.text("Rotation");
            ImGui.nextColumn();
            ImGui.button(" R ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##R",rot);
            transformComponent.setRotation(rot[0]);

            ImGui.nextColumn();
            ImGui.text("Scale");
            ImGui.nextColumn();
            ImFloat w = new ImFloat(transformComponent.getScale().x);
            ImGui.button(" W ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.inputFloat("##W",w);

            ImFloat h = new ImFloat(transformComponent.getScale().y);
            ImGui.button(" H ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.inputFloat("##H",h);


//            Vector3f position = transformComponent.getPosition();
//            float[] posBuffer = new float[] { position.x, position.y, position.z };
//            ImGui.dragFloat3("Position", posBuffer, 1f);
//            transformComponent.getPosition().set(posBuffer);
//
//            float[] rotBuffer = new float[] { transformComponent.getRotation() };
//            ImGui.dragFloat("Rotation", rotBuffer, 0.01f);
//            transformComponent.setRotation(rotBuffer[0]);
//            Vector3f scale = transformComponent.getScale();
//
//            float[] scaleBuffer = new float[] { scale.x, scale.y, scale.z };
//            ImGui.dragFloat3("Scale", scaleBuffer, 1f);
//            transformComponent.getScale().set(scaleBuffer);

        }
    }

    @Override
    public void render() {
        ImGui.setNextWindowClass(windowClass);
        ImGui.begin("Inspector" );
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10, 5);

        ImGui.beginChild("##InspectorChild", -1, -1, true);
        ImGui.textDisabled("INSPECTOR");
        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));
        if (gameObject != null && gameObject.get() != null) {
            GameObject go = gameObject.get();
            ImGui.columns(2);
            ImGui.setColumnWidth(-1, 100);
            ImGui.text("ID");
            ImGui.nextColumn();
            ImString id = new ImString(Objects.requireNonNull(go).getId().toString());
            ImGui.beginDisabled();
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##ID", id);
            ImGui.endDisabled();
            ImGui.nextColumn();

            ImString name = new ImString(go.getName(), 255);
            ImGui.text("Name");
            ImGui.nextColumn();
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##Name", name);
            go.setName(name.toString());
            ImGui.nextColumn();
            ImBoolean active = new ImBoolean(go.isActive());
            ImGui.text("Active");
            ImGui.nextColumn();
            ImGui.checkbox("##Active", active);

            go.setActive(active.get());
            ImGui.columns(1);
            ImGui.separator();
            ImGui.text("Components");

            for (Component component : go.getComponents().values()) {
                components.get(component.getComponentType()).run();
            }
        }
        ImGui.popFont();
        ImGui.endChild();
        ImGui.popStyleVar();
        ImGui.end();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of inspector window ...");
    }
}
