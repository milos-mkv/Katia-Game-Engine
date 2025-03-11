package org.katia.editor.ui.menubar.menus;

import imgui.ImGui;
import org.katia.editor.ui.menubar.MainMenuBar;
import org.katia.editor.ui.menubar.MenuAction;

/**
 * File menu bar.
 * @see org.katia.editor.ui.menubar.menus.Menu
 */
public class FileMenu extends Menu {

    private final MainMenuBar mainMenuBar;

    /**
     * File Menu Constructor.
     * @param mainMenuBar Main Menu Bar.
     */
    public FileMenu(MainMenuBar mainMenuBar) {
        super("File");
        this.mainMenuBar = mainMenuBar;
    }

    /**
     * Render menu items.
     */
    @Override
    protected void body() {
        mainMenuBar.getActions().put(MenuAction.CREATE_NEW_PROJECT, ImGui.menuItem("New Project", "Ctrl+N"));
        mainMenuBar.getActions().put(MenuAction.OPEN_PROJECT, ImGui.menuItem("Open Project", "Ctrl+O"));
        ImGui.separator();
        mainMenuBar.getActions().put(MenuAction.EXIT, ImGui.menuItem("Exit", "Ctrl+W"));
    }
}
