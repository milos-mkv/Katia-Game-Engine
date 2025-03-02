package org.katia.editor.ui;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.flag.ImGuiDockNodeFlags;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class UICoreDockWindow implements UIComponent {

    protected String name;
    protected ImGuiWindowClass windowClass;

    public UICoreDockWindow(String name) {
        this.name = name;
        this.windowClass = new ImGuiWindowClass();
        this.windowClass.setDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoDockingOverMe
                | ImGuiDockNodeFlags.NoDockingSplitMe
                | ImGuiDockNodeFlags.NoCloseButton
                | ImGuiDockNodeFlags.NoTabBar
        );
    }

    @Override
    public void render() {
        ImGui.setNextWindowClass(windowClass);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
        ImGui.begin(name);
        ImGui.textDisabled(name.toUpperCase());
        ImGui.beginChild("##" + name, -1, -1, true);

        body();

        ImGui.endChild();
        ImGui.end();
        ImGui.popStyleVar();
    }

    protected abstract void body();
}
