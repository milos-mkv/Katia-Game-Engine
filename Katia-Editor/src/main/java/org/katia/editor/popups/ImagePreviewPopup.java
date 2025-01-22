package org.katia.editor.popups;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.katia.FileSystem;
import org.katia.Icons;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.factory.TextureFactory;
import org.katia.gfx.Texture;

public class ImagePreviewPopup {

    static Texture image;
    static String path;
    public static void setImage(String path) {
        image = TextureFactory.createTexture(path);
        ImagePreviewPopup.path = path;
    }

    public static void render() {
        ImVec2 workSize = ImGui.getMainViewport().getWorkSize();
        ImVec2 modalSize = new ImVec2(700, 600);

        ImBoolean unusedOpen = new ImBoolean(true);
        ImGui.setNextWindowPos(workSize.x / 2 - modalSize.x / 2, workSize.y / 2 - modalSize.y / 2);
        ImGui.setNextWindowSize(modalSize.x, modalSize.y);

        int flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoTitleBar;
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 5);
        if (ImGui.beginPopupModal("Image Preview", unusedOpen, flags)) {
            String text = FileSystem.getFileName(path);
            ImGui.textDisabled(" IMAGE");
            ImGui.sameLine();
            ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - ImGui.calcTextSize(text).x / 2);
            ImGui.text(text);
            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));
            ImGui.pushStyleColor(ImGuiCol.ScrollbarGrab, 0.8f, 0.4f, 0.4f, 1.0f);
            ImGui.beginChild("Image Body", -1, -1);
            renderCheckerboardWithImage(image, image.getWidth(), image.getHeight(), 700, 550);
            ImGui.endChild();
            ImGui.popStyleColor();
            ImGui.popFont();

            if (ImGui.isKeyDown(ImGuiKey.Escape)) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
        ImGui.popStyleVar();
    }

    public static void renderCheckerboardWithImage(Texture texture, int textureWidth, int textureHeight, float displayWidth, float displayHeight) {
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
        int borderColor = ImColor.intToColor(0, 0, 0, 0); // Light gray border color
        float borderThickness = 2.0f; // Thickness of the border
        drawList.addRect(startPos.x, startPos.y, startPos.x + displayWidth + 10, startPos.y + displayHeight + 12, borderColor, 0.0f, 0, borderThickness);

        // Calculate the image's aspect ratio
        float aspectRatio = (float) textureWidth / textureHeight;


        // Determine the image size
        float scaledWidth = textureWidth;
        float scaledHeight = textureHeight;

        if (textureWidth > displayWidth || textureHeight > displayHeight) {
            // Scale the image only if it exceeds the display dimensions
            scaledWidth = displayWidth;
            scaledHeight = displayWidth / aspectRatio;

            if (scaledHeight > displayHeight) {
                scaledHeight = displayHeight;
                scaledWidth = displayHeight * aspectRatio;
            }
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
    public static void renderOutlinedText(ImDrawList drawList, String text, float x, float y, int textColor, int outlineColor) {
        float outlineThickness = 2.0f; // Thickness of the outline

        // Adjust text position for centering
        ImVec2 textSize = ImGui.calcTextSize(text);
        float centerX = x - textSize.x / 2.0f - 20;
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

}
