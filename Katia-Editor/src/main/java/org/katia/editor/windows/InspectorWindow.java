package org.katia.editor.windows;

import imgui.ImGui;
import imgui.type.ImBoolean;
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
            Vector3f position = transformComponent.getPosition();
            float[] posBuffer = new float[] { position.x, position.y, position.z };
            ImGui.dragFloat3("Position", posBuffer, 1f);
            transformComponent.getPosition().set(posBuffer);

            float[] rotBuffer = new float[] { transformComponent.getRotation() };
            ImGui.dragFloat("Rotation", rotBuffer, 0.01f);
            transformComponent.setRotation(rotBuffer[0]);
            Vector3f scale = transformComponent.getScale();

            float[] scaleBuffer = new float[] { scale.x, scale.y, scale.z };
            ImGui.dragFloat3("Scale", scaleBuffer, 1f);
            transformComponent.getScale().set(scaleBuffer);

        }
    }

    @Override
    public void render() {
        ImGui.begin("Inspector");
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
        ImGui.end();
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of inspector window ...");
    }
}
