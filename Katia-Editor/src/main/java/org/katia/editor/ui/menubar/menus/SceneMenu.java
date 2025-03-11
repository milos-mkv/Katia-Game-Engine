package org.katia.editor.ui.menubar.menus;

import imgui.ImGui;
import org.katia.editor.ui.menubar.MainMenuBar;
import org.katia.editor.ui.menubar.MenuAction;

/**
 * Scene menu bar.
 * @see org.katia.editor.ui.menubar.menus.Menu
 */
public class SceneMenu extends Menu {

    private final MainMenuBar mainMenuBar;

    /**
     * Scene Menu Constructor.
     * @param mainMenuBar Main Menu Bar.
     */
    public SceneMenu(MainMenuBar mainMenuBar) {
        super("Scene");
        this.mainMenuBar = mainMenuBar;
    }

    /**
     * Render menu items.
     */
    @Override
    protected void body() {
        mainMenuBar.getActions().put(MenuAction.CREATE_NEW_SCENE, ImGui.menuItem("New Scene"));
        mainMenuBar.getActions().put(MenuAction.OPEN_SCENE, ImGui.menuItem("Open Scene"));
        mainMenuBar.getActions().put(MenuAction.SAVE_SCENE, ImGui.menuItem("Save Scene"));
    }
}
