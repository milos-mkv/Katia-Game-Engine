package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.katia.Logger;


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
        @JsonSubTypes.Type(value = AnimationComponent.class, name = "AnimationComponent"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Component {

    String componentType;

    /**
     * Dispose of component.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "\tDisposing of component:", componentType);
    }
}
