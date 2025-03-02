package org.katia.gfx.renderers;

import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.game.Game;
import org.katia.gfx.resources.ShaderProgram;
import org.katia.gfx.resources.Texture;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

/**
 * This class is responsible for rendering game objects with sprite component.
 */
public class SpriteRenderer extends Renderer {

    private final Game game;

    private int vbo;
    private int vao;
    private int ebo;

    /**
     * Quad mesh constructor.
     */
    public SpriteRenderer(Game game) {
        super("shader");
        Logger.log(Logger.Type.INFO, "Initialize quad mesh!");
        this.game = game;
        createBuffers();
    }

    /**
     * Create buffers for sprite renderer.
     */
    @Override
    protected void createBuffers() {
        var vertices = new float[] {
                0.5f,  0.5f, 0.0f, 1.0f, 1.0f,  0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, -0.5f,  0.5f, 0.0f, 0.0f, 1.0f
        };
        var indices = new int[] { 0, 1, 3, 1, 2, 3 };
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
     * Render game object with provided camera.
     * @param gameObject GameObject with sprite component to render.
     * @param select True if we want to render it to select FrameBuffer.
     */
    public void render(GameObject gameObject, boolean select) {
        GameObject camera = game.getSceneManager().getCamera();
        ShaderProgram shaderProgram = select ? selectShader : defaultShader;
        shaderProgram.use();
        shaderProgram.setUniformMatrix4("projection", camera.getComponent(CameraComponent.class).getCameraProjection());
        shaderProgram.setUniformMatrix4("view", camera.getComponent(TransformComponent.class).getWorldTransformMatrix().invert());
        Texture texture = game.getResourceManager().getTexture(gameObject.getComponent(SpriteComponent.class).getPath()) ;
        var transform = gameObject.getComponent(TransformComponent.class)
                .getWorldTransformMatrix()
                .scale(texture.getWidth(), texture.getHeight(), 1);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getId());
        shaderProgram.setUniformMatrix4("model", transform);
        shaderProgram.setUniformBoolean("isCamera", 0);
        if (select) {
            shaderProgram.setUniformInt("selectId", gameObject.getSelectID());
        }

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    /**
     * Dispose of quad mesh.
     */
    public void dispose() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}
