package org.katia.core.components;

import lombok.Data;
import org.katia.Logger;

@Data
public class TextComponent extends Component {

    String text;
    int textSize;

    /**
     * Text component default constructor.
     */
    public TextComponent() {
        super("Text");
        this.text = "";
        this.textSize = 24;
    }

    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of text component ...");
    }
}
