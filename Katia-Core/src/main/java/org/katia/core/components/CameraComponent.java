package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * GameObject camera component.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CameraComponent extends Component {

    Vector2f viewport;
    Vector3f background;

    /**
     * Camera component default constructor.
     */
    public CameraComponent() {
        super("Camera");
        this.viewport = new Vector2f(0, 0);
        this.background = new Vector3f(0, 0, 0);
    }

    /**
     * Get camera projection matrix.
     * @return Matrix4f
     */
    @JsonIgnore
    public Matrix4f getCameraProjection() {
        return new Matrix4f().ortho(-viewport.x / 2, viewport.x / 2, -viewport.y / 2, viewport.y /2, -1.0f, 1.0f);
    }
}
