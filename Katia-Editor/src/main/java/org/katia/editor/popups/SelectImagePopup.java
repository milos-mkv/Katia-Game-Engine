package org.katia.editor.popups;

import lombok.Getter;

public class SelectImagePopup extends Popup {

    @Getter
    static SelectImagePopup instance = new SelectImagePopup();


    public SelectImagePopup() {
        super("Select Image", "IMAGE SELECT", 800, 600);
    }

    @Override
    public void body() {

    }
}
