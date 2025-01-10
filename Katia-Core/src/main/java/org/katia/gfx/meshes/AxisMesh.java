package org.katia.gfx.meshes;

import lombok.Getter;
import org.katia.core.GameObject;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.gfx.ShaderProgram;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class AxisMesh {


    @Getter
    private static final AxisMesh instance = new AxisMesh();

    ShaderProgram shaderProgram;

    public final int vao;
    public final int vbo;
    public final int ebo;

    public AxisMesh() {
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

        shaderProgram = ShaderProgramFactory.createShaderProgram("Axis",
                "./Katia-Core/src/main/resources/shaders/axis.vert",
                "./Katia-Core/src/main/resources/shaders/axis.frag"
        );
    }

    public void render(GameObject camera) {
        shaderProgram.use();
        shaderProgram.setUniformMatrix4("projection", camera.getComponent(CameraComponent.class).getCameraProjection());
        shaderProgram.setUniformMatrix4("view", camera.getComponent(TransformComponent.class).getTransformMatrix().invert());
        glBindVertexArray(vao);
        glDrawElements(GL_LINES, 4, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void dispose() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}
