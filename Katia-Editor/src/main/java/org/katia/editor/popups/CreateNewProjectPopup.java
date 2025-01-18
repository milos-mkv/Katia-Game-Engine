package org.katia.editor.popups;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.editor.EditorUtils;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.game.Configuration;

/**
 * This class renders `Create new project` popup.
 */
public class CreateNewProjectPopup {

    static ImString name = new ImString();
    static ImString path = new ImString();
    static ImInt windowWidth = new ImInt(800);
    static ImInt windowHeight = new ImInt(800);
    static ImString gameTitle = new ImString();
    static ImBoolean windowResizable = new ImBoolean(true);
    static ImBoolean gameVSync = new ImBoolean(true);
    static ImString errorMessage = new ImString();

    public static void render() {
        ImVec2 workSize = ImGui.getMainViewport().getWorkSize();
        ImVec2 modalSize = new ImVec2(800, 435);

        ImBoolean unusedOpen = new ImBoolean(true);
        ImGui.setNextWindowPos(workSize.x / 2 - modalSize.x / 2, workSize.y / 2 - modalSize.y / 2);
        ImGui.setNextWindowSize(modalSize.x, modalSize.y);

        int flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoTitleBar;
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        if (ImGui.beginPopupModal("Create New Project Popup", unusedOpen, flags)) {
            renderHeader();
            renderFormBody();
            renderFooter();
            ImGui.endPopup();
        }
        ImGui.popStyleVar();
    }

    private static void renderHeader() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10, 5);
        ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, 7);
        ImGui.beginChild("Header", -1, 50, true, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default40"));
        ImGui.text("CREATE NEW PROJECT");
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 40);

        renderCloseButton();

        ImGui.popFont();
        ImGui.popStyleVar(2);
        ImGui.endChild();
    }

    private static void resetValues() {
        name.set("");
        path.set("");
        windowWidth.set(800);
        windowHeight.set(800);
        gameTitle.set("");
        windowResizable.set(true);
        gameVSync.set(true);
        errorMessage.set("");
    }

    private static void renderCloseButton() {
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);

        ImVec2 cursor = ImGui.getCursorPos();
        if (ImGui.button("##CLOSE", 33, 33)) {
            ImGui.closeCurrentPopup();
            resetValues();
        }

        renderButtonTextState(cursor, Icons.SquareX);

        ImGui.popStyleColor(3);
        ImGui.popStyleVar();
    }

    private static void renderFormBody() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 20, 5);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);

        ImGui.beginChild("FormBodyChild", -1, 325, true, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        {
            renderInputFields();
            renderConfigurationFields();
            renderErrorMessage();
        }
        ImGui.endChild();

        ImGui.popStyleColor(2);
        ImGui.popStyleVar();
    }

    private static void renderErrorMessage() {
        if (!errorMessage.get().isEmpty()) {
            ImGui.separator();
            ImGui.textColored(0.8f, 0.4f, 0.4f, 1.0f, errorMessage.get());
        }
    }

    private static void renderInputFields() {
        ImGui.columns(2, "##COL", false);
        ImGui.setColumnWidth(-1, 60);

        ImGui.text("Name");
        ImGui.nextColumn();
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##Name", name);

        ImGui.nextColumn();
        ImGui.text("Path");
        ImGui.nextColumn();

        ImGui.beginDisabled();
        ImGui.setNextItemWidth(-50);
        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default15"));
        ImGui.inputText("##Path", path);
        ImGui.popFont();
        ImGui.endDisabled();

        ImGui.sameLine();
        renderFolderButton();

        ImGui.columns(1);
        ImGui.separator();
        ImGui.textDisabled("Configuration");
        ImGui.separator();
    }

    private static void renderFolderButton() {
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);

        ImVec2 cursor = ImGui.getCursorPos();
        if (ImGui.button("##Open", 35, 30)) {
            String selectedPath = EditorUtils.openFolderDialog();
            if (selectedPath != null) {
                path.set(selectedPath);
            }
        }

        renderButtonTextState(new ImVec2(cursor.x + 5, cursor.y + 4), Icons.Folder);

        ImGui.popStyleColor(3);
    }

    private static void renderButtonTextState(ImVec2 cursor, String icon) {
        if (ImGui.isItemActive()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.4f, 0.4f, 1.0f);
        } else if (ImGui.isItemHovered()) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.3f, 0.6f, 0.8f, 0.8f);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.8f, 0.8f, 1.0f);
        }

        ImGui.setCursorPos(cursor.x, cursor.y);
        ImGui.text(icon);
        ImGui.popStyleColor();
    }

    private static void renderConfigurationFields() {
        ImGui.pushID("ConfigurationPartID");
        ImGui.columns(2, "##C2", false);
        ImGui.setColumnWidth(-1, 200);

        renderConfigField("Window width", "##Window width", windowWidth);
        renderConfigField("Window height", "##Window height", windowHeight);
        renderConfigField("Window resizable", "##Window resizable", windowResizable);
        renderConfigField("Game title", "##Game title", gameTitle);
        renderConfigField("Game vSync", "##Game vSync", gameVSync);

        ImGui.columns(1);
        ImGui.popID();
    }

    private static void renderConfigField(String label, String id, ImInt value) {
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.inputInt(id, value);
        ImGui.nextColumn();
    }

    private static void renderConfigField(String label, String id, ImBoolean value) {
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.checkbox(id, value);
        ImGui.nextColumn();
    }

    private static void renderConfigField(String label, String id, ImString value) {
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.inputText(id, value);
        ImGui.nextColumn();
    }

    private static void renderFooter() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 10, 5);
        ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, 7);
        ImGui.beginChild("Footer", -1, 50, true, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImGui.setCursorPosX(ImGui.getWindowWidth() / 2 - 50);
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5);
        if (ImGui.button(" CREATE ")) {
            try {
                Configuration configuration = new Configuration();
                configuration.setWidth(windowWidth.get());
                configuration.setHeight(windowHeight.get());
                configuration.setTitle(gameTitle.get());
                configuration.setResizable(windowResizable.get());
                configuration.setVSync(gameVSync.get());
                ProjectManager.getInstance().createProject(path.get(), name.get(), configuration);
                errorMessage.set("", true);

            } catch (RuntimeException e) {
                errorMessage.set(Icons.TriangleError + " " + e.getMessage(), true);
                Logger.log(Logger.Type.ERROR, e.toString());
            }
            ImGui.closeCurrentPopup();
        }

        ImGui.endChild();
        ImGui.popStyleVar(2);
    }
}
