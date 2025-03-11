package org.katia.editor.ui.popups;

import imgui.ImGui;
import lombok.Getter;
import org.katia.Logger;

import java.util.HashMap;

/**
 * This class holds all instance of UI popups and manages them and their actions.
 * This class should be globally accessible so we can request to open popups from any
 * part of the system.
 */
public class PopupManager {

    @Getter
    static PopupManager instance = new PopupManager();

    HashMap<Class<?>, Popup> popups;
    Class<?> popupToOpen;

    /**
     * Popup Manager Constructor.
     */
    public PopupManager() {
        Logger.log(Logger.Type.INFO, "Popup Manager Constructor");
        popups = new HashMap<>();
        popups.put(CreateProjectPopup.class, new CreateProjectPopup());
        popups.put(CreateScenePopup.class, new CreateScenePopup());
        popups.put(OpenScenePopup.class, new OpenScenePopup());
        popups.put(ImagePreviewPopup.class, new ImagePreviewPopup());
    }

    /**
     * Open popup without pass params.
     * @param popup Popup type.
     */
    public void openPopup(Class<?> popup) {
        openPopup(popup, null);
    }

    /**
     * Open popup and pass it some data.
     * @param popup Popup type.
     * @param data Custom data for provided popup.
     */
    public void openPopup(Class<?> popup, Object data) {
        Logger.log(Logger.Type.INFO, "Open Popup:", popup.toString());
        popups.get(popup).open(data);
        popupToOpen = popup;
    }

    /**
     * Render all popups.
     */
    public void render() {
        if (popupToOpen != null) {
            System.out.println(popupToOpen.toString());
            ImGui.openPopup(popupToOpen.toString());
            popupToOpen = null;
        }
        popups.forEach((_, popup) -> popup.render());
    }

    /**
     * Get popup.
     * @param popup Popup type.
     * @return Popup
     * @param <T> Popup Type
     */
    @SuppressWarnings("unchecked")
    public <T> T get(T popup) {
        return (T) popups.get(popup.getClass());
    }
}
