package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.luaj.vm2.LuaValue;

/**
 * GameObject script component.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScriptComponent extends Component {

    String path;
    @JsonIgnore
    LuaValue behaviourTable;

    /**
     * Script component constructor.
     */
    public ScriptComponent() {
        super("Script");
        this.behaviourTable = null;
    }

    /**
     * Dispose of script component.
     */
    @Override
    public void dispose() {
        super.dispose();
        this.behaviourTable = null;
    }
}
