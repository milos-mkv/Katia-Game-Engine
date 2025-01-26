package org.katia.editor.windows;

import imgui.*;
import imgui.flag.ImGuiCol;
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
import org.katia.core.components.*;
import org.katia.editor.EditorUtils;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.popups.SelectImagePopup;
import org.katia.factory.ComponentFactory;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.TextureFactory;
import org.katia.gfx.Texture;

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

        components = new LinkedHashMap<>();
        components.put("Transform", this::renderTransformComponent);
        components.put("Sprite", this::renderSpriteComponent);
        components.put("Script", this::renderScriptComponent);
        components.put("Camera", this::renderCameraComponent);
        components.put("Text", this::renderTextComponent);

        windowClass= new ImGuiWindowClass();
        windowClass.setDockNodeFlagsOverrideSet(
                ImGuiDockNodeFlags.NoDockingOverMe | ImGuiDockNodeFlags.NoDockingSplitMe | ImGuiDockNodeFlags.NoCloseButton | ImGuiDockNodeFlags.NoTabBar);

    }
    private void renderAddComponentContextMenu(GameObject gameObject) {
        if (ImGui.beginPopup("Add Component Menu")) {
            for (String componentType : components.keySet()) {
                boolean hasComponent = gameObject.getComponent(Component.components.get(componentType)) != null;

                if (componentType.equals("Transform")) {
                    ImGui.beginDisabled();
                }
                if (ImGui.menuItem(componentType, hasComponent ? Icons.Check : null)) {
                    if (hasComponent) {
                        gameObject.removeComponent(componentType);
                    } else {
                        gameObject.addComponent(Objects.requireNonNull(ComponentFactory.createComponent(componentType)));
                    }
                }
                if (componentType.equals("Transform")) {
                    ImGui.endDisabled();
                }
            }
            ImGui.endPopup();
        }
    }

    public void removeSelectedGameObjectEventCallback(Object object) {
        GameObject gameObject = (GameObject) object;
        if ((this.gameObject.get() == gameObject) || (gameObject.isChild(this.gameObject.get()))) {
            this.gameObject.clear();
        }
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = new WeakReference<>(gameObject);
    }

    private void renderTextComponent() {
        TextComponent textComponent = gameObject.get().getComponent(TextComponent.class);
        if (ImGui.collapsingHeader("Text Component")) {
            ImGui.columns(2);
            ImGui.text("Font");
            ImGui.nextColumn();
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##Font", new ImString(""));
            ImGui.nextColumn();
            ImGui.text("Text");
            ImGui.nextColumn();
            ImString textt = new ImString("");
            ImGui.inputTextMultiline("##Text", textt, -1, 70 );
            ImGui.nextColumn();
            ImGui.text("Color");
            ImGui.nextColumn();
            float[] colors = new float[] { 1.0f, 1.0f, 0.5f, 1.0f };
            ImGui.setNextItemWidth(-1);
            ImGui.colorEdit4("##Color", colors);
            ImGui.nextColumn();
            ImGui.text("Scale");
            ImGui.nextColumn();
            float[] scale = new float[] { 1.0f };
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##Scale", scale);
            ImGui.columns(1);
            ImGui.separator();
        }
    }

    public void renderCheckerboardWithImage(Texture texture, int textureWidth, int textureHeight, float displayWidth, float displayHeight) {
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

        if (texture != null) {
            ImGui.getWindowDrawList().addImage(texture.getId(), imageX, imageY, imageX + scaledWidth, imageY + scaledHeight);
            String text = texture.getWidth() + "x" + texture.getHeight();
            ImVec2 textSize = ImGui.calcTextSize(text);

            float adjustedX = startPos.x + displayWidth - textSize.x + 30.0f; // Subtract text width and margin
            float adjustedY = startPos.y + displayHeight - textSize.y + 10.0f; // Subtract text height and margin

            renderOutlinedText(drawList, text, adjustedX, adjustedY, ImColor.intToColor(255, 255, 255, 255), ImColor.intToColor(0, 0, 0, 255));
        }
    }
    public void renderOutlinedText(ImDrawList drawList, String text, float x, float y, int textColor, int outlineColor) {
        float outlineThickness = 2.0f; // Thickness of the outline

        // Adjust text position for centering
        ImVec2 textSize = ImGui.calcTextSize(text);
        float centerX = x - textSize.x / 2.0f;
        float centerY = y - textSize.y / 2.0f;

        // Draw the outline by rendering the text in 8 surrounding positions
        drawList.addText(centerX - outlineThickness, centerY, outlineColor, text); // Left
        drawList.addText(centerX + outlineThickness, centerY, outlineColor, text); // Right
        drawList.addText(centerX, centerY - outlineThickness, outlineColor, text); // Top
        drawList.addText(centerX, centerY + outlineThickness, outlineColor, text); // Bottom
        drawList.addText(centerX - outlineThickness, centerY - outlineThickness, outlineColor, text); // Top-left
        drawList.addText(centerX + outlineThickness, centerY - outlineThickness, outlineColor, text); // Top-right
        drawList.addText(centerX - outlineThickness, centerY + outlineThickness, outlineColor, text); // Bottom-left
        drawList.addText(centerX + outlineThickness, centerY + outlineThickness, outlineColor, text); // Bottom-right

        // Draw the main text in the center
        drawList.addText(centerX, centerY, textColor, text);
    }

    /**
     * Render sprite component items.
     */
    private void renderSpriteComponent() {
        SpriteComponent spriteComponent = Objects.requireNonNull(gameObject.get()).getComponent(SpriteComponent.class);
        if (ImGui.collapsingHeader("Sprite Component")) {
            ImGui.columns(2);
//            ImGui.button("     ", 90, 30);
            ImGui.text("Sprite");
            ImGui.nextColumn();
            ImString path = new ImString("");
            ImGui.setNextItemWidth(-30);
            ImGui.beginDisabled();
            ImGui.inputText("##TexturePath", path);
            ImGui.endDisabled();
            ImGui.sameLine();
            ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);

            var cursor = ImGui.getCursorPos();
            if (ImGui.button("  ")) {
//                SelectImagePopup.getInstance().open();

               String o = EditorUtils.openFileDialog();
               if (o != null) {
                   spriteComponent.setTexture(o);
//                   spriteComponent.setPath(o);
               }


            }
            ImGui.popStyleVar();
            ImGui.popStyleColor(3);

            if (ImGui.isItemActive()) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.4f, 0.4f, 1.0f);
            } else if (ImGui.isItemHovered()) {
                ImGui.pushStyleColor(ImGuiCol.Text, 0.3f, 0.6f, 0.8f, 0.8f);
            } else {
                ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.8f, 0.8f, 1.0f);
            }
            ImGui.sameLine();
            ImGui.setCursorPos(cursor.x + 1, cursor.y + 2);
            ImGui.text(Icons.Folder);
            ImGui.popStyleColor();

//            ImGui.popFont();
            ImGui.columns(1);

            Texture texture = spriteComponent.getTexture();
            int textureWidth = texture != null ? texture.getWidth() : 0;
            int textureHeight = texture != null ? texture.getHeight() : 0;
            renderCheckerboardWithImage(spriteComponent.getTexture(),
                    textureWidth, textureHeight, ImGui.getWindowWidth() - 20, 300);
            ImGui.separator();

        }
        SelectImagePopup.getInstance().render();

    }

    private void renderScriptComponent() {
        ScriptComponent scriptComponent = gameObject.get().getComponent(ScriptComponent.class);
        if (ImGui.collapsingHeader("Script Component")) {
            ImGui.columns(2);
            ImGui.text("Script");
            ImGui.nextColumn();
            ImString path = new ImString("");
            ImGui.setNextItemWidth(-50);
            ImGui.beginDisabled();
            ImGui.inputText("##TexturePath", path);
            ImGui.endDisabled();
            ImGui.sameLine();
            ImGui.button("ADD");
            ImGui.columns(1);
            ImGui.separator();
        }
    }

    private void renderCameraComponent() {
        CameraComponent cameraComponent = gameObject.get().getComponent(CameraComponent.class);
        if (ImGui.collapsingHeader("Camera Component")) {
            ImGui.columns(2);
            ImGui.text("Viewport");
            ImGui.nextColumn();
            float[] viewportX = new float[] { 1.0f };
            float[] viewportY = new float[] { 1.0f };
            ImGui.textColored(1.0f, 0.5f, 0.5f, 1.0f, " w ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##X", viewportX);

            ImGui.textColored(0.5f, 1.0f, 0.5f, 1.0f, " h ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##Y",viewportY);
            ImGui.nextColumn();
            ImGui.text("Color");
            ImGui.nextColumn();
            float[] color = new float[] { 1.0f, 1.0f, 1.0f };
            ImGui.setNextItemWidth(-1);
            ImGui.colorEdit3("##Color", color);
            ImGui.columns(1);
            ImGui.separator();
        }
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
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);

        ImGui.begin("Inspector" );

        ImGui.textDisabled("INSPECTOR");
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10, 5);

        ImGui.beginChild("##InspectorChild", -1, -1, true);

        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));
        if (gameObject != null && gameObject.get() != null) {
            GameObject go = gameObject.get();
            ImGui.columns(2);
            ImGui.setColumnWidth(-1, 100);
            ImGui.columns(1);
            ImGui.columns(2);
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
            ImGui.sameLine();
            if (ImGui.button(Icons.Plus)) {
                ImGui.openPopup("Add Component Menu");
            }
            for (Component component : go.getComponents().values()) {
                components.get(component.getComponentType()).run();
            }

            renderAddComponentContextMenu(gameObject.get());
        }
        ImGui.popFont();
        ImGui.endChild();
        ImGui.popStyleVar();
        ImGui.end();
        ImGui.popStyleVar();

    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of inspector window ...");
    }
}
