package org.katia.editor.renderer;

import lombok.Data;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.katia.Logger;
import org.katia.core.Scene;
import org.katia.editor.managers.ProjectManager;
import org.katia.factory.FrameBufferFactory;
import org.katia.game.Game;
import org.katia.gfx.resources.FrameBuffer;

import static org.lwjgl.opengl.GL11C.glViewport;

/**
 * This class is responsible for rendering current active scene in two separate frame buffers.
 */
@Data
public class EditorSceneRenderer {

    @Getter
    static EditorSceneRenderer instance = new EditorSceneRenderer();

    EditorCameraController cameraController;
    FrameBuffer defaultframeBuffer;
    FrameBuffer selectFrameBuffer;

    public EditorSceneRenderer() {
        Logger.log(Logger.Type.INFO, "Editor Scene Renderer Constructor");
        cameraController = EditorCameraController.getInstance();
        defaultframeBuffer = FrameBufferFactory.createDefaultFrameBuffer(1920, 1080);
        selectFrameBuffer = FrameBufferFactory.createSelectFrameBuffer(1920, 1080);
    }

    public void render() {
        Game game = ProjectManager.getGame();
        if (game == null || game.getSceneManager().getActiveScene() == null) {
            return;
        }
        Vector2f viewport = cameraController.getViewport();
        if (Settings.w != viewport.x || Settings.h != viewport.y) {
            Logger.disable();
            FrameBufferFactory.dispose(defaultframeBuffer);
            FrameBufferFactory.dispose(selectFrameBuffer);
            setDefaultframeBuffer(FrameBufferFactory.createDefaultFrameBuffer((int)Settings.w, (int) Settings.h));
            setSelectFrameBuffer(FrameBufferFactory.createSelectFrameBuffer((int)Settings.w, (int) Settings.h));
            Logger.enable();
        }
        EditorCameraController.getInstance().setViewport(Settings.w, Settings.h);

        game.setDebug(true);
        game.getSceneManager().setCamera(EditorCameraController.getInstance().getCamera());
        glViewport(0, 0, (int)Settings.w, (int) Settings.h);
        game.getSceneRenderer().render(defaultframeBuffer);
        game.getSceneRenderer().render(selectFrameBuffer);

    }

}
