package org.katia.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.CameraComponent;
import org.katia.core.components.TransformComponent;
import org.katia.managers.InputManager;
import org.katia.managers.LuaConsole;
import org.katia.managers.SceneManager;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public abstract class SceneFactory {

    private static ObjectMapper objectMapper;

    /**
     * Initialize scene factory.
     */
    public static void initialize() {
        Logger.log(Logger.Type.INFO, "Initialize scene factory!");
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Create scene.
     * @param name Scene name.
     * @return Scene
     */
    public static Scene createScene(String name, int width, int height) {
        Scene scene = new Scene(name, width, height);
        addCustomScriptPath(scene);

        GameObject mainCameraGameObject = GameObjectFactory.createGameObject("Main Camera");
        mainCameraGameObject.addComponent(new CameraComponent());
        mainCameraGameObject.getComponent(TransformComponent.class).setPosition(new Vector3f(0, 0, 0));
        mainCameraGameObject.getComponent(TransformComponent.class).setScale(new Vector3f(1, 1, 0));
        mainCameraGameObject.getComponent(CameraComponent.class).setViewport(new Vector2f(width, height));
        scene.addGameObject(mainCameraGameObject);

        Logger.log(Logger.Type.SUCCESS, "Scene created:", name);
        return scene;
    }

    /**
     * Generate json from scene.
     * @param scene Scene.
     * @return String
     */
    public static String generateJsonFromScene(Scene scene) {
        Logger.log(Logger.Type.INFO, "Generate json from scene:", scene.getName());
        String json = null;
        try {
            json = objectMapper.writeValueAsString(scene);
            Logger.log(Logger.Type.SUCCESS, "Json scene generated!");
        } catch (JsonProcessingException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return json;
    }

    /**
     * Generate scene from json.
     * @param json json string.
     * @return Scene
     */
    public static Scene generateSceneFromJson(String json) {
        Logger.log(Logger.Type.INFO, "Generate scene from json:");
        try {
            final Scene scene = objectMapper.readValue(json, Scene.class);
            addCustomScriptPath(scene);
            GameObjectFactory.reconstructGameObject(scene, scene.getRootGameObject());
            Logger.log(Logger.Type.SUCCESS, "Scene generated:", scene.getName());
            return scene;
        } catch (JsonProcessingException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return null;
    }

    /**
     * Setup lua context with custom libraries and lua binds.
     * @param scene Scene.
     */
    private static void addCustomScriptPath(Scene scene) {
        scene.setGlobals(JsePlatform.standardGlobals());
        scene.getGlobals().loadfile("./Katia-Core/src/main/resources/scripts/classes.lua").call();
        String customScriptsPath = "./Katia-Core/src/main/resources/scripts/";
        String existingPath = scene.getGlobals().get("package").get("path").tojstring();
        scene.getGlobals().get("package").set("path", LuaValue.valueOf(customScriptsPath + existingPath));
        scene.getGlobals().set("InputManager", CoerceJavaToLua.coerce(InputManager.getInstance()));
        scene.getGlobals().set("SceneManager", CoerceJavaToLua.coerce(SceneManager.getInstance()));
        scene.getGlobals().set("print", LuaConsole.getInstance());
    }
}
