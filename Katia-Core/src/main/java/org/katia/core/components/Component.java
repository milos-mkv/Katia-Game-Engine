package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TransformComponent.class, name = "TransformComponent"),
        @JsonSubTypes.Type(value = SpriteComponent.class, name = "SpriteComponent"),
        @JsonSubTypes.Type(value = CameraComponent.class, name = "CameraComponent"),
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
        }
    };
    String componentType;
}
