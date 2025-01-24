package org.katia.editor.renderer;

import lombok.Data;
import lombok.Getter;
import org.joml.Vector2f;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TextComponent;
import org.katia.core.components.TransformComponent;
import org.katia.editor.managers.EditorSceneManager;
import org.katia.gfx.FrameBuffer;
import org.katia.gfx.SceneRenderer;
import org.katia.gfx.meshes.AxisMesh;
import org.katia.gfx.meshes.QuadMesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

@Data
public class EditorSceneRenderer {

    @Getter
    static EditorSceneRenderer instance = new EditorSceneRenderer();

    EditorCameraController cameraController;
    FrameBuffer frameBuffer;

    public EditorSceneRenderer() {
        cameraController = EditorCameraController.getInstance();
        frameBuffer = new FrameBuffer(1920, 1080, false);
    }

    public void render(int width, int height) {
        Scene scene = EditorSceneManager.getInstance().getScene();
        if (scene == null) {
            return;
        }

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer.getId());
        glViewport(0, 0, 1920, 1080);


        cameraController.setViewport(Settings.w, Settings.h);

        SceneRenderer.getInstance().render(scene, cameraController.getCamera());

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


}
