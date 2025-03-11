package org.katia.editor.ui.menubar.menus;

import imgui.ImGui;
import org.katia.editor.managers.EditorAssetManager;

/**
 * Base class for all menu bars.
 */
public abstract class Menu {

    protected final String name;

    /**
     * Menu Constructor.
     * @param name Menu name.
     */
    public Menu(String name) {
        this.name = name;
    }

    /**
     * Render menu.
     */
    public void render() {
        if (ImGui.beginMenu(name)) {
            ImGui.pushFont(EditorAssetManager.getInstance().getFont("Text20"));
            body();
            ImGui.popFont();
            ImGui.endMenu();
        }
    }

    /**
     * Custom body to add for each custom menu.
     */
    protected abstract void body();
}
