package org.katia.gfx.meshes;

import lombok.Data;
import lombok.Getter;
import org.joml.Matrix4f;
import org.katia.Logger;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.gfx.ShaderProgram;
import org.katia.gfx.Texture;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

@Data
public class QuadMesh {

    @Getter
    private static final QuadMesh instance = new QuadMesh();

    ShaderProgram shaderProgram;

    private final int vbo;
    private final int vao;
    private final int ebo;

    boolean isSelect = false;
    /**
     * Quad mesh constructor.
     */
    public QuadMesh() {
        Logger.log(Logger.Type.INFO, "Initialize quad mesh!");
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

        shaderProgram = ShaderProgramFactory.createShaderProgram("Select",
                "./Katia-Core/src/main/resources/shaders/select/select.vert",
                "./Katia-Core/src/main/resources/shaders/select/shader.frag");

        shaderProgram = ShaderProgramFactory.createShaderProgram("Default",
                "./Katia-Core/src/main/resources/shaders/shader.vert",
                "./Katia-Core/src/main/resources/shaders/shader.frag");
    }

    /**
     * Set camera projection and view for quad mesh rendering.
     * @param projection Camera projection matrix.
     * @param view Camera view matrix.
     */
    public void use(Matrix4f projection, Matrix4f view) {
        shaderProgram = ShaderProgramFactory.getShaderProgram("Default");
        isSelect = false;
        shaderProgram.use();
        shaderProgram.setUniformMatrix4("projection",projection);
        shaderProgram.setUniformMatrix4("view", view);
    }

    public void use(Matrix4f projection, Matrix4f view, boolean s) {
        shaderProgram = ShaderProgramFactory.getShaderProgram("Select");
        isSelect = true;
        shaderProgram.use();
        shaderProgram.setUniformMatrix4("projection",projection);
        shaderProgram.setUniformMatrix4("view", view);
    }

    /**
     * Render quad.
     */
    public void render(Texture texture, Matrix4f transform) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getId());
        shaderProgram.setUniformMatrix4("model", transform);
        shaderProgram.setUniformBoolean("isCamera", 0);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    /**
     * Render quad.
     */
    public void render(Texture texture, Matrix4f transform, int id) {
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture.getId());
        shaderProgram.setUniformMatrix4("model", transform);
        shaderProgram.setUniformInt("selectId", id);

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
