package org.katia.gfx;

import lombok.Getter;
import org.joml.Vector2i;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TextComponent;
import org.katia.gfx.renderers.*;

import static org.lwjgl.opengl.GL11.*;

public class SceneRenderer {

    @Getter
    static SceneRenderer instance = new SceneRenderer();

    GameObject camera;
    boolean isSelectMode = false;

    /**
     * Scene renderer constructor.
     */
    public SceneRenderer() {
        Logger.log(Logger.Type.INFO, "Creating scene renderer!");
    }

    /**
     *
     * @param scene Scene to render.
     * @param camera GameObject with camera component to use.
     * @param select True if we want to render it to select FrameBuffer.
     */
    public void render(Scene scene, GameObject camera, boolean select) {
        isSelectMode = select;
        this.camera = camera;
        CameraComponent cameraComponent = camera.getComponent(CameraComponent.class);
        var backgroundColor = cameraComponent.getBackground();
        if (!select) {
            glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GridRenderer.getInstance().render(camera);
            AxisRenderer.getInstance().render(camera);
        } else {
            glClearColor(0, 0, 0, 1);
            glClear(GL_COLOR_BUFFER_BIT);
        }
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

        SpriteComponent spriteComponent = gameObject.getComponent(SpriteComponent.class);
        if (spriteComponent != null && spriteComponent.getPath() != null) {
            SpriteRenderer.getInstance().render(gameObject, camera, isSelectMode);
        }

        CameraComponent cameraComponent = gameObject.getComponent(CameraComponent.class);
        if (cameraComponent != null) {
            CameraRenderer.getInstance().render(gameObject, camera, isSelectMode);
        }

        TextComponent textComponent = gameObject.getComponent(TextComponent.class);
        if (textComponent != null && textComponent.getPath()!= null) {
            TextRenderer.getInstance().render(gameObject, camera, isSelectMode);
        }

        for (GameObject child : gameObject.getChildren()) {
            renderGameObject(child);
        }
    }
}
