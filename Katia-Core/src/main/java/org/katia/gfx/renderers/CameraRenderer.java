package org.katia.gfx.renderers;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.game.Game;
import org.katia.gfx.resources.ShaderProgram;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

/**
 * This class is responsible for rendering game objects with camera component.
 */
public class CameraRenderer extends Renderer {

    private final Game game;

    public int vao;
    public int vbo;
    public int ebo;

    /**
     * Line Rectangle Mesh constructor.
     */
    public CameraRenderer(Game game) {
        super("shader");
        Logger.log(Logger.Type.INFO, "Create Line Rectangle Mesh ...");
        this.game = game;
        createBuffers();
    }

    /**
     * Create buffers for line rectangle mesh.
     */
    @Override
    protected void createBuffers() {
        var vertices = new float[] {
                0.5f,  0.5f, 0.0f, 1.0f, 1.0f,
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
                -0.5f,  0.5f, 0.0f, 0.0f, 1.0f
        };

        int[] indices = {
                0, 1,  // Line from top right to bottom right
                1, 2,  // Line from bottom right to bottom left
                2, 3,  // Line from bottom left to top left
                3, 0   // Line from top left to top right
        };

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    /**
     * Render rectangle of provided transform and color.
     * @param gameObject Transform matrix.
     */
    public void render(GameObject gameObject,  boolean select) {
        GameObject camera = game.getSceneManager().getCamera();
        CameraComponent cameraComponent = gameObject.getComponent(CameraComponent.class);
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);

        Vector3f scale = new Vector3f(transformComponent.getScale());
        transformComponent.setScale(new Vector3f(cameraComponent.getViewport(), 1.0f));
        Matrix4f worldTransform = transformComponent.getWorldTransformMatrix();

        ShaderProgram shaderProgram = select ? selectShader : defaultShader;
        shaderProgram.use();
        shaderProgram.setUniformMatrix4("projection", camera.getComponent(CameraComponent.class).getCameraProjection());
        shaderProgram.setUniformMatrix4("view", camera.getComponent(TransformComponent.class).getWorldTransformMatrix().invert());
        shaderProgram.setUniformMatrix4("model", worldTransform);
        shaderProgram.setUniformBoolean("isCamera", 1);
        shaderProgram.setUniformVec3("bgColor", new Vector3f(1, 1, 1));
        if (select) {
            shaderProgram.setUniformInt("selectId", gameObject.getSelectID());
        }
        glBindVertexArray(vao);
        glDrawElements(GL_LINES, 8, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        transformComponent.setScale(new Vector3f(scale.x,scale.y, 1.0f));
    }

    /**
     * Dispose of line rectangle mesh.
     */
    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of line rectangle mesh ...");
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        glDeleteBuffers(ebo);
    }
}
