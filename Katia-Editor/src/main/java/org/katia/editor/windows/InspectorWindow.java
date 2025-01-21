package org.katia.editor.windows;

import imgui.*;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImString;
import lombok.Data;
import org.joml.Vector3f;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.Component;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TransformComponent;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.TextureFactory;

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
        SpriteComponent spriteComponent = new SpriteComponent();
        spriteComponent.setTexture("C:\\Users\\milos\\Documents\\GitHub\\Katia-Game-Engine\\Katia-Editor\\src\\main\\resources\\images\\logo.png");
        gameObject1.addComponent(spriteComponent);
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
    public void renderCheckerboardWithImage(int textureId, int textureWidth, int textureHeight, float displayWidth, float displayHeight) {
        // Get ImGui's draw list to draw the checkerboard
        ImDrawList drawList = ImGui.getWindowDrawList();

        // Get the top-left corner of the available space in the current ImGui window
        ImVec2 startPos = ImGui.getCursorScreenPos();

        // Checkerboard settings
        float checkerSize = 16.0f; // Size of each checkerboard square
        int numColumns = (int) Math.ceil(displayWidth / checkerSize);
        int numRows = (int) Math.ceil(displayHeight / checkerSize);

        // Draw the checkerboard pattern
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                // Alternate colors
                boolean isDark = (row + col) % 2 == 0;
                int color = isDark ? ImColor.intToColor(50, 50, 50, 255) : ImColor.intToColor(100, 100, 100, 255);

                float x0 = startPos.x + col * checkerSize;
                float y0 = startPos.y + row * checkerSize;
                float x1 = x0 + checkerSize;
                float y1 = y0 + checkerSize;

                drawList.addRectFilled(x0, y0, x1, y1, color);
            }
        }

        // Reserve space for the checkerboard background
        ImGui.dummy(displayWidth, displayHeight);
        // Draw the border around the checkerboard
        int borderColor = ImColor.intToColor(100, 100, 100, 255); // Light gray border color
        float borderThickness = 2.0f; // Thickness of the border
        drawList.addRect(startPos.x, startPos.y, startPos.x + displayWidth + 5, startPos.y + displayHeight + 5, borderColor, 0.0f, 0, borderThickness);

        // Calculate the image's aspect ratio
        float aspectRatio = (float) textureWidth / textureHeight;

        // Scale the image to fit within the display area while maintaining the aspect ratio
        float scaledWidth = displayWidth;
        float scaledHeight = displayWidth / aspectRatio;

        if (scaledHeight > displayHeight) {
            scaledHeight = displayHeight;
            scaledWidth = displayHeight * aspectRatio;
        }

        // Center the image within the checkerboard
        float imageX = startPos.x + (displayWidth - scaledWidth) / 2.0f;
        float imageY = startPos.y + (displayHeight - scaledHeight) / 2.0f;

        // Render the image on top of the checkerboard
        ImGui.getWindowDrawList().addImage(textureId, imageX, imageY, imageX + scaledWidth, imageY + scaledHeight);
    }

    /**
     * Render sprite component items.
     */
    private void renderSpriteComponent() {
        SpriteComponent spriteComponent = gameObject.get().getComponent(SpriteComponent.class);
        if (ImGui.collapsingHeader("Sprite Component")) {
            int textureWidth = spriteComponent.getTexture().getWidth();
            int textureHeight = spriteComponent.getTexture().getHeight();
            renderCheckerboardWithImage(spriteComponent.getTexture().getId(),
                    textureWidth, textureHeight, ImGui.getWindowWidth() - 20, 300);
            // Render the image
//            ImGui.image(spriteComponent.getTexture().getId(), scaledWidth, scaledHeight);
        }
    }

    private void renderScriptComponent() {
        
    }

    private void renderCameraComponent() {
    }

    /**
     * Render transform component items.
     */
    private void renderTransformComponent() {
        TransformComponent transformComponent = gameObject.get().getComponent(TransformComponent.class);
        if (ImGui.collapsingHeader("Transform Component")) {
            ImGui.columns(2);

            float[] positionX = new float[] { transformComponent.getPosition().x };
            float[] positionY = new float[] { transformComponent.getPosition().y };
            ImGui.text("Position");
            ImGui.nextColumn();
            ImGui.textColored(1.0f, 0.5f, 0.5f, 1.0f, " x ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##X", positionX);

            ImGui.textColored(0.5f, 1.0f, 0.5f, 1.0f, " y ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##Y",positionY);

            transformComponent.setPosition(new Vector3f(positionX[0], positionY[0], 0));

            ImGui.nextColumn();
            float[] rot = new float[] { transformComponent.getRotation() };

            ImGui.text("Rotation");
            ImGui.nextColumn();
            ImGui.text("   ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##R",rot);
            transformComponent.setRotation(rot[0]);

            float[] scaleX = new float[] { transformComponent.getScale().x };
            float[] scaleY = new float[] { transformComponent.getScale().y };
            ImGui.nextColumn();
            ImGui.text("Scale");
            ImGui.nextColumn();
            ImGui.textColored(1.0f, 0.5f, 0.5f, 1.0f, " x ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##W",scaleX);

            ImGui.textColored(0.5f, 1.0f, 0.5f, 1.0f, " y ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##H",scaleY);
            transformComponent.setScale(new Vector3f(scaleX[0], scaleY[0], 1));
            ImGui.columns(1);
            ImGui.separator();
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
