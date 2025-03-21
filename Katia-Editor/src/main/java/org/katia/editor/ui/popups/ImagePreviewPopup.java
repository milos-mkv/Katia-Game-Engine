package org.katia.editor.ui.popups;

import imgui.*;
import imgui.flag.*;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.gfx.resources.Texture;

/**
 * This class represents image preview popup.
 * @see org.katia.editor.ui.popups.Popup
 */
public class ImagePreviewPopup extends Popup {

    public ImagePreviewPopup() {
        super("IMAGE PREVIEW", 700, 600);
    }

    /**
     * Render image preview popup.
     */
    @Override
    public void body() {
        Texture image = EditorAssetManager.getInstance().getImage((String) data);
        renderCheckerboardWithImage(image, image.getWidth(), image.getHeight(), 700, 550);
    }

    /**
     * Render checkerboard with image in it.
     * @param texture Image to render.
     * @param textureWidth Image width.
     * @param textureHeight Image height.
     * @param displayWidth Max image width.
     * @param displayHeight Max image height.
     */
    public void renderCheckerboardWithImage(Texture texture, int textureWidth, int textureHeight, float displayWidth, float displayHeight) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        ImVec2 startPos = ImGui.getCursorScreenPos();

        drawCheckerboard(drawList, startPos, displayWidth, displayHeight);
        ImGui.dummy(displayWidth, displayHeight);

        drawImage(texture, textureWidth, textureHeight, displayWidth, displayHeight, startPos);
    }

    /**
     * Render just checkerboard.
     * @param drawList ImGui DrawList.
     * @param startPos Start position.
     * @param displayWidth Display width.
     * @param displayHeight Display Height.
     */
    private void drawCheckerboard(ImDrawList drawList, ImVec2 startPos, float displayWidth, float displayHeight) {
        float checkerSize = 16.0f;
        int numColumns = (int) Math.ceil(displayWidth / checkerSize);
        int numRows = (int) Math.ceil(displayHeight / checkerSize);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                boolean isDark = (row + col) % 2 == 0;
                int color = isDark ? ImColor.intToColor(50, 50, 50, 255) : ImColor.intToColor(100, 100, 100, 255);
                drawList.addRectFilled(
                        startPos.x + col * checkerSize,
                        startPos.y + row * checkerSize,
                        startPos.x + (col + 1) * checkerSize,
                        startPos.y + (row + 1) * checkerSize,
                        color
                );
            }
        }
    }

    /**
     * Draw image.
     * @param texture Image to draw.
     * @param textureWidth Image width.
     * @param textureHeight Image height.
     * @param displayWidth Max display width.
     * @param displayHeight Max display height.
     * @param startPos Start position.
     */
    private void drawImage(Texture texture, int textureWidth, int textureHeight, float displayWidth, float displayHeight, ImVec2 startPos) {
        float aspectRatio = (float) textureWidth / textureHeight;
        float scaledWidth = textureWidth;
        float scaledHeight = textureHeight;

        // Scale the image to fit within the display area
        if (textureWidth > displayWidth || textureHeight > displayHeight) {
            scaledWidth = displayWidth;
            scaledHeight = displayWidth / aspectRatio;

            if (scaledHeight > displayHeight) {
                scaledHeight = displayHeight;
                scaledWidth = displayHeight * aspectRatio;
            }
        }

        float imageX = startPos.x + (displayWidth - scaledWidth) / 2.0f;
        float imageY = startPos.y + (displayHeight - scaledHeight) / 2.0f;

        if (texture != null) {
            // Draw the main image
            ImGui.getWindowDrawList().addImage(texture.getId(), imageX, imageY, imageX + scaledWidth, imageY + scaledHeight);

            // Check if the mouse is hovering over the image
            if (ImGui.isMouseHoveringRect(imageX, imageY, imageX + scaledWidth, imageY + scaledHeight)) {
               ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
                ImGui.beginTooltip();

                // Display image dimensions
//                ImGui.text(String.format("Dimensions: %dx%d", textureWidth, textureHeight));

                // Calculate the mouse position relative to the image
                float mouseX = ImGui.getMousePosX() - imageX;
                float mouseY = ImGui.getMousePosY() - imageY;

                // Normalize mouse coordinates to UV space (0.0 to 1.0)
                float uvMouseX = mouseX / scaledWidth;
                float uvMouseY = mouseY / scaledHeight;

                // Define the zoom level
                float zoomFactor = 0.05f; // 25% of the image

                // Calculate UV coordinates for the zoomed-in area
                float uvStartX = Math.max(uvMouseX - zoomFactor / 2, 0.0f);
                float uvStartY = Math.max(uvMouseY - zoomFactor / 2, 0.0f);
                float uvEndX = Math.min(uvMouseX + zoomFactor / 2, 1.0f);
                float uvEndY = Math.min(uvMouseY + zoomFactor / 2, 1.0f);

                // Ensure UVs stay within valid bounds
                if (uvStartX < 0.0f) uvStartX = 0.0f;
                if (uvStartY < 0.0f) uvStartY = 0.0f;
                if (uvEndX > 1.0f) uvEndX = 1.0f;
                if (uvEndY > 1.0f) uvEndY = 1.0f;

                // Render the zoomed-in part of the image in the tooltip
                float zoomSize = 150.0f; // Size of the zoom preview
                ImGui.image(texture.getId(), zoomSize, zoomSize, uvStartX, uvStartY, uvEndX, uvEndY);

                ImGui.endTooltip();
                ImGui.popStyleVar();
            }

            renderImageInfo(ImGui.getWindowDrawList(), texture, displayWidth, displayHeight, startPos);
        }
    }

    private void renderImageInfo(ImDrawList drawList, Texture texture, float displayWidth, float displayHeight, ImVec2 startPos) {
        String dimensions = texture.getWidth() + "x" + texture.getHeight();
        ImVec2 textSize = ImGui.calcTextSize(dimensions);

        float adjustedX = startPos.x + displayWidth - textSize.x - 200;
        float adjustedY = startPos.y + displayHeight - textSize.y - 10;

        renderOutlinedText(drawList, dimensions, adjustedX, adjustedY, ImColor.intToColor(255, 255, 255, 255), ImColor.intToColor(0, 0, 0, 255));
    }

    private void renderOutlinedText(ImDrawList drawList, String text, float x, float y, int textColor, int outlineColor) {
        float outlineThickness = 2.0f;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    drawList.addText(x + dx * outlineThickness, y + dy * outlineThickness, outlineColor, text);
                }
            }
        }

        drawList.addText(x, y, textColor, text);
    }
}
