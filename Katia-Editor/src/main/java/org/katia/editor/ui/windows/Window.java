package org.katia.editor.ui.windows;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Vector2f;
import org.katia.editor.managers.EditorAssetManager;

@Data
@NoArgsConstructor
public abstract class Window {

    protected String name;
    protected boolean visible;
    protected ImGuiWindowClass windowClass;
    protected Vector2f childPadding = new Vector2f(5, 5);
    protected int windowFlags;

    /**
     * Window constructor.
     * @param name Dock name.
     */
    public Window(String name) {
        this.name = name;
        this.visible = true;
        this.windowClass = new ImGuiWindowClass();
        this.windowClass.setDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoDockingOverMe
                | ImGuiDockNodeFlags.NoDockingSplitMe
                | ImGuiDockNodeFlags.NoCloseButton
                | ImGuiDockNodeFlags.NoTabBar
        );
        this.windowFlags = ImGuiWindowFlags.None;
    }

    /**
     * Render dock.
     */
    public void render() {
        if (!visible) return;

        ImGui.setNextWindowClass(windowClass);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 0);
        ImGui.begin(name);
        ImGui.pushFont(EditorAssetManager.getInstance().getFont("Default"));
        ImGui.textDisabled(name.toUpperCase());
        ImGui.popFont();
        ImGui.sameLine();

        header();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, childPadding.x, childPadding.y);

        ImGui.beginChild("##" + name, -1, -1, true, windowFlags);

            body();

        ImGui.endChild();
        ImGui.popStyleVar();
        ImGui.end();
        ImGui.popStyleVar();
    }

    protected void header() {
        ImGui.newLine();
    }

    protected abstract void body();
}
