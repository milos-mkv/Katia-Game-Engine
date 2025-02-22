package org.katia.editor.renderer;

import lombok.Data;
import lombok.Getter;
import org.katia.core.Scene;
import org.katia.editor.managers.EditorSceneManager;
import org.katia.factory.ShaderProgramFactory;
import org.katia.gfx.FrameBuffer;
import org.katia.gfx.SceneRenderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30C.glClearBufferiv;

@Data
public class EditorSceneRenderer {

    @Getter
    static EditorSceneRenderer instance = new EditorSceneRenderer();

    EditorCameraController cameraController;
    FrameBuffer defaultframeBuffer;
    FrameBuffer selectFrameBuffer;

    public EditorSceneRenderer() {
        cameraController = EditorCameraController.getInstance();
        defaultframeBuffer = new FrameBuffer(1920, 1080, false);
        selectFrameBuffer = new FrameBuffer(1920, 1080, true);
    }

    public void render() {
        Scene scene = EditorSceneManager.getInstance().getScene();
        if (scene == null) {
            return;
        }
        glBindFramebuffer(GL_FRAMEBUFFER, defaultframeBuffer.getId());
        glViewport(0, 0, 1920, 1080);
        cameraController.setViewport(Settings.w, Settings.h);
        SceneRenderer.getInstance()
                .render(scene, cameraController.getCamera(), false);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Second iteration render to select frame buffer.
        glBindFramebuffer(GL_FRAMEBUFFER, selectFrameBuffer.getId());
        glViewport(0, 0, 1920, 1080);
        SceneRenderer.getInstance()
                .render(scene, cameraController.getCamera(), true);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

}
