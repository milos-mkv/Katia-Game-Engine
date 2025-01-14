package org.katia.gfx;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TextComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.FontFactory;
import org.katia.factory.ShaderProgramFactory;
import org.katia.factory.TextureFactory;
import org.katia.gfx.meshes.AxisMesh;
import org.katia.gfx.meshes.QuadMesh;

import static org.lwjgl.opengl.GL11.*;

public class SceneRenderer {

    @Getter
    static SceneRenderer instance = new SceneRenderer();

    FontRenderer fontRenderer;
    CameraComponent camera;
    Matrix4f cameraTransform;
    /**
     * Scene renderer constructor.
     */
    public SceneRenderer() {
        Logger.log(Logger.Type.INFO, "Creating scene renderer!");
//         fontLoader = new FontLoader("./assets/Roboto-Regular.ttf", 40);

        fontRenderer = new FontRenderer();

    }

    /**
     * Render provided scene.
     * @param scene Scene.
     */
    public void render(Scene scene) {
        GameObject camera = scene.find("Main Camera");
        CameraComponent cameraComponent = camera.getComponent(CameraComponent.class);
        var backgroundColor = cameraComponent.getBackground();
       this.camera = cameraComponent;
        cameraTransform = camera.getComponent(TransformComponent.class).getTransformMatrix();
        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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
        if (spriteComponent != null) {
            fontRenderer.renderText("KATIA", -200, 300,1f, camera.getCameraProjection());
//            gameObject.getComponent(TransformComponent.class).setScale(new Vector3f(1, 1, 1));
//            gameObject.getComponent(TransformComponent.class).setRotation(0);

         //   fontRenderer.render(gameObject, cameraTransform, camera.getCameraProjection());
        }
        for (GameObject child : gameObject.getChildren()) {
            renderGameObject(child);
        }
    }

}
