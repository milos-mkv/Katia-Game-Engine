package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.joml.Vector4f;
import org.katia.Logger;
import org.katia.gfx.Font;

@Data
public class TextComponent extends Component {

    String text;
    float scale;
    Vector4f color;
    String fontPath;
    @JsonIgnore
    Font font;

    /**
     * Text component default constructor.
     */
    public TextComponent() {
        super("Text");
        this.fontPath = null;
        this.text = "";
        this.scale = 1.0f;
        this.font = null;
        this.color = new Vector4f(1, 1, 1, 1);
    }

    @JsonIgnore
    public void setFont(Font font) {
        this.font = font;
        this.fontPath = font.getPath();
    }

    /**
     * Dispose of text component.
     */
    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of text component ...");
    }
}
