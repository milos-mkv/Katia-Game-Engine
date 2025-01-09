package org.katia.gfx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteProgram;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShaderProgram {

    private int id;

    /**
     * Bind shader program.
     */
    public void use() {
        glUseProgram(id);
    }

    /**
     * Set uniform matrix4.
     * @param name Uniform name.
     * @param matrix Matrix.
     */
    public void setUniformMatrix4(String name, Matrix4f matrix) {
        var buffer = new float[16];
        matrix.get(buffer);
        glUniformMatrix4fv(glGetUniformLocation(id, name), false, buffer);
    }

    /**
     * Set uniform boolean.
     * @param name Uniform name.
     * @param value Boolean value.
     */
    public void setUniformBoolean(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    /**
     * Set uniform vector3.
     * @param name Uniform name.
     * @param value Vector3 value.
     */
    public void setUniformVec3(String name, Vector3f value) {
        glUniform3f(glGetUniformLocation(id, name), value.x, value.y, value.z);
    }

    /**
     * Set uniform float.
     * @param name Uniform name.
     * @param value Float value.
     */
    public void setUniformFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(id, name), value);
    }

    /**
     * Set uniform integer.
     * @param name Uniform name.
     * @param value Integer value.
     */
    public void setUniformInt(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    /**
     * Dispose of shader program.
     */
    public void dispose() {
        glDeleteProgram(id);
    }
}
