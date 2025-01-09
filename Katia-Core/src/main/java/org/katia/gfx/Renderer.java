package org.katia.gfx;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ShaderProgramFactory;
import org.katia.factory.TextureFactory;
import org.katia.gfx.meshes.QuadMesh;
import org.katia.gfx.meshes.TextureMesh;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer {

    QuadMesh quadMesh;
    ShaderProgram defaultShaderProgram;
    ShaderProgram textureShader;
    Texture t;
    public Renderer() {
        Logger.log(Logger.Type.INFO, "Creating renderer!");
        quadMesh = QuadMesh.getInstance();

        defaultShaderProgram = ShaderProgramFactory.createShaderProgram("Default",
                "./Katia-Core/src/main/resources/shaders/shader.vert",
                "./Katia-Core/src/main/resources/shaders/shader.frag");

        textureShader = ShaderProgramFactory.createShaderProgram("Texture",
                "./Katia-Core/src/main/resources/shaders/texture.vert",
                "./Katia-Core/src/main/resources/shaders/texture.frag");

        t = TextureFactory.createTexture("C:\\Users\\milos\\OneDrive\\Pictures\\Screenshots\\Screenshot 2024-03-10 192344.png");

    }

    public void render(Scene scene, int w, int h) {
        glBindFramebuffer(GL_FRAMEBUFFER, scene.getFrameBuffer().getId());
        glViewport(0, 0, scene.getFrameBuffer().getWidth(), scene.getFrameBuffer().getHeight());
        Logger.log(String.valueOf(scene.getFrameBuffer().getHeight()));
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        defaultShaderProgram.use();

        GameObject camera = scene.find("Main Camera");
        defaultShaderProgram.setUniformMatrix4("projection",camera.getComponent(CameraComponent.class).getCameraProjection());
        defaultShaderProgram.setUniformMatrix4("view", camera.getComponent(TransformComponent.class).getTransformMatrix().invert());

        renderGameObject(scene.getRootGameObject());
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glViewport(0, 0, w, h);

        textureShader.use();
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, scene.getFrameBuffer().getTexture());
        TextureMesh.getInstance().render();

    }

    private void renderGameObject(GameObject gameObject) {
        SpriteComponent spriteComponent = gameObject.getComponent(SpriteComponent.class);
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
        if (spriteComponent != null && spriteComponent.getTexture() != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, spriteComponent.getTexture().getId());
            defaultShaderProgram.setUniformMatrix4("model", transformComponent.getTransformMatrix());
            quadMesh.render();
        }
        for (GameObject child : gameObject.getChildren()) {
            renderGameObject(child);
        }
    }

}
