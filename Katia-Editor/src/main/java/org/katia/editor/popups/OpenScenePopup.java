package org.katia.editor.popups;

import imgui.ImGui;
import lombok.Getter;

public class OpenScenePopup extends Popup {

    @Getter
    static OpenScenePopup instance = new OpenScenePopup();

    public OpenScenePopup() {
        super("Open Scene", "OPEN SCENE", 500, 400);
    }

    @Override
    public void body() {
        ImGui.beginChild("##Child", -1, -1);

        ImGui.endChild();
    }
}
