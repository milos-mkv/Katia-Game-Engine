package org.katia.factory;

import org.katia.Logger;
import org.katia.gfx.resources.FrameBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class is responsible for creating OpenGL frame buffers.
 * @see FrameBuffer
 */
public abstract class FrameBufferFactory {

    /**
     * Create default frame buffer.
     * @param width Buffer width.
     * @param height Buffer height.
     * @return FrameBuffer
     */
    public static FrameBuffer createDefaultFrameBuffer(int width, int height) {
        return createFrameBuffer(width, height, false);
    }

    /**
     * Create select frame buffer.
     * @param width Buffer width.
     * @param height Buffer height.
     * @return FrameBuffer
     */
    public static FrameBuffer createSelectFrameBuffer(int width, int height) {
        return createFrameBuffer(width, height, true);
    }

    /**
     * Create frame buffer.
     * @param width Buffer width.
     * @param height Buffer height.
     * @param oneColor Use one color.
     */
    public static FrameBuffer createFrameBuffer(int width, int height, boolean oneColor) {
        Logger.log(Logger.Type.INFO, "Creating frame buffer ...");
        int id = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, id);

        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexImage2D(GL_TEXTURE_2D, 0, oneColor ? GL_R32I : GL_RGB, width, height, 0, oneColor ? GL_RED_INTEGER : GL_RGB, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        FrameBuffer frameBuffer = null;
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Logger.log(Logger.Type.ERROR, "Failed to create FrameBuffer!");
        } else {
            Logger.log(Logger.Type.SUCCESS, "FrameBuffer created!");
            frameBuffer = new FrameBuffer(id, texture, rbo, width, height, oneColor);
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return frameBuffer;
    }

    /**
     * Dispose of frame buffer.
     * @param frameBuffer FrameBuffer
     */
    public static void dispose(FrameBuffer frameBuffer) {
        Logger.log(Logger.Type.DISPOSE, "Disposing of FrameBuffer:", String.valueOf(frameBuffer.getId()));
        glDeleteFramebuffers(frameBuffer.getId());
        glDeleteRenderbuffers(frameBuffer.getRbo());
        glDeleteTextures(frameBuffer.getTexture());
    }
}
