package org.katia.gfx.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents OpenGL frame buffer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameBuffer {
    private int id;
    private int texture;
    private int rbo;
    private int width;
    private int height;
    private boolean isSelect;
}
