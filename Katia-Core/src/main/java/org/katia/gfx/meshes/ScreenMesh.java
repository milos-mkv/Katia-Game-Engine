package org.katia.gfx.meshes;

import lombok.Getter;
import org.katia.Logger;
import org.katia.factory.ShaderProgramFactory;
import org.katia.game.Game;
import org.katia.gfx.FrameBuffer;
import org.katia.gfx.ShaderProgram;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class ScreenMesh {

    @Getter
    private static final ScreenMesh instance = new ScreenMesh();

    private final int vbo;
    private final int vao;
    private final int ebo;

    ShaderProgram shaderProgram;

    /**
     * Quad mesh constructor.
     */
    public ScreenMesh() {
        Logger.log(Logger.Type.INFO, "Initialize TextureMesh mesh!");
        var vertices = new float[] {
                1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
                -1.0f,  1.0f, 0.0f, 0.0f, 1.0f
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

        shaderProgram = ShaderProgramFactory.createShaderProgram("ScreenShader",
                "./Katia-Core/src/main/resources/shaders/screen.vert",
                "./Katia-Core/src/main/resources/shaders/screen.frag");
    }

    /**
     * Render quad.
     */
    public void render(FrameBuffer frameBuffer, int width, int height) {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        shaderProgram.use();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, frameBuffer.getTexture());
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
