package org.katia.gfx.resources;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents texture.
 */
@Data
@AllArgsConstructor
public class Texture {
    private String path;
    private int id;
    private int width;
    private int height;
}
