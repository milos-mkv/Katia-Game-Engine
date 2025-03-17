package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.luaj.vm2.LuaValue;

import java.util.*;

/**
 * GameObject script component.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScriptComponent extends Component {

    String path;

    List<Map.Entry<String, String>> params;

    @JsonIgnore
    LuaValue behaviourTable;

    /**
     * Script component constructor.
     */
    public ScriptComponent() {
        super("Script");
        this.behaviourTable = null;
        this.params = new ArrayList<>();
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
