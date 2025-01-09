package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.lang.ref.WeakReference;
import java.util.Objects;

@Data
public class TransformComponent extends Component {

    private Vector3f position;
    private float rotation;
    private Vector3f scale;

    @JsonIgnore
    private WeakReference<TransformComponent> parent;

    /**
     * Transform component default constructor.
     */
    public TransformComponent() {
        super("Transform");
        position = new Vector3f(0, 0, 0);
        rotation = 0;
        scale = new Vector3f(1, 1, 1);
        parent = null;
    }

    /**
     * Get transform matrix.
     * @return Matrix4f
     */
    @JsonIgnore
    public Matrix4f getTransformMatrix() {
        return new Matrix4f().identity()
                .translate(position)
                .rotate(rotation, new Vector3f(0, 0, 1))
                .scale(scale);
    }

    /**
     * Set parent transform component.
     * @param transformComponent Parent transform component.
     */
    public void setParent(TransformComponent transformComponent) {
        this.parent = new WeakReference<>(transformComponent);
    }

    /**
     * Get world transform matrix.
     * @return Matrix4f
     */
    @JsonIgnore
    public Matrix4f getWorldTransformMatrix() {
        if (this.parent != null) {
            return Objects.requireNonNull(this.parent.get())
                    .getWorldTransformMatrix()
                    .mul(getTransformMatrix());
        }
        return getTransformMatrix();
    }

    /**
     * Set local transform from provided world matrix representation.
     * @param transform World transform.
     */
    @JsonIgnore
    public void setTransformFromWorldMatrix(Matrix4f transform) {
        Matrix4f parentWorldTransform = new Matrix4f().identity().invert();
        if (this.parent != null) {
            parentWorldTransform = Objects.requireNonNull(this.parent.get())
                    .getWorldTransformMatrix()
                    .invert();
        }
        Matrix4f localTransformMatrix = new Matrix4f();
        parentWorldTransform.mul(transform, localTransformMatrix);

        localTransformMatrix.getTranslation(position);
        localTransformMatrix.getScale(scale);

        AxisAngle4f axisAngle4f = new AxisAngle4f();
        localTransformMatrix.getRotation(axisAngle4f);
        rotation = axisAngle4f.angle * axisAngle4f.z;
    }
}
