package org.katia.core.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.katia.core.Scene;
import org.luaj.vm2.LuaValue;

@Data
public class ScriptComponent extends Component {

    private String path;
    @JsonIgnore
    private LuaValue behaviourTable;

    public ScriptComponent() {
        super("Script");
    }

    /**
     * Attach script file to component.
     * @param scene Scene.
     * @param path Path to lua file.
     */
    public void addScriptFile(Scene scene, String path) {
        this.path = path;
        if (path != null) {
            LuaValue module = scene.getGlobals().loadfile(path);
            this.behaviourTable = module.call();
        }
    }

    @Override
    public void dispose() {
        this.behaviourTable = null;
    }
}
