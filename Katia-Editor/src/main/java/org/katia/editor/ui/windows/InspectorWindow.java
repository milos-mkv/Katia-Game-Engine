package org.katia.editor.ui.windows;

import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import lombok.Data;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.katia.FileSystem;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.*;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.factory.ComponentFactory;
import org.katia.gfx.resources.Texture;

import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class InspectorWindow extends Window {

    WeakReference<GameObject> gameObject;
    Map<String, Runnable> components;
    String locked = Icons.Unlock;

    public InspectorWindow() {
        super("Inspector");
        Logger.log(Logger.Type.INFO, "Creating inspector window ...");
        gameObject = new WeakReference<GameObject>(null);
        components = new LinkedHashMap<>();
        components.put("Transform", this::renderTransformComponent);
        components.put("Sprite", this::renderSpriteComponent);
        components.put("Script", this::renderScriptComponent);
        components.put("Camera", this::renderCameraComponent);
        components.put("Text", this::renderTextComponent);
    }

    private void renderAddComponentContextMenu(GameObject gameObject) {
        if (ImGui.beginPopup("Add Component Menu")) {
            for (String componentType : components.keySet()) {
                boolean hasComponent = gameObject.getComponent(ComponentFactory.getComponentClass(componentType)) != null;

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

    /**
     * Remove selected game object.
     * @param gameObject Game Object.
     */
    public void removeSelectedGameObject(GameObject gameObject) {
        if ((this.gameObject.get() == gameObject) || (gameObject.isChild(this.gameObject.get()))) {
            this.gameObject.clear();
        }
    }

    /**
     * Set selected game object.
     * @param gameObject Game Object.
     */
    public void setGameObject(GameObject gameObject) {
        if (locked == Icons.Unlock) {
            this.gameObject = new WeakReference<>(gameObject);
        }
    }

    /**
     * Render text component data.
     */
    private void renderTextComponent() {
        TextComponent textComponent = Objects.requireNonNull(gameObject.get()).getComponent(TextComponent.class);
        if (ImGui.collapsingHeader("Text Component")) {
            ImGui.columns(2);
            ImGui.text("Font");
            ImGui.nextColumn();
            ImGui.setNextItemWidth(-1);
            ImGui.beginDisabled();
            // [ 1 ] Font Path
            ImGui.inputText("##FontPath", new ImString(textComponent.getPath()));
            if (ImGui.beginDragDropTarget()) {
                Path payload = ImGui.acceptDragDropPayload("FontFile");
                if (payload != null ) {
                    textComponent.setPath(FileSystem.relativize(ProjectManager.getGame().getDirectory(), payload.toString()));
                }
                ImGui.endDragDropTarget();
            }
            ImGui.endDisabled();
            ImGui.nextColumn();
            ImGui.text("Text");
            ImGui.nextColumn();
            ImString textBuffer = new ImString();
            textBuffer.set(textComponent.getText());
            // [ 2 ] Text data
            ImGui.inputTextMultiline("##TextData", textBuffer, -1, 70);
            ImGui.nextColumn();
            textComponent.setText(textBuffer.toString());

            ImGui.text("Color");
            ImGui.nextColumn();
            Vector4f textColor = textComponent.getColor();
            float[] colors = new float[] { textColor.x, textColor.y, textColor.z, textColor.w };
            ImGui.setNextItemWidth(-1);
            // [ 3 ] Text color
            ImGui.colorEdit4("##Color", colors);
            textComponent.setColor(new Vector4f(colors[0], colors[1], colors[2], colors[3]));
            ImGui.nextColumn();

            ImGui.text("Scale");
            ImGui.nextColumn();
            float[] scale = new float[] { textComponent.getScale() };
            ImGui.setNextItemWidth(-1);
            // [ 4 ] Text scale
            ImGui.dragFloat("##Scale", scale, 0.01f);
            textComponent.setScale(scale[0]);
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
        int numColumns = (int) Math.ceil(displayWidth + 10 / checkerSize) ;
        int numRows = (int) Math.ceil(displayHeight / checkerSize) ;

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
        drawList.addRect(startPos.x, startPos.y, startPos.x + displayWidth + 12, startPos.y + displayHeight + 5, borderColor, 0.0f, 0, borderThickness);

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
     * Render sprite component data.
     */
    private void renderSpriteComponent() {
        SpriteComponent spriteComponent = Objects.requireNonNull(gameObject.get()).getComponent(SpriteComponent.class);
        if (ImGui.collapsingHeader("Sprite Component")) {
            ImGui.columns(2);
            ImGui.text("Sprite");
            ImGui.nextColumn();
            ImString path = new ImString(spriteComponent.getPath());
            ImGui.setNextItemWidth(-30);
            ImGui.beginDisabled();
            ImGui.inputText("##TexturePath", path);
            if (ImGui.beginDragDropTarget()) {
                Path payload = ImGui.acceptDragDropPayload("ImageFile");
                if (payload != null ) {
                    spriteComponent.setPath(FileSystem.relativize(ProjectManager.getGame().getDirectory(), payload.toString()));
                }
                ImGui.endDragDropTarget();
            }
            ImGui.endDisabled();
            ImGui.columns(1);

            // TODO: Check this!
            Texture texture = (spriteComponent.getPath() == null || spriteComponent.getPath().isEmpty()) ? null : ProjectManager.getGame()
                    .getResourceManager()
                    .getTexture(spriteComponent.getPath());
            int textureWidth = texture != null ? texture.getWidth() : 0;
            int textureHeight = texture != null ? texture.getHeight() : 0;
            renderCheckerboardWithImage(texture,
                    textureWidth, textureHeight, ImGui.getWindowWidth() - 20, 300);
            ImGui.separator();
        }
    }

    /**
     * Render script component.
     */
    private void renderScriptComponent() {
        ScriptComponent scriptComponent = Objects.requireNonNull(gameObject.get()).getComponent(ScriptComponent.class);
        if (ImGui.collapsingHeader("Script Component")) {
            ImGui.columns(2);
            ImGui.text("Script");
            ImGui.nextColumn();
            ImString path = new ImString(scriptComponent.getPath());
            ImGui.setNextItemWidth(-1);
            ImGui.beginDisabled();
            ImGui.inputText("##sciptPath", path);
            if (ImGui.beginDragDropTarget()) {
                Path payload = ImGui.acceptDragDropPayload("LuaScript");
                if (payload != null ) {
                    scriptComponent.setPath(FileSystem.relativize(ProjectManager.getGame().getDirectory(), payload.toString()));
                }
                ImGui.endDragDropTarget();
            }
            ImGui.endDisabled();
            ImGui.nextColumn();
            ImGui.text("Params");
            ImGui.nextColumn();
            if (ImGui.button(" + ")) {
                scriptComponent.getParams().add(new AbstractMap.SimpleEntry<>("paramKey", ""));
            }
            ImGui.columns(1);

            ImGui.separator();
            int indexToRemove = -1;
            int index = 1;
            String keyToChange = null;
            int indexToChange = -1;
            for (var param : scriptComponent.getParams()) {
                ImGui.textDisabled(String.format("%3s", index));
                ImGui.sameLine();
                ImString id = new ImString();
                id.set(param.getKey().toString());
                ImGui.setNextItemWidth(150);
                ImGui.inputText("##Name" + index, id);
                if (!id.toString().equals(param.getKey().toString())) {
                    keyToChange = id.toString();
                    indexToChange = index -1 ;
                }
                ImGui.sameLine();
                ImString value = new ImString();
                value.set(param.getValue().toString());
                ImGui.beginDisabled();
                ImGui.setNextItemWidth(-30);
                ImGui.inputText("##Value" + index, value);
                ImGui.endDisabled();
                if (ImGui.beginDragDropTarget()) {
                    Object payload = ImGui.acceptDragDropPayload("GameObject");
                    if (payload instanceof GameObject) {

                        System.out.println(payload);
                        param.setValue(((GameObject) payload).getId().toString());
                    }
                    if (payload instanceof Path) {
                        param.setValue(
                                FileSystem.relativize(
                                        ProjectManager.getGame().getDirectory(),
                                        payload.toString()));

                    }
                    ImGui.endDragDropTarget();
                }
                if (ImGui.beginDragDropTarget()) {
                    Object payload = ImGui.acceptDragDropPayload("Prefab");
                    if (payload instanceof Path) {
                        param.setValue(
                                FileSystem.relativize(
                                        ProjectManager.getGame().getDirectory(),
                                        payload.toString()));

                    }
                    ImGui.endDragDropTarget();
                }
                ImGui.sameLine();
                EditorAssetManager.getInstance().getFont("Default25").setScale(0.7f);
                ImGui.pushFont(    EditorAssetManager.getInstance().getFont("Default25"));
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
                ImGui.setCursorPosY(ImGui.getCursorPosY() + 5);
                var cursor = ImGui.getCursorPos();
                if (ImGui.button(" ##" + index)) {
                    indexToRemove = index - 1;
                }
                ImGui.popStyleVar();
                ImGui.popStyleColor(3);

                if (ImGui.isItemActive()) {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.4f, 0.4f, 1.0f);
                } else if (ImGui.isItemHovered()) {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0.3f, 0.6f, 0.8f, 0.8f);
                } else {
                    ImGui.pushStyleColor(ImGuiCol.Text, 0.6f, 0.6f, 0.6f, 1.0f);
                }
                ImGui.setCursorPos(cursor.x + 3, cursor.y + 3);
                ImGui.text(Icons.Trash);
                ImGui.popStyleColor();
                ImGui.setCursorPosY(ImGui.getCursorPosY() + 2);
                EditorAssetManager.getInstance().getFont("Default25").setScale(1.0f);
                ImGui.popFont();
                index++;
            }

            if (indexToRemove > -1) {
                scriptComponent.getParams().remove(indexToRemove);
                indexToRemove = -1;
            }
            if (keyToChange != null) {
                String value = scriptComponent.getParams().get(indexToChange).getValue();
                scriptComponent.getParams().add(indexToChange, new AbstractMap.SimpleEntry<>(keyToChange, value));
                scriptComponent.getParams().remove(indexToChange + 1);

            }

            ImGui.separator();
        }

    }

    /**
     * Render camera component.
     */
    private void renderCameraComponent() {
        CameraComponent cameraComponent = Objects.requireNonNull(gameObject.get()).getComponent(CameraComponent.class);
        if (ImGui.collapsingHeader("Camera Component")) {
            ImGui.pushID("Start Camera Component ID");
            ImGui.columns(2);
            ImGui.text("Viewport");
            ImGui.nextColumn();
            float[] viewportX = new float[] { cameraComponent.getViewport().x };
            float[] viewportY = new float[] { cameraComponent.getViewport().y };
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

            cameraComponent.setViewport(new Vector2f(viewportX[0], viewportY[0]));

            ImGui.nextColumn();
            Vector3f bgColor = cameraComponent.getBackground();
            float[] color = new float[] { bgColor.x, bgColor.y, bgColor.z };
            ImGui.setNextItemWidth(-1);
            ImGui.colorEdit3("##Color", color);
            ImGui.columns(1);
            ImGui.separator();
            cameraComponent.setBackground(new Vector3f(color[0], color[1], color[2]));
            ImGui.popID();
        }
    }

    /**
     * Render transform component items.
     */
    private void renderTransformComponent() {
        TransformComponent transformComponent = Objects.requireNonNull(gameObject.get()).getComponent(TransformComponent.class);
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
            ImGui.dragFloat("##R",rot, 0.01f);
            transformComponent.setRotation(rot[0]);

            float[] scaleX = new float[] { transformComponent.getScale().x };
            float[] scaleY = new float[] { transformComponent.getScale().y };
            ImGui.nextColumn();
            ImGui.text("Scale");
            ImGui.nextColumn();
            ImGui.textColored(1.0f, 0.5f, 0.5f, 1.0f, " x ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##W",scaleX, 0.01f);

            ImGui.textColored(0.5f, 1.0f, 0.5f, 1.0f, " y ");
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.dragFloat("##H",scaleY, 0.01f);
            transformComponent.setScale(new Vector3f(scaleX[0], scaleY[0], 1));
            ImGui.columns(1);
            ImGui.separator();
        }
    }

    /**
     * Render inspector window.
     */
    @Override
    public void body() {
        if (gameObject == null || gameObject.get() == null) {
            return;
        }
        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));
        ImGui.columns(2);
        ImGui.setColumnWidth(-1, 100);
        ImGui.columns(1);
        ImGui.columns(2);
        ImGui.text("ID");
        ImGui.nextColumn();
        ImString id = new ImString(gameObject.get().getId().toString());
        ImGui.beginDisabled();
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##ID", id);
        ImGui.endDisabled();
        ImGui.nextColumn();

        ImString name = new ImString(gameObject.get().getName(), 255);
        ImGui.text("Name");
        ImGui.nextColumn();
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##Name", name);
        gameObject.get().setName(name.toString());
        ImGui.nextColumn();
        ImBoolean active = new ImBoolean(gameObject.get().isActive());
        ImGui.text("Active");
        ImGui.nextColumn();
        ImGui.checkbox("##Active", active);

        gameObject.get().setActive(active.get());
        ImGui.columns(1);
        ImGui.separator();
        ImGui.textDisabled("Components");
        ImGui.sameLine();
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        var cur = ImGui.getCursorPos();
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 32);

        if (ImGui.button("  ##AddComponent")) {
            ImGui.openPopup("Add Component Menu");
        }
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);

        if (ImGui.isItemActive()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.4f, 0.4f, 1.0f);
        } else if (ImGui.isItemHovered()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.3f, 0.6f, 0.8f, 0.8f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.6f, 0.6f, 0.6f, 1.0f);
        }
        ImGui.setCursorPos(ImGui.getWindowWidth() - 30, cur.y + 5);
        EditorAssetManager.getInstance().getFont("Default25").setScale(0.7f);
        ImGui.pushFont( EditorAssetManager.getInstance().getFont("Default25"));
        ImGui.text(Icons.PlusMinus);
        EditorAssetManager.getInstance().getFont("Default25").setScale(1.0f);

        ImGui.popFont();
        ImGui.popStyleColor();
        for (Component component : gameObject.get().getComponents().values()) {
            components.get(component.getComponentType()).run();
        }
        renderAddComponentContextMenu(gameObject.get());
        ImGui.popFont();
    }

    @Override
    protected void header() {
        EditorAssetManager.getInstance().getFont("Default25").setScale(0.7f);
        ImGui.pushFont( EditorAssetManager.getInstance().getFont("Default25"));
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 28);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 3);
        var cursor = ImGui.getCursorPos();
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        if (ImGui.button("  ##Lock", 20, 20)) {
            locked = locked.equals(Icons.Lock) ? Icons.Unlock : Icons.Lock;
        }
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);
        if (ImGui.isItemActive()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.4f, 0.4f, 1.0f);
        } else if (ImGui.isItemHovered()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.3f, 0.6f, 0.8f, 0.8f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.6f, 0.6f, 0.6f, 1.0f);
        }
        ImGui.setCursorPos(cursor.x + 3, cursor.y + 2);
        ImGui.text(locked);
        ImGui.popStyleColor();
        ImGui.popFont();
        EditorAssetManager.getInstance().getFont("Default25").setScale(1.0f);
    }
}