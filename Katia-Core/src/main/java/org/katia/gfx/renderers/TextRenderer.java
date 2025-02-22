package org.katia.gfx.renderers;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.Math;
import org.katia.core.GameObject;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.TextComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.gfx.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31C.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * This class is responsible for rendering game object with text components which represent text in game.
 */
public class TextRenderer extends Renderer {

    @Getter
    static TextRenderer instance = new TextRenderer();

    private int vao;
    private int staticVbo;
    private int instanceVbo;

    /**
     * Text mesh default constructor.
     */
    public TextRenderer() {
        super("text");
        Logger.log(Logger.Type.INFO, "Initialize text mesh!");
        createBuffers();
    }

    /**
     * Create text buffers.
     */
    @Override
    protected void createBuffers() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // Static VBO for vertex positions and texture coordinates
        staticVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, staticVbo);
        FloatBuffer staticBuffer = BufferUtils.createFloatBuffer(4 * 4);
        staticBuffer.put(new float[]{
                // Vertex positions (x, y) and texture coordinates (s, t)
                0.0f, 0.0f, 0.0f, 1.0f, // Bottom-left
                1.0f, 0.0f, 1.0f, 1.0f, // Bottom-right
                1.0f, 1.0f, 1.0f, 0.0f, // Top-right
                0.0f, 1.0f, 0.0f, 0.0f  // Top-left
        }).flip();
        glBufferData(GL_ARRAY_BUFFER, staticBuffer, GL_STATIC_DRAW);

        // Vertex attributes for static data
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0); // Vertex position
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES); // Texture coordinates
        glEnableVertexAttribArray(1);

        // Instance VBO for model matrices and texture mapped values
        instanceVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);
        glBufferData(GL_ARRAY_BUFFER, 1024 * (16 + 4) * Float.BYTES, GL_DYNAMIC_DRAW); // Allocate instance buffer

        // Model matrix attributes (mat4)
        for (int i = 0; i < 4; i++) {
            glVertexAttribPointer(2 + i, 4, GL_FLOAT, false, (16 + 4) * Float.BYTES, i * 4 * Float.BYTES);
            glEnableVertexAttribArray(2 + i);
            glVertexAttribDivisor(2 + i, 1);
        }

        // Texture mapped values (vec4)
        glVertexAttribPointer(6, 4, GL_FLOAT, false, (16 + 4) * Float.BYTES, 16 * Float.BYTES);
        glEnableVertexAttribArray(6);
        glVertexAttribDivisor(6, 1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    /**
     * Render game object with text component with provided camera.
     * @param gameObject GameObject with text component.
     * @param camera GameObject with camera component.
     * @param select True if we want to render it to select FrameBuffer.
     */
    public void render(GameObject gameObject, GameObject camera, boolean select) {
        TextComponent textComponent = gameObject.getComponent(TextComponent.class);
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);

        ShaderProgram shaderProgram = ShaderProgramFactory.getShaderProgram(
                select ? selectShaderName : defaultShaderName
        );
        shaderProgram.use();

        shaderProgram.setUniformMatrix4("proj", camera.getComponent(CameraComponent.class).getCameraProjection());
        shaderProgram.setUniformMatrix4("view", camera.getComponent(TransformComponent.class).getTransformMatrix().invert());
        shaderProgram.setUniformInt("uTexture", 0);
        shaderProgram.setUniformVec4("uFontColor", textComponent.getColor());
        if (select) {
            shaderProgram.setUniformInt("selectId", gameObject.getSelectID());
        }

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textComponent.getFont().getTexture().getId());

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);

        float x = transformComponent.getWorldPosition().x;
        float y = transformComponent.getWorldPosition().y;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer instanceBuffer = BufferUtils.createFloatBuffer(textComponent.getText().length() * (16 + 4));

            float cursorX = transformComponent.getWorldPosition().x;
            float cursorY = transformComponent.getWorldPosition().y;

            for (int i = 0; i < textComponent.getText().length(); i++) {
                char c = textComponent.getText().charAt(i);

                if (c == '\n') {
                    cursorX = x;
                    cursorY -= textComponent.getFont().getSize() * textComponent.getScale();
                    continue;
                }

                STBTTBakedChar glyph = textComponent.getFont().getGlyphInfo(c);

                float xoff = glyph.xoff() * textComponent.getScale();
                float yoff = glyph.yoff() * textComponent.getScale();
                float width = (glyph.x1() - glyph.x0()) * textComponent.getScale();
                float height = (glyph.y1() - glyph.y0()) * textComponent.getScale();
                float yy = cursorY + (textComponent.getFont().getTexture().getHeight() - glyph.yoff() * textComponent.getScale()) - height - textComponent.getFont().getTexture().getHeight() ;



                Matrix4f model = new Matrix4f()
                        .translate(new Vector3f(cursorX + xoff, yy, 0))
                        .scale(width, height, 1.0f);

                float[] matrixArray = new float[16];
                model.get(matrixArray);
                for (float value : matrixArray) {
                    instanceBuffer.put(value);
                }

                instanceBuffer.put(org.katia.Math.map(glyph.x0(), 0, 512, 0, 1))
                        .put(org.katia.Math.map(glyph.y0(), 0, 512, 0, 1))
                        .put(org.katia.Math.map(glyph.x1(), 0, 512, 0, 1))
                        .put(Math.map( glyph.y1(), 0, 512, 0, 1));

                cursorX += textComponent.getScale() + (glyph.xadvance() * textComponent.getScale()) ;
            }

            instanceBuffer.flip();
            glBufferSubData(GL_ARRAY_BUFFER, 0, instanceBuffer);
        }

        glDrawArraysInstanced(GL_TRIANGLE_FAN, 0, 4, textComponent.getText().length());

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Dispose of text renderer.
     */
    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of text renderer ...");
        glDeleteBuffers(instanceVbo);
        glDeleteBuffers(staticVbo);
        glDeleteVertexArrays(vao);
    }
}
