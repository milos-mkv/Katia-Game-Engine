package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.katia.Logger;
import org.luaj.vm2.LuaValue;

@Data
public class ScriptComponent extends Component {

    String name;
    String path;
    @JsonIgnore
    LuaValue behaviourTable;

    /**
     * Script component constructor.
     */
    public ScriptComponent() {
        super("Script");
    }

    /**
     * Attach script file to component.
     * @param name Behaviour class name.
     * @param path Path to lua file.
     */
    public void addScriptFile(String name, String path) {
        this.path = path;
        this.name = name;
    }

    /**
     * Dispose of script component.
     */
    @Override
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of script component ...");
        this.behaviourTable = null;
    }
}
