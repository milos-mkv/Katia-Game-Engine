package org.katia.gfx;

import lombok.Setter;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TextComponent;
import org.katia.game.Game;
import org.katia.gfx.renderers.*;
import org.katia.gfx.resources.FrameBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * This class is responsible for rendering current active scene.
 */
public class SceneRenderer {

    private final Game game;

    private final AxisRenderer axisRenderer;
    private final GridRenderer gridRenderer;
    private final SpriteRenderer spriteRenderer;
    private final CameraRenderer cameraRenderer;
    private final TextRenderer textRenderer;

    boolean isSelectMode = false;

    public SceneRenderer(Game game) {
        Logger.log(Logger.Type.INFO, "Creating Scene Renderer ...");
        this.game = game;

        axisRenderer = new AxisRenderer(game);
        gridRenderer = new GridRenderer(game);
        spriteRenderer = new SpriteRenderer(game);
        cameraRenderer = new CameraRenderer(game);
        textRenderer = new TextRenderer(game);
    }

    /**
     * Render game scene into provided frame buffer. If frame buffer is <code>null</code> it will render to default
     * frame buffer.
     * @param frameBuffer Frame Buffer.
     */
    public void render(FrameBuffer frameBuffer) {
        Scene scene = game.getSceneManager().getActiveScene();

        if (frameBuffer != null) {
            glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer.getId());
            isSelectMode = frameBuffer.isSelect();

        } else {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            isSelectMode = false;
        }
        if (isSelectMode) {
            glClearColor(0, 0, 0, 1);
            glClear(GL_COLOR_BUFFER_BIT);
        } else {
            var color = game.getSceneManager().getCamera().getComponent(CameraComponent.class).getBackground();
            glClearColor(color.x, color.y, color.z, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glClear(GL_COLOR_BUFFER_BIT);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            if (game.isDebug()) {
                gridRenderer.render();
                axisRenderer.render();
            }
        }


        renderGameObject(scene.getRootGameObject());

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
            spriteRenderer.render(gameObject, isSelectMode);
        }

        CameraComponent cameraComponent = gameObject.getComponent(CameraComponent.class);
        if (cameraComponent != null) {
            cameraRenderer.render(gameObject, isSelectMode);
        }

        TextComponent textComponent = gameObject.getComponent(TextComponent.class);
        if (textComponent != null && textComponent.getPath()!= null) {
            textRenderer.render(gameObject, isSelectMode);
        }

        for (GameObject child : gameObject.getChildren()) {
            renderGameObject(child);
        }
    }
}
