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
import org.katia.core.components.*;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * This class is responsible for creating game scenes.
 */
public abstract class SceneFactory {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static Scene createScene(String name, int width, int height) {
        return createScene(name, width, height, true);
    }

    public static Scene createScene(String name) {
        return createScene(name, 0, 0, false);
    }

    /**
     * Create scene.
     * @param name Scene name.
     * @param width Main camera viewport width.
     * @param height Main camera viewport height.
     * @return Scene
     */
    public static Scene createScene(String name, int width, int height, boolean createCamera) {
        Scene scene = new Scene(name);
        if (createCamera) {
            GameObject mainCameraGameObject = GameObjectFactory.createGameObject("Main Camera");
            mainCameraGameObject.addComponent(new CameraComponent());
            mainCameraGameObject.getComponent(TransformComponent.class).setPosition(new Vector3f(0, 0, 0));
            mainCameraGameObject.getComponent(TransformComponent.class).setScale(new Vector3f(1, 1, 0));
            mainCameraGameObject.getComponent(CameraComponent.class).setViewport(new Vector2f(width, height));

            scene.addGameObject(mainCameraGameObject);
        }
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
            scene.setGlobals(JsePlatform.standardGlobals());
            GameObjectFactory.reconstructGameObject(scene.getRootGameObject());

            Logger.log(Logger.Type.SUCCESS, "Scene generated:", scene.getName());
            return scene;
        } catch (JsonProcessingException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return null;
    }
}
