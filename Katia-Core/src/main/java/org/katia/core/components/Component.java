package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * Base class for all game object components.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TransformComponent.class, name = "TransformComponent"),
        @JsonSubTypes.Type(value = SpriteComponent.class, name = "SpriteComponent"),
        @JsonSubTypes.Type(value = CameraComponent.class, name = "CameraComponent"),
        @JsonSubTypes.Type(value = ScriptComponent.class, name = "ScriptComponent"),
        @JsonSubTypes.Type(value = TextComponent.class, name = "TextComponent"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Component {
    @JsonIgnore
    public static HashMap<String, Class<?>> components = new HashMap<>() {
        {
            put("Transform", TransformComponent.class);
            put("Sprite", SpriteComponent.class);
            put("Camera", CameraComponent.class);
            put("Script", ScriptComponent.class);
            put("Text", TextComponent.class);
        }
    };

    String componentType;

    /**
     * Dispose of component.
     */
    public abstract void dispose();
}
