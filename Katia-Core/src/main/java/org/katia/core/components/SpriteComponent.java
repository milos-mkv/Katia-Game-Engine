package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.katia.Logger;
import org.katia.factory.TextureFactory;
import org.katia.gfx.Texture;

@Data
public class SpriteComponent extends Component {

    private String path;
    @JsonIgnore
    private Texture texture;

    /**
     * Sprite component constructor.
     */
    public SpriteComponent() {
        super("Sprite");
    }

    /**
     * Set texture.
     * @param path Path to texture.
     */
    public void setTexture(String path) {
        this.path = path;
        this.texture = TextureFactory.createTexture(path);
    }

    /**
     * Dispose of sprite component.
     */
    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of sprite component ...");
    }
}
