package org.katia.editor;

import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Data;
import lombok.Getter;
import org.joml.Vector2i;
import org.katia.Logger;
import org.katia.editor.menubar.MainMenuBar;
import org.katia.editor.ui.*;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

/**
 * Global class holding all UI components of game engine editor, responsible for rendering UI components.
 */
@Data
public class EditorUI {

    @Getter
    static EditorUI instance = new EditorUI();

    HashMap<Class<?>, UIComponent> components;

    ImGuiImplGl3 imGuiImplGl3;
    ImGuiImplGlfw imGuiImplGlfw;

    /**
     * Editor UI Constructor.
     */
    public EditorUI() {
        Logger.log(Logger.Type.INFO, "Editor UI Constructor");
        components = new LinkedHashMap<>();

        // List of component classes to registerSceneWindow.class,
        List<Class<? extends UIComponent>> componentClasses = List.of(
                DockSpace.class, HierarchyWindow.class, InspectorWindow.class, ProjectWindow.class,
                MainMenuBar.class, CodeEditorWindow.class
        );

        for (Class<? extends UIComponent> clazz : componentClasses) {
            try {
                components.put(clazz, clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                Logger.log(Logger.Type.ERROR, "Failed to instantiate " + clazz.getSimpleName());
            }
        }

        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGlfw.init(EditorWindow.getInstance().getHandle(), true);
        imGuiImplGl3 = new ImGuiImplGl3();
        imGuiImplGl3.init("#version 430");
    }

    /**
     * Get UI component.
     * @param component UI component.
     * @return Component
     * @param <T> Valid UI component
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T>  component) {
        return (T) components.get(component);
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

        components.forEach((key, value) -> value.render());

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