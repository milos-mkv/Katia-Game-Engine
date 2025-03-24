package org.katia.scripting;

import lombok.Data;
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
import java.util.UUID;

/**
 * This class is responsible for executing lua vm for current running game scene in game instance.
 */
@Data
public class LuaScriptExecutioner {

    private final Game game;

    LuaConsole console;

    /**
     * Lua Script Executioner constructor.
     * @param game Game instance.
     */
    public LuaScriptExecutioner(Game game) {
        Logger.log(Logger.Type.INFO, "Creating lua script executioner for game:", game.getDirectory());
        this.game = game;
        this.console = new LuaConsole();
    }

    /**
     * Call all init methods for all game objects in current active scene.
     */
    public void init() {
        Logger.log(Logger.Type.INFO, "Run script executioner init method!");
        Scene scene = game.getSceneManager().getActiveScene();
        bindClasses();
        executeInit(scene.getRootGameObject());
    }

    /**
     * Call all update methods for all game objects in current active scene.
     * @param dt Delta time.
     */
    public void update(float dt) {
        Scene scene = game.getSceneManager().getActiveScene();
        executeUpdate(scene.getRootGameObject(), dt);
    }

    /**
     * Call init method for provided game object.
     * @param gameObject Game Object.
     */
    private void executeInit(GameObject gameObject) {
        Logger.log(Logger.Type.INFO, "Script executioner calling init method for:", gameObject.getName());
        var scene = game.getSceneManager().getActiveScene();
        var component = gameObject.getComponent(ScriptComponent.class);

        if (component != null && component.getPath() != null) {
            String scriptFile = game.getResourceManager().getScript(component.getPath());
            component.setBehaviourTable(scene.getGlobals().loadfile(scriptFile).call());
            LuaTable params = new LuaTable();
            params.set("gameObject", CoerceJavaToLua.coerce(gameObject));
            params.set("scene", CoerceJavaToLua.coerce(scene));

            for (var key : component.getParams()) {
                try {
                    params.set(key.getKey(), CoerceJavaToLua.coerce(scene.find(UUID.fromString(key.getValue()))));
                } catch (IllegalArgumentException e) {
                    params.set(key.getKey(), CoerceJavaToLua.coerce(key.getValue()));
                }
            }

            component.getBehaviourTable().get("init").call(component.getBehaviourTable(), params);
        }

        for (int i = gameObject.getChildren().size() - 1; i >= 0 ; i--) {
            executeInit(gameObject.getChildren().get(i));
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
     * Bind java classes to current active scene.
     */
    private void bindClasses() {
        Scene scene = game.getSceneManager().getActiveScene();
        URL classesLua = Main.class.getClassLoader().getResource("scripts/classes.lua");
        URL scriptsPath = Main.class.getClassLoader().getResource("scripts/");

        Logger.log(Logger.Type.INFO, "Bind java classes to lua globals for scene:", scene.getName());
        scene.getGlobals().loadfile(classesLua.getPath()).call();
        String existingPath = scene.getGlobals().get("package").get("path").tojstring();
        scene.getGlobals()
                .get("package")
                .set("path", LuaValue.valueOf(scriptsPath.getPath() + existingPath));

        scene.getGlobals().set("Input", CoerceJavaToLua.coerce(game.getInputManager()));
        scene.getGlobals().set("SceneManager", CoerceJavaToLua.coerce(game.getSceneManager()));
        scene.getGlobals().set("Window", CoerceJavaToLua.coerce(game.getWindow()));
        scene.getGlobals().set("AudioManager", CoerceJavaToLua.coerce(game.getAudioManager()));
        scene.getGlobals().set("GameObject", CoerceJavaToLua.coerce(new LuaGameObject(game)));

        scene.getGlobals().set("print", this.console);
    }
}
