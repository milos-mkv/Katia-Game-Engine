package org.katia.gfx;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TextComponent;
import org.katia.core.components.TransformComponent;
import org.katia.gfx.meshes.AxisMesh;
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

        QuadMesh.getInstance().use(
                cameraComponent.getCameraProjection(),
                camera.getComponent(TransformComponent.class).getTransformMatrix().invert()
        );
        renderGameObject(scene.getRootGameObject());
    }

    /**
     * Render game object.
     * @param gameObject Game Object.
     */
    private void renderGameObject(GameObject gameObject) {
        SpriteComponent spriteComponent = gameObject.getComponent(SpriteComponent.class);
        // NOTE: Render only game object that has sprite component and which texture is set.
        if (spriteComponent != null && spriteComponent.getTexture() != null) {
            QuadMesh.getInstance().render(
                    spriteComponent.getTexture(),
//                    TextureFactory.createTexture("test.png"),
                    gameObject.getComponent(TransformComponent.class).getWorldTransformMatrix()
            );
        }
        TextComponent textComponent = gameObject.getComponent(TextComponent.class);
        if (textComponent != null) {
            fontRenderer.renderText(gameObject, camera);
//            fontRenderer.renderText("KATIA", -200, 300,1f, camera.getCameraProjection());
        }
        for (GameObject child : gameObject.getChildren()) {
            renderGameObject(child);
        }
    }

}
