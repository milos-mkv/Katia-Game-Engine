package org.katia.gfx.meshes;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.katia.factory.ShaderProgramFactory;
import org.katia.gfx.ShaderProgram;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LineRectangleMesh {

    @Getter
    static LineRectangleMesh instance = new LineRectangleMesh();

    public int vao, vbo, ebo;
    ShaderProgram shaderProgram;

    public LineRectangleMesh() {
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

        shaderProgram = ShaderProgramFactory.createShaderProgram("Default",
                "./Katia-Core/src/main/resources/shaders/shader.vert",
                "./Katia-Core/src/main/resources/shaders/shader.frag");
    }

    public void render(Matrix4f transform, Vector3f bgColor) {
        shaderProgram.setUniformMatrix4("model", transform);
        shaderProgram.setUniformBoolean("isCamera", 1);
        shaderProgram.setUniformVec3("bgColor", bgColor);

        glBindVertexArray(vao);
        glDrawElements(GL_LINES, 8, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
}
