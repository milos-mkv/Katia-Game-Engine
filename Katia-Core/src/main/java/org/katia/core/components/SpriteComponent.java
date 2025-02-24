package org.katia.core.components;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * GameObject sprite component.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpriteComponent extends Component {

    private String path;

    /**
     * Sprite component constructor.
     */
    public SpriteComponent() {
        super("Sprite");
    }
}