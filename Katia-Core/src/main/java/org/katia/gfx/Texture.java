package org.katia.gfx;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Texture {

    private String path;
    private int id;
    private int width;
    private int height;

}
