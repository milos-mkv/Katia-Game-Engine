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
    FrameBuffer frameBuffer;
    FrameBuffer idFrameBuffer;

    public EditorSceneRenderer() {
        cameraController = EditorCameraController.getInstance();
        frameBuffer = new FrameBuffer(1920, 1080, false);
        idFrameBuffer = new FrameBuffer(1920, 1080, true);

    }

    public void render() {
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

    public void renderToFrameBuffer() {
        Scene scene = EditorSceneManager.getInstance().getScene();
        if (scene == null) {
            return;
        }

        glBindFramebuffer(GL_FRAMEBUFFER, idFrameBuffer.getId());
        glViewport(0, 0, 1920, 1080);

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        SceneRenderer.getInstance().render(scene, cameraController.getCamera(), true);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


}
