package org.katia.gfx;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TextComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.gfx.meshes.AxisMesh;
import org.katia.gfx.meshes.LineRectangleMesh;
import org.katia.gfx.meshes.QuadMesh;

import static org.lwjgl.opengl.GL11.*;

public class SceneRenderer {

    @Getter
    static SceneRenderer instance = new SceneRenderer();

    FontRenderer fontRenderer;
    Matrix4f cameraTransform;
    GameObject camera;
    GridRenderer gridRenderer;
    /**
     * Scene renderer constructor.
     */
    public SceneRenderer() {
        Logger.log(Logger.Type.INFO, "Creating scene renderer!");
        fontRenderer = new FontRenderer();
        gridRenderer = new GridRenderer();

    }

    public void render(Scene scene, GameObject camera) {
        this.camera = camera;
        scene.setSize(new Vector2i(0, 0));
        CameraComponent cameraComponent = camera.getComponent(CameraComponent.class);
        var backgroundColor = cameraComponent.getBackground();
        cameraTransform = camera.getComponent(TransformComponent.class).getTransformMatrix();
        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(2);
        gridRenderer.render(camera);
        AxisMesh.getInstance().render(camera);

        QuadMesh.getInstance().use(
                cameraComponent.getCameraProjection(),
                camera.getComponent(TransformComponent.class).getTransformMatrix().invert()
        );
        renderGameObject(scene.getRootGameObject());
    }

    /**
     * Render provided scene.
     * @param scene Scene.
     */
    public void render(Scene scene) {
        camera = scene.find("Main Camera");
        scene.setSize(new Vector2i(0, 0));
        CameraComponent cameraComponent = camera.getComponent(CameraComponent.class);
        var backgroundColor = cameraComponent.getBackground();
        cameraTransform = camera.getComponent(TransformComponent.class).getTransformMatrix();
        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(2);
        gridRenderer.render(camera);
        AxisMesh.getInstance().render(camera);


        renderGameObject(scene.getRootGameObject());
    }

    /**
     * Render game object.
     * @param gameObject Game Object.
     */
    private void renderGameObject(GameObject gameObject) {
        if (!gameObject.isActive()) {
            return;
        }


        // NOTE: Render only game object that has sprite component and which texture is set.
        SpriteComponent spriteComponent = gameObject.getComponent(SpriteComponent.class);
        if (spriteComponent != null && spriteComponent.getTexture() != null) {
            var texture = spriteComponent.getTexture();
            var transform = gameObject.getComponent(TransformComponent.class)
                    .getWorldTransformMatrix()
                    .scale(texture.getWidth(), texture.getHeight(), 1);
            QuadMesh.getInstance().use(
                    camera.getComponent(CameraComponent.class).getCameraProjection(),
                    camera.getComponent(TransformComponent.class).getTransformMatrix().invert()
            );
            QuadMesh.getInstance().render(texture, transform);
        }

        TextComponent textComponent = gameObject.getComponent(TextComponent.class);
        if (textComponent != null && textComponent.getFont() != null) {
            fontRenderer.renderText(gameObject, camera);
        }

        CameraComponent cameraComponent = gameObject.getComponent(CameraComponent.class);
        if (cameraComponent != null) {
            renderCameraObject(gameObject);
        }
        for (GameObject child : gameObject.getChildren()) {
            renderGameObject(child);
        }
    }
    private void renderCameraObject(GameObject gameObject) {
        CameraComponent cameraComponent = gameObject.getComponent(CameraComponent.class);
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);

        Vector3f scale = new Vector3f(transformComponent.getScale());
        System.out.println(scale);
        transformComponent.setScale(new Vector3f(
                cameraComponent.getViewport().x,
                cameraComponent.getViewport().y, 1.0f
        ));
        Matrix4f worldTransform = transformComponent.getWorldTransformMatrix();
        QuadMesh.getInstance().use(
                camera.getComponent(CameraComponent.class).getCameraProjection(),
                camera.getComponent(TransformComponent.class).getTransformMatrix().invert()
        );
        LineRectangleMesh.getInstance().render(worldTransform, cameraComponent.getBackground());
        transformComponent.setScale(new Vector3f(scale.x,scale.y, 1.0f
        ));
    }
}
