package org.katia.editor.ui.popups;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.EditorInputManager;
import org.lwjgl.glfw.GLFW;

/**
 * Base class of all UI popups which creates generic popup body with title bar.
 */
public abstract class Popup {

    protected String title;
    protected int width;
    protected int height;
    protected int flags;
    protected Object data;

    /**
     * Popup Constructor.
     * @param title Title name.
     * @param width Popup width.
     * @param height Popup height.
     */
    public Popup(String title, int width, int height) {
        Logger.log(Logger.Type.INFO, title, "Popup Constructor");
        this.title = title;
        this.width = width;
        this.height = height;
        this.flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                | ImGuiWindowFlags.NoTitleBar;
    }

    /**
     * Open popup.
     * @param data Anything related to this popup.
     */
    public void open(Object data) {
        this.data = data;
    }

    /**
     * Render popup.
     */
    public void render() {
        begin();
        if (ImGui.beginPopupModal(this.getClass().toString(), new ImBoolean(true), flags)) {
            header();
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10, 10);
            ImGui.beginChild(title + "Body", -1, -1, true);
            body();
            ImGui.endChild();
            ImGui.popStyleVar();
            if (EditorInputManager.getInstance().isKeyJustPressed(GLFW.GLFW_KEY_ESCAPE)) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
        end();
    }

    /**
     * Setup popup UI style settings anc vars.
     */
    private void begin() {
        ImVec2 workSize = ImGui.getMainViewport().getWorkSize();
        ImVec2 modalSize = new ImVec2(width, height);
        ImGui.setNextWindowPos((workSize.x - modalSize.x) / 2, (workSize.y - modalSize.y) / 2);
        ImGui.setNextWindowSize(modalSize.x, modalSize.y);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 2);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.4f, 0.4f, 0.4f, 0.5f);
    }

    /**
     * Remove popup UI settings and vars.
     */
    private void end() {
        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
    }

    /**
     * Render popup header.
     */
    private void header() {
        ImGui.pushFont(EditorAssetManager.getInstance().getFont("Default"));
        ImGui.textDisabled(title);
        ImGui.popFont();
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 30);
        setupCloseButton();
        ImGui.popStyleColor();
    }

    /**
     * Render custom close button.
     */
    private void setupCloseButton() {
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
        if (ImGui.button(" ")) {
            ImGui.closeCurrentPopup();
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
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 30);
        ImGui.text(Icons.SquareX);
    }

    public abstract void body();
}
