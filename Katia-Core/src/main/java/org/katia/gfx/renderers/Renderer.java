package org.katia.gfx.renderers;

import org.katia.Main;
import org.katia.factory.ShaderProgramFactory;
import org.katia.gfx.resources.ShaderProgram;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public abstract class Renderer {

    public static final String ShadersDir;
    static {
        try {
            ShadersDir = Paths.get(Main.class.getClassLoader().getResource("shaders/").toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    protected ShaderProgram defaultShader;
    protected ShaderProgram selectShader;

    /**
     * Mesh constructor.
     * @param shaderName Shader name.
     */
    public Renderer(String shaderName) {
        createShaders(shaderName);
    }

    /**
     * Create shaders with provided shader name.
     */
    private void createShaders(String shaderName) {
        // NOTE: Creating select shader only requires to use different fragment shader.
       selectShader = ShaderProgramFactory.createShaderProgram(
               "select/" + shaderName,
                ShadersDir +"/"+ shaderName + ".vert",
                ShadersDir +"/"+ "select.frag");

       defaultShader = ShaderProgramFactory.createShaderProgram(
                shaderName,
                ShadersDir +"/"+ shaderName + ".vert",
                ShadersDir+"/"+ shaderName + ".frag");
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
