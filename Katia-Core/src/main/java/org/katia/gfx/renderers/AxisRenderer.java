package org.katia.gfx.renderers;

import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.game.Game;
import org.katia.gfx.resources.ShaderProgram;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

/**
 * This class is responsible for rendering x and y line axis.
 */
public class AxisRenderer extends Renderer {

    private final Game game;

    public int vao;
    public int vbo;
    public int ebo;

    /**
     * Axis Mesh default constructor.
     */
    public AxisRenderer(Game game) {
        super("axis");
        Logger.log(Logger.Type.INFO, "Creating Axis Mesh ...");
        this.game = game;
        createBuffers();
    }

    /**
     * Create buffers for axis mesh.
     */
    @Override
    protected void createBuffers() {
        var vertices = new float[] {-500f,  0f, 0.0f, 500f, 0f, 0.0f, 0f, -500f, 0.0f, 0f, 500f, 0.0f };
        int[] indices = { 0, 1, 2, 3 };

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    /**
     * Render x and y axis lines.
     */
    public void render() {
        GameObject camera = game.getSceneManager().getCamera();
        ShaderProgram shaderProgram = defaultShader;
        shaderProgram.use();
        shaderProgram.setUniformMatrix4("projection", camera.getComponent(CameraComponent.class).getCameraProjection());
        shaderProgram.setUniformMatrix4("view", camera.getComponent(TransformComponent.class).getTransformMatrix().invert());
        glBindVertexArray(vao);
        glDrawElements(GL_LINES, 4, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    /**
     * Dispose of line axis renderer.
     */
    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of line axis renderer ...");
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}
