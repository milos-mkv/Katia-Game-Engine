package org.katia.factory;

import org.katia.core.components.CameraComponent;
import org.katia.core.components.Component;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TransformComponent;

public abstract class ComponentFactory {

    /**
     * Create component.
     * @param type Type of component as string.
     * @return Component
     */
    public static Component createComponent(String type) {
        return switch (type) {
            case "Transform" -> new TransformComponent();
            case "Sprite" -> new SpriteComponent();
            case "Camera" -> new CameraComponent();
            default -> null;
        };
    }

}
