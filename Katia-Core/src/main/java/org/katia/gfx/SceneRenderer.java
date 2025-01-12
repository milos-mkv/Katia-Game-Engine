package org.katia.gfx;

import lombok.Getter;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.gfx.meshes.AxisMesh;
import org.katia.gfx.meshes.QuadMesh;

import static org.lwjgl.opengl.GL11.*;

public class SceneRenderer {

    @Getter
    static SceneRenderer instance = new SceneRenderer();

    /**
     * Scene renderer constructor.
     */
    public SceneRenderer() {
        Logger.log(Logger.Type.INFO, "Creating scene renderer!");
    }

    /**
     * Render provided scene.
     * @param scene Scene.
     */
    public void render(Scene scene) {
        GameObject camera = scene.find("Main Camera");
        CameraComponent cameraComponent = camera.getComponent(CameraComponent.class);
        var backgroundColor = cameraComponent.getBackground();

        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1);
        glClear(GL_COLOR_BUFFER_BIT);
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
                    gameObject.getComponent(TransformComponent.class).getWorldTransformMatrix()
            );
        }
        for (GameObject child : gameObject.getChildren()) {
            renderGameObject(child);
        }
    }

}
