package org.katia.editor.ui.menubar.menus;

import imgui.ImGui;
import org.katia.editor.ui.menubar.MainMenuBar;
import org.katia.editor.ui.menubar.MenuAction;

/**
 * View menu bar.
 * @see org.katia.editor.ui.menubar.menus.Menu
 */
public class ViewMenu extends Menu {

    private final MainMenuBar mainMenuBar;

    /**
     * View Menu Constructor.
     * @param mainMenuBar Main Menu Bar.
     */
    public ViewMenu(MainMenuBar mainMenuBar) {
        super("View");
        this.mainMenuBar = mainMenuBar;
    }

    /**
     * Render menu items.
     */
    @Override
    protected void body() {
        mainMenuBar.getActions().put(MenuAction.TOGGLE_HIERARCHY_WINDOW, ImGui.menuItem("Toggle Hierarchy Window"));
        mainMenuBar.getActions().put(MenuAction.TOGGLE_INSPECTOR_WINDOW, ImGui.menuItem("Toggle Inspector Window"));
        mainMenuBar.getActions().put(MenuAction.TOGGLE_PROJECT_WINDOW, ImGui.menuItem("Toggle Project Window"));
        mainMenuBar.getActions().put(MenuAction.TOGGLE_CONSOLE_WINDOW, ImGui.menuItem("Toggle Console Window"));
    }
}
