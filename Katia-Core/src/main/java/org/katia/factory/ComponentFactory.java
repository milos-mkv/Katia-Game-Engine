package org.katia.factory;

import org.katia.core.components.*;

/**
 * Class responsible for component generation and stuff.
 */
public abstract class ComponentFactory {

    /**
     * Get component class based on string representation of it.
     * @param type Type as string.
     * @return Class
     */
    public static Class<?> getComponentClass(String type) {
        return switch (type) {
            case "Transform" -> TransformComponent.class;
            case "Sprite" -> SpriteComponent.class;
            case "Camera" -> CameraComponent.class;
            case "Script" -> ScriptComponent.class;
            case "Text" -> TextComponent.class;
            default -> null;
        };
    }

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
            case "Script" -> new ScriptComponent();
            case "Text" -> new TextComponent();
            default -> null;
        };
    }
}
