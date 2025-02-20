package org.katia.gfx;

import lombok.Data;
import org.katia.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@Data
public class FrameBuffer {

    private int id;
    private int texture;
    private int rbo;
    private int width;
    private int height;

    /**
     * FrameBuffer constructor.
     * @param width FrameBuffer width.
     * @param height FrameBuffer height.
     * @param oneColor Use one color.
     */
    public FrameBuffer(int width, int height, boolean oneColor) {
        this.width = width;
        this.height = height;

        id = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, id);

        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexImage2D(GL_TEXTURE_2D, 0,
                oneColor ? GL_R32I : GL_RGB,
                width,
                height,
                0,
                oneColor ? GL_RED_INTEGER : GL_RGB,
                GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Logger.log(Logger.Type.ERROR, "Failed to create FrameBuffer!");
        } else {
            Logger.log(Logger.Type.SUCCESS, "FrameBuffer created!");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Dispose of FrameBuffer.
     */
    public void dispose() {
        Logger.log(Logger.Type.INFO, "Disposing of FrameBuffer!");
        glDeleteFramebuffers(id);
        glDeleteRenderbuffers(rbo);
        glDeleteTextures(texture);
    }
}
