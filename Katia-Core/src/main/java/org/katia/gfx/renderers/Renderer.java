package org.katia.gfx.renderers;

import org.katia.factory.ShaderProgramFactory;

public abstract class Renderer {

    public static final String ShadersDir = "./Katia-Core/src/main/resources/shaders/";

    protected final String defaultShaderName;
    protected final String selectShaderName;

    /**
     * Mesh constructor.
     * @param shaderName Shader name.
     */
    public Renderer(String shaderName) {
        this.defaultShaderName = shaderName;
        this.selectShaderName = "select/" + shaderName;

        createShaders();
    }

    /**
     * Create shaders with provided shader name.
     */
    private void createShaders() {
        // NOTE: Creating select shader only requires to use different fragment shader.
        ShaderProgramFactory.createShaderProgram(
                selectShaderName,
                ShadersDir + defaultShaderName + ".vert",
                ShadersDir + "select.frag");

        ShaderProgramFactory.createShaderProgram(
                defaultShaderName,
                ShadersDir + defaultShaderName + ".vert",
                ShadersDir + defaultShaderName + ".frag");
    }

    /**
     * Create buffers.
     */
    protected abstract void createBuffers();

    /**
     * Dispose of created buffers.
     */
    public abstract void dispose();
}
