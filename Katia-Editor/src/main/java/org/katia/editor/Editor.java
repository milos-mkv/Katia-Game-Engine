package org.katia.editor;

import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGui;
import lombok.Data;
import lombok.Getter;
import org.katia.Logger;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.EditorSceneManager;
import org.katia.editor.managers.GameManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.menubar.MainMenuBar;
import org.katia.editor.menubar.MenuAction;
import org.katia.editor.renderer.EditorSceneRenderer;
import org.katia.editor.windows.UIRenderer;
import org.katia.factory.GameObjectFactory;
import org.katia.gfx.FrameBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

@Data
public class Editor {

    @Getter
    static final Editor instance = new Editor();

    long handle;
    ImGuiImplGlfw imGuiImplGlfw;
    ImGuiImplGl3 imGuiImplGl3;
    UIRenderer uiRenderer;
    List<String> droppedFiles;
    public Editor() {
        Logger.log(Logger.Type.INFO, "Creating editor ...");

        createWindow();
        initializeImGui();
        droppedFiles = new ArrayList<>();
        uiRenderer = new UIRenderer();
    }

    /**
     * Create editor window.
     * @throws RuntimeException Throws when failed to create glfw window.
     */
    void createWindow() throws RuntimeException {
        Logger.log(Logger.Type.INFO, "Creating editor window ...");
        handle = GLFW.glfwCreateWindow(1200, 800, "Katia Editor", MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window!");
        }
        GLFWVidMode videoMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        GLFW.glfwSetWindowPos(handle, (videoMode.width() - 1200) / 2, (videoMode.height() - 800) / 2);
        GLFW.glfwShowWindow(handle);
        GLFW.glfwMakeContextCurrent(handle);
        GLFW.glfwSetWindowSizeLimits(handle, 1200, 800, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);

        GL.createCapabilities();
        GLFW.glfwSetFramebufferSizeCallback(handle, (long handle, int w, int h) -> {
            glViewport(0, 0, w, h);
        });
        GLFW.glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
                if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) { // Check if Ctrl is pressed
                    switch (key) {
                        case GLFW.GLFW_KEY_N:
                            uiRenderer.get(MainMenuBar.class).getActions().put(MenuAction.CREATE_NEW_PROJECT, true);
                            break;
                        case GLFW.GLFW_KEY_O:
                            uiRenderer.get(MainMenuBar.class).getActions().put(MenuAction.OPEN_PROJECT, true);
                            break;
                        case GLFW.GLFW_KEY_S:
                            uiRenderer.get(MainMenuBar.class).getActions().put(MenuAction.SAVE_PROJECT, true);
                            break;
                        case GLFW.GLFW_KEY_W:
                            uiRenderer.get(MainMenuBar.class).getActions().put(MenuAction.EXIT, true);
                            break;
                    }
                }
            }
        });
        GLFW.glfwSetDropCallback(handle, GLFWDropCallback.create((win, count, names) -> {
            droppedFiles.clear();
            for (int i = 0; i < count; i++) {
                droppedFiles.add(GLFWDropCallback.getName(names, i));
            }
        }));
    }

    /**
     * Initialize imgui.
     */
    private void initializeImGui() {
        ImGui.createContext();
        ImGui.styleColorsDark();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
        EditorAssetManager.getInstance();
        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGl3 = new ImGuiImplGl3();
        imGuiImplGlfw.init(handle, true);
        imGuiImplGl3.init("#version 330");
        setupStyle();
//        setupImGuiStyle2();
    }
    private void setupStyle() {
        float[][] colors = ImGui.getStyle().getColors();
        colors[ImGuiCol.Text] = new float[]{0.8f, 0.8f, 0.8f, 1.0f}; // Light grey text
        colors[ImGuiCol.TextDisabled] = new float[]{0.5f, 0.5f, 0.5f, 1.0f}; // Dimmed grey text
        colors[ImGuiCol.WindowBg] = new float[]{0.14f, 0.16f, 0.18f, 1.0f}; // Dark background{0.16f, 0.18f, 0.2f, 1.0f};
        colors[ImGuiCol.ChildBg] = new float[]{0.16f, 0.18f, 0.2f, 1.0f}; // Slightly darker child window
        colors[ImGuiCol.PopupBg] = new float[]{0.18f, 0.2f, 0.22f, 1.0f}; // Dark popup background
        colors[ImGuiCol.Border] = new float[]{0.2f, 0.22f, 0.24f, 1.0f}; // Grey border
        colors[ImGuiCol.BorderShadow] = new float[]{0.0f, 0.0f, 0.0f, 0.0f}; // No border shadow
        colors[ImGuiCol.FrameBg] = new float[]{0.2f, 0.22f, 0.24f, 1.0f}; // Neutral grey for frames
        colors[ImGuiCol.FrameBgHovered] = new float[]{0.3f, 0.6f, 0.8f, 0.7f}; // Hovered light blue frame
        colors[ImGuiCol.FrameBgActive] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Active frame with bright blue
        colors[ImGuiCol.TitleBg] = new float[]{0.14f, 0.16f, 0.18f, 1.0f}; // Dark grey title
        colors[ImGuiCol.TitleBgActive] = new float[]{0.2f, 0.25f, 0.3f, 1.0f}; // Active dark title background
        colors[ImGuiCol.TitleBgCollapsed] = new float[]{0.14f, 0.16f, 0.18f, 1.0f}; // Collapsed dark title
        colors[ImGuiCol.MenuBarBg] = new float[]{0.14f, 0.16f, 0.18f, 1.0f}; // Dark grey menu bar
        colors[ImGuiCol.ScrollbarBg] = new float[]{0.14f, 0.16f, 0.18f, 0.0f}; // Dark scrollbar background
        colors[ImGuiCol.ScrollbarGrab] = new float[]{0.3f, 0.3f, 0.3f, 0.6f}; // Light blue scrollbar grab
        colors[ImGuiCol.ScrollbarGrabHovered] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Hovered light blue scrollbar
        colors[ImGuiCol.ScrollbarGrabActive] = new float[]{0.5f, 0.8f, 1.0f, 1.0f}; // Active bright blue scrollbar
        colors[ImGuiCol.CheckMark] = new float[]{0.3f, 0.6f, 0.8f, 1.0f}; // Light blue checkmark
        colors[ImGuiCol.SliderGrab] = new float[]{0.3f, 0.6f, 0.8f, 0.8f}; // Light blue slider grab
        colors[ImGuiCol.SliderGrabActive] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Active slider grab
        colors[ImGuiCol.Button] = new float[]{0.2f, 0.22f, 0.24f, 1.0f}; // Neutral button
        colors[ImGuiCol.ButtonHovered] = new float[]{0.3f, 0.6f, 0.8f, 0.8f}; // Hovered light blue button
        colors[ImGuiCol.ButtonActive] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Active bright blue button
        colors[ImGuiCol.Header] = new float[]{0.2f, 0.25f, 0.3f, 1.0f}; // Neutral header
        colors[ImGuiCol.HeaderHovered] = new float[]{0.3f, 0.6f, 0.8f, 0.8f}; // Hovered light blue header
        colors[ImGuiCol.HeaderActive] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Active bright blue header
        colors[ImGuiCol.Separator] = new float[]{0.2f, 0.22f, 0.24f, 1.0f}; // Separator
        colors[ImGuiCol.SeparatorHovered] = new float[]{0.3f, 0.6f, 0.8f, 0.8f}; // Hovered light blue separator
        colors[ImGuiCol.SeparatorActive] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Active bright blue separator
        colors[ImGuiCol.ResizeGrip] = new float[]{0.3f, 0.6f, 0.8f, 0.8f}; // Resize grip
        colors[ImGuiCol.ResizeGripHovered] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Hovered resize grip
        colors[ImGuiCol.ResizeGripActive] = new float[]{0.5f, 0.8f, 1.0f, 1.0f}; // Active resize grip
        colors[ImGuiCol.Tab] = new float[]{0.2f, 0.25f, 0.3f, 1.0f}; // Neutral tab
        colors[ImGuiCol.TabHovered] = new float[]{0.3f, 0.6f, 0.8f, 0.8f}; // Hovered light blue tab
        colors[ImGuiCol.TabActive] = new float[]{0.26f, 0.28f, 0.3f, 0.8f}; // Active bright blue tab
        colors[ImGuiCol.TabUnfocused] = new float[]{0.16f, 0.18f, 0.2f, 1.0f}; // Unfocused tab
        colors[ImGuiCol.TabUnfocusedActive] = new float[]{0.2f, 0.25f, 0.3f, 1.0f}; // Unfocused active tab
        colors[ImGuiCol.DockingPreview] = new float[]{0.3f, 0.6f, 0.8f, 0.7f}; // Light blue docking preview
        colors[ImGuiCol.DockingEmptyBg] = new float[]{0.14f, 0.16f, 0.18f, 1.0f}; // Dark docking background
        colors[ImGuiCol.PlotLines] = new float[]{0.3f, 0.6f, 0.8f, 1.0f}; // Plot lines
        colors[ImGuiCol.PlotLinesHovered] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Hovered plot lines
        colors[ImGuiCol.PlotHistogram] = new float[]{0.3f, 0.6f, 0.8f, 1.0f}; // Plot histogram
        colors[ImGuiCol.PlotHistogramHovered] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Hovered histogram
        colors[ImGuiCol.TextSelectedBg] = new float[]{0.3f, 0.6f, 0.8f, 0.8f}; // Text selection background
        colors[ImGuiCol.DragDropTarget] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Drag-drop target
        colors[ImGuiCol.NavHighlight] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Navigation highlight
        colors[ImGuiCol.NavWindowingHighlight] = new float[]{0.3f, 0.6f, 0.8f, 0.7f}; // Navigation window highlight
        colors[ImGuiCol.NavWindowingDimBg] = new float[]{0.0f, 0.0f, 0.0f, 0.3f}; // Dim navigation background
        colors[ImGuiCol.ModalWindowDimBg] = new float[]{0.0f, 0.0f, 0.0f, 0.35f}; // Modal window dim background

        ImGui.getStyle().setColors(colors);

        ImGuiStyle style = ImGui.getStyle();
        style.setWindowPadding(8.00f, 8.00f);
        style.setFramePadding(5.00f, 2.00f);
        style.setCellPadding(6.00f, 6.00f);
        style.setItemSpacing(6.00f, 6.00f);
        style.setItemInnerSpacing(6.00f, 6.00f);
        style.setTouchExtraPadding(0.00f, 0.00f);
        style.setIndentSpacing(25.0f);
        style.setScrollbarSize(10.0f);
        style.setGrabMinSize(10.0f);
        style.setWindowBorderSize(1.0f);
        style.setChildBorderSize(1.0f);
        style.setPopupBorderSize(1.0f);
        style.setFrameBorderSize(1.0f);
        style.setTabBorderSize(1.0f);
        style.setWindowRounding(7.0f);
        style.setChildRounding(4.0f);
        style.setFrameRounding(3.0f);
        style.setPopupRounding(4.0f);
        style.setScrollbarRounding(9.0f);
        style.setGrabRounding(3.0f);
        style.setLogSliderDeadzone(4.0f);
        style.setTabRounding(4.0f);
//        style.setWindowMenuButtonPosition(-1);
    }
    /**
     * Run editor.
     */
    public void run() {
        Logger.log(Logger.Type.INFO, "Run editor ...");
        GLFW.glfwSwapInterval(0);
        GameObjectFactory.initialize();

        ProjectManager.getInstance().openProject("/home/mmilicevic/Desktop/test");
        EditorSceneManager.getInstance().openScene("/home/mmilicevic/Desktop/test/scenes/MainScene.scene");
        while (!GLFW.glfwWindowShouldClose(handle)) {
            GLFW.glfwPollEvents();

            EditorSceneRenderer.getInstance().render();
            GameManager.getInstance().run();

            glClearColor(0.14f, 0.16f, 0.18f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();
            ImGuizmo.beginFrame();

            ImVec2 viewportPos = ImGui.getMainViewport().getPos();
            ImVec2 viewportSize = ImGui.getMainViewport().getSize();

            float padding = 10.0f;// Adjust dock space bounds
            ImGui.setNextWindowPos(viewportPos.x + padding, viewportPos.y + padding + 40);
            ImGui.setNextWindowSize(viewportSize.x - 2 * padding, viewportSize.y - 2 * padding - 50);
            ImGui.setNextWindowBgAlpha(0.0f); // Make it transparent if needed
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 1, 1);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
            ImGui.pushStyleColor(ImGuiCol.Separator, 0.0f, 0.0f, 0.0f, 0.0f);

            ImGui.begin("DockSpace", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
            ImGui.dockSpace(ImGui.getID("DockSpace"));//, 0, 0,  ImGuiDockNodeFlags.AutoHideTabBar);
            ImGui.end();

            ImGui.popStyleColor();
            ImGui.popStyleVar(2);
            uiRenderer.render();
            ImGui.render();
            imGuiImplGl3.renderDrawData(ImGui.getDrawData());
            GLFW.glfwSwapBuffers(handle);
            droppedFiles.clear();
        }
    }

    /**
     * Dispose of editor.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of editor ...");
        GLFW.glfwDestroyWindow(handle);
    }
}
