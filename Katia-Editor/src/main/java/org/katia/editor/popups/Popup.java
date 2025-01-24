package org.katia.editor.popups;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.katia.Icons;
import org.katia.editor.managers.EditorInputManager;
import org.lwjgl.glfw.GLFW;

public abstract class Popup {
    String popup;
    String title;
    int width;
    int height;
    boolean isOpen;
    int flags;

    public Popup(String popup, String title, int width, int height) {
        this.popup = popup;
        this.title = title;
        this.width = width;
        this.height = height;
        this.isOpen = false;
        this.flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoTitleBar;

    }

    public void open() {
        isOpen = true;
        ImGui.openPopup(popup);
    }

    public void begin() {
        ImVec2 workSize = ImGui.getMainViewport().getWorkSize();
        ImVec2 modalSize = new ImVec2(width, height);
        ImGui.setNextWindowPos((workSize.x - modalSize.x) / 2, (workSize.y - modalSize.y) / 2);
        ImGui.setNextWindowSize(modalSize.x, modalSize.y);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 2);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.4f, 0.4f, 0.4f, 0.5f);

    }

    public void render() {

        begin();
        if (ImGui.beginPopupModal(popup, new ImBoolean(true), flags)) {
            header();
            body();

            if (EditorInputManager.getInstance().isKeyJustPressed(GLFW.GLFW_KEY_ESCAPE)) {
                ImGui.closeCurrentPopup();
                isOpen = false;
            }
            ImGui.endPopup();
        }
        end();
    }

    public void end() {
        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
    }

    private void header() {
        ImGui.textDisabled(title);
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 30);
        setupCloseButton();
        ImGui.popStyleColor();
    }

    public abstract void body();

    private  void setupCloseButton() {
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);

        if (ImGui.button(" ")) {
            ImGui.closeCurrentPopup();
            isOpen = false;
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
}
