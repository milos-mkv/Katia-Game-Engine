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

import static org.katia.Math.map;

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
        camera.getComponent(CameraComponent.class).setBackground(new Vector3f(0.1f, 0.1f, 0.1f));
        // FIXME: For some reason if we start with position 0, 0 and spawn new game object Gizmo fails.
        camera.getComponent(TransformComponent.class).setPosition(new Vector3f(0.1f, 0.1f, 0));
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
     * Get camera viewport.
     * @return Vector2f
     */
    public Vector2f getViewport() {
        return camera.getComponent(CameraComponent.class).getViewport();
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

        scale.x = Math.max(0.1f, Math.min(2.0f, scale.x + delta));
        scale.y = Math.max(0.1f, Math.min(2.0f, scale.y + delta));
    }

    public Vector2f getCursorWorldPosition(float x, float y) {
        var camPos = camera.getComponent(TransformComponent.class).getPosition();
        var zoom = camera.getComponent(TransformComponent.class).getScale().x;

        float worldX = map(x, 0, getViewport().x,
                camPos.x - (getViewport().x / 2f) * zoom,
                camPos.x + (getViewport().x / 2f) * zoom);

        float worldY = map(y, 0, getViewport().y ,
                camPos.y - (getViewport().y / 2f) * zoom,
                camPos.y + (getViewport().y / 2f) * zoom);

        return new Vector2f(worldX, worldY);
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
