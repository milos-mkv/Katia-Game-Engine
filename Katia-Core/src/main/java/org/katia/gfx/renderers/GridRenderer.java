package org.katia.gfx.renderers;

import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.game.Game;
import org.katia.gfx.resources.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

/**
 * This class is responsible for rendering infinite 2d gird in debug purposes. Used in editor mostly.
 */
public class GridRenderer extends Renderer {

    private final Game game;

    private int vbo;
    private int vao;

    private static final float GRID_SIZE = 100.0f;  // Block size for grid
    private static final int MAX_LINES = 400;       // Maximum number of grid lines

    /**
     * Grid renderer default constructor.
     */
    public GridRenderer(Game game) {
        super("grid");
        this.game = game;
        createBuffers();
    }

    /**
     * Crete buffers for grid renderer.
     */
    @Override
    protected void createBuffers() {
        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(vao);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        // Allocate enough space for maximum grid lines
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_LINES * 2 * 3 * Float.BYTES, GL15.GL_DYNAMIC_DRAW);

        GL30.glEnableVertexAttribArray(0);
        GL30.glVertexAttribPointer(0, 3, GL15.GL_FLOAT, false, 3 * Float.BYTES, 0);

        GL30.glBindVertexArray(0);
    }

    /**
     * Render 2D grid that fallows provided game objet with camera component.
     */
    public void render() {
        GameObject camera = game.getSceneManager().getCamera();
        CameraComponent cameraComponent = camera.getComponent(CameraComponent.class);
        TransformComponent cameraTransform = camera.getComponent(TransformComponent.class);

        Vector3f cameraPosition = cameraTransform.getPosition();
        float cameraX = cameraPosition.x;
        float cameraY = cameraPosition.y;

        float halfGridCount = 20; // Visible grid lines around the camera
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(MAX_LINES * 2 * 3);

        // Generate grid vertices dynamically
        for (float x = cameraX - (cameraX % GRID_SIZE) - GRID_SIZE * halfGridCount;
             x <= cameraX + (cameraX % GRID_SIZE) + GRID_SIZE * halfGridCount;
             x += GRID_SIZE) {
            vertexBuffer.put(x).put(cameraY - GRID_SIZE * halfGridCount).put(0.0f);
            vertexBuffer.put(x).put(cameraY + GRID_SIZE * halfGridCount).put(0.0f);
        }

        for (float y = cameraY - (cameraY % GRID_SIZE) - GRID_SIZE * halfGridCount;
             y <= cameraY + (cameraY % GRID_SIZE) + GRID_SIZE * halfGridCount;
             y += GRID_SIZE) {
            vertexBuffer.put(cameraX - GRID_SIZE * halfGridCount).put(y).put(0.0f);
            vertexBuffer.put(cameraX + GRID_SIZE * halfGridCount).put(y).put(0.0f);
        }

        vertexBuffer.flip();
        ShaderProgram shaderProgram = defaultShader;
        shaderProgram.use();
        shaderProgram.setUniformMatrix4("projection", cameraComponent.getCameraProjection());
        shaderProgram.setUniformMatrix4("view", cameraTransform.getTransformMatrix().invert());

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexBuffer);
        GL30.glDrawArrays(GL15.GL_LINES, 0, vertexBuffer.limit() / 3);
        GL30.glBindVertexArray(0);
    }

    /**
     * Dispose of gird renderer.
     */
    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of grid renderer ...");
        GL15.glDeleteBuffers(vbo);
        GL30.glDeleteVertexArrays(vao);
    }
}
