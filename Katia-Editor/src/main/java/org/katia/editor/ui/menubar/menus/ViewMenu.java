package org.katia.editor.ui.menubar.menus;

import org.katia.editor.ui.menubar.MainMenuBar;

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

    }
}
