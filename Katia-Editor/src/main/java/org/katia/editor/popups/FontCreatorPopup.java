package org.katia.editor.popups;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.katia.FileSystem;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.editor.managers.EditorInputManager;
import org.katia.factory.FontFactory;
import org.katia.gfx.resources.Font;
import org.katia.gfx.resources.Texture;
import org.lwjgl.glfw.GLFW;

public class FontCreatorPopup {


    private static String path;
    private static boolean isSet = false;
    static Font font;

    public static void setFont(String path) {
        FontCreatorPopup.path = path;
        isSet = true;
        font = FontFactory.createFont(path);
        Logger.log("WTF");
    }

    /**
     * Render image preview popup.
     */
    public static void render() {
        if (!isSet) {
            return;
        }
        ImVec2 workSize = ImGui.getMainViewport().getWorkSize();
        ImVec2 modalSize = new ImVec2(700, 600);

        ImGui.setNextWindowPos((workSize.x - modalSize.x) / 2, (workSize.y - modalSize.y) / 2);
        ImGui.setNextWindowSize(modalSize.x, modalSize.y);

        int flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoTitleBar;

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 5);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 2);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.4f, 0.4f, 0.4f, 0.5f);

        if (ImGui.beginPopupModal("Font Creator", new ImBoolean(true), flags)) {
            renderHeader();
            renderBody();
            if (EditorInputManager.getInstance().isKeyJustPressed(GLFW.GLFW_KEY_ESCAPE)) {
                ImGui.closeCurrentPopup();
                isSet = false;
            }
            ImGui.endPopup();
        }

        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
    }

    private static void renderBody() {
        ImGui.beginChild("##FontBody", -1, -1, true);
        ImGui.image(font.getTexture().getId(), 512, 512);
        ImGui.endChild();
    }

    private static void renderHeader() {
        String fileName = FileSystem.getFileName(path);

        ImGui.textDisabled(" FONT");
        ImGui.sameLine();
        centerText(fileName);
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 30);
        setupCloseButton();
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 30);
        ImGui.text(Icons.SquareX);
        ImGui.popStyleColor();
    }

    private static void centerText(String text) {
        ImGui.setCursorPosX((ImGui.getWindowWidth() - ImGui.calcTextSize(text).x) / 2);
        ImGui.text(text);
    }

    private static void setupCloseButton() {
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);

        if (ImGui.button(" ")) {
            ImGui.closeCurrentPopup();
            isSet = false;
        }
        ImGui.popStyleVar();
        ImGui.popStyleColor(3);
        updateCloseButtonColor();
    }

    private static void updateCloseButtonColor() {
        if (ImGui.isItemActive()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.4f, 0.4f, 1.0f);
        } else if (ImGui.isItemHovered()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.3f, 0.6f, 0.8f, 0.8f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.8f, 0.8f, 1.0f);
        }
    }

    public static void renderCheckerboardWithImage(Texture texture, int textureWidth, int textureHeight, float displayWidth, float displayHeight) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        ImVec2 startPos = ImGui.getCursorScreenPos();

        drawCheckerboard(drawList, startPos, displayWidth, displayHeight);
        reserveCheckerboardSpace(displayWidth, displayHeight);

        drawImage(texture, textureWidth, textureHeight, displayWidth, displayHeight, startPos);
    }

    private static void drawCheckerboard(ImDrawList drawList, ImVec2 startPos, float displayWidth, float displayHeight) {
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

    private static void reserveCheckerboardSpace(float displayWidth, float displayHeight) {
        ImGui.dummy(displayWidth, displayHeight);
    }

    private static void drawImage(Texture texture, int textureWidth, int textureHeight, float displayWidth, float displayHeight, ImVec2 startPos) {
        float aspectRatio = (float) textureWidth / textureHeight;
        float scaledWidth = textureWidth;
        float scaledHeight = textureHeight;

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
            ImGui.getWindowDrawList().addImage(texture.getId(), imageX, imageY, imageX + scaledWidth, imageY + scaledHeight);
            renderImageInfo(ImGui.getWindowDrawList(), texture, displayWidth, displayHeight, startPos);
        }
    }

    private static void renderImageInfo(ImDrawList drawList, Texture texture, float displayWidth, float displayHeight, ImVec2 startPos) {
        String dimensions = texture.getWidth() + "x" + texture.getHeight();
        ImVec2 textSize = ImGui.calcTextSize(dimensions);

        float adjustedX = startPos.x + displayWidth - textSize.x - 10;
        float adjustedY = startPos.y + displayHeight - textSize.y - 10;

        renderOutlinedText(drawList, dimensions, adjustedX, adjustedY, ImColor.intToColor(255, 255, 255, 255), ImColor.intToColor(0, 0, 0, 255));
    }

    private static void renderOutlinedText(ImDrawList drawList, String text, float x, float y, int textColor, int outlineColor) {
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
