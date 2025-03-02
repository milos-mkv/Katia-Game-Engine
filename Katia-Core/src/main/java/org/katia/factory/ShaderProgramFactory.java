package org.katia.factory;

import lombok.Data;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.gfx.resources.ShaderProgram;


import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * This class is responsible for creating shader programs.
 */
public abstract class ShaderProgramFactory {

    @Data
    static class Shader {

        protected int id;

        /**
         * Shader constructor.
         * @param type Shader type.
         * @param code Shader code.
         */
        public Shader(int type, String code) {
            id = glCreateShader(type);
            glShaderSource(id, code);
            glCompileShader(id);

            String err = glGetShaderInfoLog(id, glGetShaderi(id, GL_INFO_LOG_LENGTH));
            if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
                Logger.log(Logger.Type.ERROR, "Failed to compile shader program:", err);
            }
        }

        /**
         * Dispose of shader.
         */
        public void dispose() {
            Logger.log(Logger.Type.INFO, "Disposing of shader:", String.valueOf(id));
            glDeleteShader(id);
        }
    }

    /**
     * Create shader program.
     * @param name Shader program name.
     * @param vertexShaderFilePath Path to vertex shader code file.
     * @param fragmentShaderFilePath Path to fragment shader code file.
     * @return ShaderProgram
     */
    public static ShaderProgram createShaderProgram(String name, String vertexShaderFilePath, String fragmentShaderFilePath) {
        Logger.log(Logger.Type.INFO, "Creating shader program:", name);
        Shader vert = new Shader(GL_VERTEX_SHADER, FileSystem.readFromFile(vertexShaderFilePath));
        Shader frag = new Shader(GL_FRAGMENT_SHADER, FileSystem.readFromFile(fragmentShaderFilePath));

        int id = glCreateProgram();
        glAttachShader(id, vert.getId());
        glAttachShader(id, frag.getId());
        glLinkProgram(id);

        String err = glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH));
        if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
            System.out.println(err);
            Logger.log(Logger.Type.ERROR, err);
            return null;
        }
        vert.dispose();
        frag.dispose();

        ShaderProgram shaderProgram = new ShaderProgram(id);

        Logger.log(Logger.Type.SUCCESS, "Shader program created:", name);
        return shaderProgram;
    }

    /**
     * Dispose of shader program.
     */
    public void dispose(ShaderProgram shaderProgram) {
        Logger.log(Logger.Type.DISPOSE, "Disposing of shader program:", String.valueOf(shaderProgram.getId()));
        glDeleteProgram(shaderProgram.getId());
    }
}