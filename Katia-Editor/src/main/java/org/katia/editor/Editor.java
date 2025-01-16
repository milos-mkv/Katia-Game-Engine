package org.katia.editor;

import imgui.ImGuiStyle;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGui;
import lombok.Data;
import lombok.Getter;
import org.katia.Logger;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.windows.UIRenderer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.glViewport;

@Data
public class Editor {

    @Getter
    static final Editor instance = new Editor();

    long handle;
    ImGuiImplGlfw imGuiImplGlfw;
    ImGuiImplGl3 imGuiImplGl3;
    UIRenderer uiRenderer;
    public Editor() {
        Logger.log(Logger.Type.INFO, "Creating editor ...");

        createWindow();
        initializeImGui();

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
        colors[ImGuiCol.WindowBg] = new float[]{0.16f, 0.18f, 0.2f, 1.0f}; // Dark background
        colors[ImGuiCol.ChildBg] = new float[]{0.14f, 0.16f, 0.18f, 1.0f}; // Slightly darker child window
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
        colors[ImGuiCol.ScrollbarBg] = new float[]{0.14f, 0.16f, 0.18f, 1.0f}; // Dark scrollbar background
        colors[ImGuiCol.ScrollbarGrab] = new float[]{0.3f, 0.6f, 0.8f, 0.8f}; // Light blue scrollbar grab
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
        colors[ImGuiCol.TabActive] = new float[]{0.4f, 0.7f, 1.0f, 1.0f}; // Active bright blue tab
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
        style.setScrollbarSize(15.0f);
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
    }
    /**
     * Run editor.
     */
    public void run() {
        Logger.log(Logger.Type.INFO, "Run editor ...");
        GLFW.glfwSwapInterval(0);
        while (!GLFW.glfwWindowShouldClose(handle)) {
            GLFW.glfwPollEvents();
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();
            ImGui.dockSpaceOverViewport(ImGui.getMainViewport());
            ImGui.showDemoWindow();
            uiRenderer.render();
            ImGui.render();
            imGuiImplGl3.renderDrawData(ImGui.getDrawData());
            GLFW.glfwSwapBuffers(handle);
        }
    }

    public static void setupImGuiStyle2() {
        float textR = 236f / 255f, textG = 240f / 255f, textB = 241f / 255f;
        float headR = 41f / 255f, headG = 128f / 255f, headB = 185f / 255f;
        float areaR = 57f / 255f, areaG = 79f / 255f, areaB = 105f / 255f;
        float bodyR = 44f / 255f, bodyG = 62f / 255f, bodyB = 80f / 255f;
        float popsR = 33f / 255f, popsG = 46f / 255f, popsB = 60f / 255f;

        ImGui.getStyle().setColor(ImGuiCol.Text, textR, textG, textB, 1.0f);
        ImGui.getStyle().setColor(ImGuiCol.TextDisabled, textR, textG, textB, 0.58f);
        ImGui.getStyle().setColor(ImGuiCol.WindowBg, bodyR, bodyG, bodyB, 0.95f);
        ImGui.getStyle().setColor(ImGuiCol.ChildBg, areaR, areaG, areaB, 0.58f);
        ImGui.getStyle().setColor(ImGuiCol.Border, 0.5f, 0.5f, 0.5f, 1.0f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBg, areaR, areaG, areaB, 1.0f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgHovered, headR, headG, headB, 0.78f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgActive, headR, headG, headB, 1.0f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBg, areaR, areaG, areaB, 1.0f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBgActive, headR, headG, headB, 1.0f);
        ImGui.getStyle().setColor(ImGuiCol.MenuBarBg, areaR, areaG, areaB, 0.47f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarBg, areaR, areaG, areaB, 1.0f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrab, headR, headG, headB, 0.21f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrabHovered, headR, headG, headB, 0.78f);
        ImGui.getStyle().setColor(ImGuiCol.Button, headR, headG, headB, 0.50f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonHovered, headR, headG, headB, 0.86f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonActive, headR, headG, headB, 1.0f);
        ImGui.getStyle().setColor(ImGuiCol.TextSelectedBg, headR, headG, headB, 0.43f);
        ImGui.getStyle().setColor(ImGuiCol.PopupBg, popsR, popsG, popsB, 0.92f);
        ImGui.getStyle().setColor(ImGuiCol.ModalWindowDimBg, areaR, areaG, areaB, 0.73f);



        ImGuiStyle style = ImGui.getStyle();
        style.setWindowPadding(8.00f, 8.00f);
        style.setFramePadding(5.00f, 2.00f);
        style.setCellPadding(6.00f, 6.00f);
        style.setItemSpacing(6.00f, 6.00f);
        style.setItemInnerSpacing(6.00f, 6.00f);
        style.setTouchExtraPadding(0.00f, 0.00f);
        style.setIndentSpacing(25.0f);
        style.setScrollbarSize(15.0f);
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
    }
    /**
     * Dispose of editor.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of editor ...");
        GLFW.glfwDestroyWindow(handle);
    }
}
