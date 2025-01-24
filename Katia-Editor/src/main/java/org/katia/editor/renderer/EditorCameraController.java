package org.katia.editor.renderer;

import lombok.Data;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.katia.core.GameObject;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.GameObjectFactory;

@Data
public class EditorCameraController {

    @Getter
    static EditorCameraController instance = new EditorCameraController();

    GameObject camera;

    /**
     * Editor camera controller constructor.
     */
    public EditorCameraController() {
        camera = GameObjectFactory.createGameObject("Editor Camera Controller");
        camera.addComponent(new CameraComponent());
        camera.getComponent(CameraComponent.class).setBackground(new Vector3f(0.15f, 0.15f, 0.15f));
        camera.getComponent(TransformComponent.class).setPosition(new Vector3f(0, 0, 0));
    }

    /**
     * Set camera viewport. (Should be set as scene window width and height)
     * @param width Viewport width.
     * @param height Viewport height.
     */
    public void setViewport(float width, float height) {
        camera.getComponent(CameraComponent.class).setViewport(new Vector2f(width, height));
    }

    /**
     * Get camera projection matrix.
     * @return Matrix4f
     */
    public Matrix4f getProjectionMatrix() {
        var viewport = camera.getComponent(CameraComponent.class).getViewport();
        return new Matrix4f().ortho(-viewport.x / 2, viewport.x / 2, -viewport.y / 2, viewport.y /2, -2.0f, 2.0f);
    }

    /**
     * Get camera transform matrix. (camera view)
     * @return Matrix4f
     */
    public Matrix4f getViewMatrix() {
        return camera.getComponent(TransformComponent.class).getTransformMatrix();
    }

    /**
     * Set camera zoom.
     * @param delta Zoom amount.
     */
    public void zoom(float delta) {
        var scale = camera.getComponent(TransformComponent.class).getScale();
        scale.x += delta;
        scale.y += delta;
    }

    /**
     * Move camera.
     * @param deltaX Move on X axis.
     * @param deltaY Move on Y axis.
     */
    public void move(float deltaX, float deltaY) {
        camera.getComponent(TransformComponent.class).getPosition().add(new Vector3f(deltaX, deltaY, 0));
    }
}
