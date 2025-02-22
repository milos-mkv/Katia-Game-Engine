package org.katia.scripting;

import org.katia.Logger;
import org.katia.Main;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.ScriptComponent;
import org.katia.game.Game;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.net.URL;

public class LuaScriptExecutioner {

    Game game;
    Scene scene;
    LuaConsole console;

    /**
     * Script executioner constructor.
     * @param game Game instance.
     */
    public LuaScriptExecutioner(Game game) {
        Logger.log(Logger.Type.INFO, "Creating script executioner ...");
        this.game = game;
        this.scene = null;
        this.console = new LuaConsole();
    }

    /**
     * Call all init methods for all game objects in provided scene.
     * @param scene Scene.
     */
    public void init(Scene scene) {
        Logger.log(Logger.Type.INFO, "Run script executioner init method!");
        this.scene = scene;
        bindClasses(scene);
        executeInit(this.scene.getRootGameObject());
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
        Logger.log(Logger.Type.INFO, "Script executioner calling init method for:", gameObject.getName());
        var component = gameObject.getComponent(ScriptComponent.class);

        if (component != null && component.getPath() != null) {
            component.setBehaviourTable(scene.getGlobals().loadfile(component.getPath()).call());
            LuaTable params = new LuaTable();
            params.set("gameObject", CoerceJavaToLua.coerce(gameObject));
            params.set("scene", CoerceJavaToLua.coerce(scene));
            component.getBehaviourTable().get("init").call(component.getBehaviourTable(), params);
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
        var component = gameObject.getComponent(ScriptComponent.class);
        if (component != null && component.getBehaviourTable() != null) {
            component.getBehaviourTable()
                    .get("update")
                    .call(component.getBehaviourTable(), LuaValue.valueOf(dt));
        }
        for (int i = gameObject.getChildren().size() - 1; i >= 0 ; i--) {
            executeUpdate(gameObject.getChildren().get(i), dt);
        }
    }

    /**
     * Bind java classes to current scene context.
     * @param scene Scene.
     */
    private void bindClasses(Scene scene) {
        URL resource = Main.class.getClassLoader().getResource("scripts/classes.lua");

        Logger.log(Logger.Type.INFO, "Bind java classes to lua globals for scene:", scene.getName());
        scene.getGlobals().loadfile("./Katia-Core/src/main/resources/scripts/classes.lua").call();
        String existingPath = scene.getGlobals().get("package").get("path").tojstring();
        scene.getGlobals()
                .get("package")
                .set("path", LuaValue.valueOf("./Katia-Core/src/main/resources/scripts/" + existingPath));

        scene.getGlobals().set("InputManager", CoerceJavaToLua.coerce(game.getInputManager()));
        scene.getGlobals().set("SceneManager", CoerceJavaToLua.coerce(game.getSceneManager()));
        scene.getGlobals().set("print", this.console);
    }
}
