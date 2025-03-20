package org.katia.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Data;
import lombok.Getter;
import org.joml.Vector2i;
import org.katia.Logger;
import org.katia.editor.ui.menubar.MainMenuBar;
import org.katia.editor.ui.windows.*;
import org.katia.editor.ui.popups.PopupManager;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

/**
 * Global class holding all UI components of game engine editor, responsible for rendering UI components.
 */
@Data
public class EditorUI {

    @Getter
    static EditorUI instance = new EditorUI();

    HashMap<Class<?>, Window> windows;

    private PopupManager popupRenderer;
    MainMenuBar mainMenuBar;
    ImGuiImplGl3 imGuiImplGl3;
    ImGuiImplGlfw imGuiImplGlfw;

    /**
     * Editor UI Constructor.
     */
    public EditorUI() {
        Logger.log(Logger.Type.INFO, "Editor UI Constructor");
        windows = new LinkedHashMap<>();
        mainMenuBar = new MainMenuBar();
        windows.put(HierarchyWindow.class, new HierarchyWindow());
        windows.put(InspectorWindow.class, new InspectorWindow());
        windows.put(ProjectWindow.class, new ProjectWindow());
        windows.put(SceneWindow.class, new SceneWindow());
        windows.put(CodeEditorWindow.class, new CodeEditorWindow());

        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGlfw.init(EditorWindow.getInstance().getHandle(), true);
        imGuiImplGl3 = new ImGuiImplGl3();
        imGuiImplGl3.init("#version 430");

        popupRenderer = new PopupManager();
    }

    /**
     * Get UI component.
     * @param component UI component.
     * @return Component
     * @param <T> Valid UI component
     */
    @SuppressWarnings("unchecked")
    public <T> T getWindow(Class<T>  component) {
        return (T) windows.get(component);
    }

    /**
     * Render editor UI.
     */
    public void render() {
        Vector2i size = EditorWindow.getInstance().getSize();

        glViewport(0, 0, size.x, size.y);
        glClearColor(0.14f, 0.16f, 0.18f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
        ImGuizmo.beginFrame();

        ImVec2 viewportPos = ImGui.getMainViewport().getPos();
        ImVec2 viewportSize = ImGui.getMainViewport().getSize();

        float padding = 10.0f; // Adjust dock space bounds
        ImGui.setNextWindowPos(viewportPos.x + padding, viewportPos.y + padding + 40);
        ImGui.setNextWindowSize(viewportSize.x - 2 * padding, viewportSize.y - 2 * padding - 40);
        ImGui.setNextWindowBgAlpha(0.0f); // Make it transparent if needed

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 1, 1);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleColor(ImGuiCol.Separator, 0.0f, 0.0f, 0.0f, 0.0f);

        ImGui.begin("DockSpace", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
        ImGui.dockSpace(ImGui.getID("DockSpace"));
        ImGui.end();

        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
        mainMenuBar.render();
        windows.forEach((key, value) -> value.render());


        PopupManager.getInstance().render();
//        popupRenderer.getPopups().forEach(popup -> popup.render());
//        CreateNewProjectPopup.getInstance().render();
//        CreateNewScenePopup.getInstance().render();
//        OpenScenePopup.getInstance().render();
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
        GLFW.glfwSwapBuffers(EditorWindow.getInstance().getHandle());
    }

    /**
     * Dispose of editor UI.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Editor UI Dispose");
        imGuiImplGl3.dispose();
        imGuiImplGlfw.dispose();
    }
}