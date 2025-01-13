package org.katia.gfx;

import lombok.Data;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.katia.FileSystem;
import org.katia.core.GameObject;
import org.katia.core.components.TransformComponent;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31C.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

@Data
public class FontRenderer {
    private FontLoader fontLoader;
    private ShaderProgram shaderProgram;
    private int vao;
    private int staticVbo;
    private int instanceVbo;

    public FontRenderer(FontLoader fontLoader, ShaderProgram shaderProgram) {
        this.fontLoader = fontLoader;
        this.shaderProgram = shaderProgram;
        createBuffers();
    }
    private void createBuffers() {
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

    static float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    private Vector2f getMappedTextureCoords() {
        return new Vector2f();
    }

    public void renderText(String text, float x, float y, float scale, Matrix4f mvp) {
        shaderProgram.use();
        shaderProgram.setUniformMatrix4("uMvpMatrix", mvp);
        shaderProgram.setUniformInt("uTexture", 0);
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, fontLoader.getTexture().getId());

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer instanceBuffer = BufferUtils.createFloatBuffer(text.length() * (16 + 4));

            float cursorX = x;
            float cursorY = y;

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);

                if (c == '\n') {
                    cursorX = x;
                    cursorY -= fontLoader.getFontSize() * scale;
                    continue;
                }

                STBTTBakedChar glyph = fontLoader.getGlyphInfo(c);
                float baselineOffset = fontLoader.getFontSize() * scale;

                float a = glyph.x0();
                float a1 = glyph.y0();
                float a2 = glyph.x1();
                float a3 = glyph.y1();
                float a4 = glyph.xoff();
                float a5 = glyph.yoff();

                float a6 = glyph.xadvance();

                float xoff = glyph.xoff() * scale;
                float yoff = glyph.yoff() * scale;
                float width = (glyph.x1() - glyph.x0()) * scale;
                float height = (glyph.y1() - glyph.y0()) * scale;

                Matrix4f model = new Matrix4f()
                        .translate(new Vector3f(cursorX + xoff, cursorY + yoff -glyph.yoff(), 0))
                        .scale(width, height, 1.0f);

                float[] matrixArray = new float[16];
                model.get(matrixArray);
                for (float value : matrixArray) {
                    instanceBuffer.put(value);
                }

                instanceBuffer.put(map(glyph.x0(), 0, 512, 0, 1))
                        .put(map(glyph.y0(), 0, 512, 0, 1))
                        .put(map(glyph.x1(), 0, 512, 0, 1))
                        .put(map( glyph.y1(), 0, 512, 0, 1));

                cursorX += (glyph.x1() - glyph.x0()) * scale + 10;
            }

            instanceBuffer.flip();
            glBufferSubData(GL_ARRAY_BUFFER, 0, instanceBuffer);
        }

        glDrawArraysInstanced(GL_TRIANGLE_FAN, 0, 4, text.length());

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}