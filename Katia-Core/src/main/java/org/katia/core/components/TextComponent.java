package org.katia.core.components;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joml.Vector4f;

/**
 * GameObject text component.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TextComponent extends Component {

    private String text;
    private String path;
    private Vector4f color;
    private float scale;

    /**
     * Text component default constructor.
     */
    public TextComponent() {
        super("Text");
        this.path = null;
        this.text = "";
        this.scale = 1.0f;
        this.color = new Vector4f(1, 1, 1, 1);
    }
}