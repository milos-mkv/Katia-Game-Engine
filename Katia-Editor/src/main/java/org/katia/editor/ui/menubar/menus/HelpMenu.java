package org.katia.editor.ui.menubar.menus;

import imgui.ImGui;
import org.katia.editor.ui.menubar.MainMenuBar;
import org.katia.editor.ui.menubar.MenuAction;

/**
 * Help menu bar.
 * @see org.katia.editor.ui.menubar.menus.Menu
 */
public class HelpMenu extends Menu {

    private final MainMenuBar mainMenuBar;

    /**
     * Help Menu Constructor.
     * @param mainMenuBar Main Menu Bar.
     */
    public HelpMenu(MainMenuBar mainMenuBar) {
        super("Help");
        this.mainMenuBar = mainMenuBar;
    }

    /**
     * Render menu items.
     */
    @Override
    protected void body() {
        mainMenuBar.getActions().put(MenuAction.ABOUT, ImGui.menuItem("About"));
   }
}
