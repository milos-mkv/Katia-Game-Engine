package org.katia.core;

import lombok.Getter;
import org.katia.Logger;
import org.katia.core.components.ScriptComponent;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class ScriptExecutioner {

    @Getter
    static ScriptExecutioner instance = new ScriptExecutioner();

    Scene scene;

    /**
     * Script executioner constructor.
     */
    public ScriptExecutioner() {
        Logger.log(Logger.Type.INFO, "Creating script executioner!");
        this.scene = null;
    }

    /**
     * Call all init methods for all game objects in provided scene.
     * @param scene Scene.
     */
    public void initialize(Scene scene) {
        Logger.log(Logger.Type.INFO, "Run script executioner init method!");
        this.scene = scene;
        executeInit(scene.getRootGameObject());
    }

    /**
     * Call all update methods for all game objects in current active scene.
     * @param dt Delta time.
     */
    public void update(float dt) {
        executeUpdate(scene.getRootGameObject(), dt);
    }

    /**
     * Call init method for provided game object.
     * @param gameObject Game Object.
     */
    private void executeInit(GameObject gameObject) {
        ScriptComponent scriptComponent = gameObject.getComponent(ScriptComponent.class);
        if (scriptComponent != null && scriptComponent.getPath() != null && scriptComponent.getBehaviourTable() != null) {
            var initMethod = scriptComponent.getBehaviourTable().get("init");
            LuaTable params = new LuaTable();
            params.set("gameObject", CoerceJavaToLua.coerce(gameObject));
            params.set("scene", CoerceJavaToLua.coerce(scene));
            initMethod.call(scriptComponent.getBehaviourTable(), params);
        }
        for(GameObject child : gameObject.getChildren()) {
            executeInit(child);
        }
    }

    /**
     * Call update method for provided game object.
     * @param gameObject Game Object.
     * @param dt Delta time.
     */
    private void executeUpdate(GameObject gameObject, float dt) {
        ScriptComponent scriptComponent = gameObject.getComponent(ScriptComponent.class);
        if (scriptComponent != null && scriptComponent.getPath() != null && scriptComponent.getBehaviourTable() != null) {
            var updateMethod = scriptComponent.getBehaviourTable().get("update");
            updateMethod.call(scriptComponent.getBehaviourTable(), LuaValue.valueOf(dt));
        }
        for (int i = gameObject.getChildren().size() - 1; i >= 0 ; i--) {
            executeUpdate(gameObject.getChildren().get(i), dt);
        }
    }
}
