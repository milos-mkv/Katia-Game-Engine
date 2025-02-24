package org.katia.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.components.TransformComponent;

import java.util.Objects;
import java.util.UUID;

/**
 * This class is responsible for creation and abstract manipulation of Game Objects.
 */
public class GameObjectFactory {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Create game object.
     * @return GameObject
     */
    public static GameObject createGameObject() {
        return createGameObject("Game Object");
    }

    /**
     * Create game object.
     * @param name Game Object name.
     * @return GameObject
     */
    public static GameObject createGameObject(String name) {
        GameObject gameObject = new GameObject(UUID.randomUUID(), name);
        gameObject.addComponent(new TransformComponent());
        return gameObject;
    }

    /**
     * Copy existing game object.
     * @param gameObject GameObject to copy.
     * @return GameObject
     */
    public static GameObject copy(GameObject gameObject) {
        GameObject copy = generateGameObjectFromJson(generateJsonFromGameObject(gameObject));
        setUUID(copy);
        return null;
    }

    /**
     * Create game object with provided component.
     * @param component Component type.
     * @return GameObject
     */
    public static GameObject createGameObjectWithComponent(String component) {
        GameObject gameObject = GameObjectFactory.createGameObject();
        gameObject.addComponent(Objects.requireNonNull(ComponentFactory.createComponent(component)));
        gameObject.setName(component);
        return gameObject;
    }

    /**
     * Generate json from game object.
     * @param gameObject Game Object.
     * @return String
     */
    public static String generateJsonFromGameObject(GameObject gameObject) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(gameObject);
        } catch (JsonProcessingException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return json;
    }

    /**
     * Generate game object from json.
     * @param json Json string.
     * @return GameObject
     */
    public static GameObject generateGameObjectFromJson(String json) {
        GameObject gameObject = null;
        try {
            gameObject = objectMapper.readValue(json, GameObject.class);
            reconstructGameObject(gameObject);
        } catch (JsonProcessingException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return gameObject;
    }

    /**
     * Re parent child game object to this game object. Used when reconstructing game object from json.
     * @param gameObject GameObject.
     */
    public static void reconstructGameObject(GameObject gameObject) {
        gameObject.setSelectID(++GameObject.TotalID);
        for (GameObject child : gameObject.getChildren()) {
            child.setParent(gameObject);
            reconstructGameObject(child);
        }
    }

    /**
     * Set new UUID to game object and his children. Used when copying game object.
     * @param gameObject GameObject.
     */
    private static void setUUID(GameObject gameObject) {
        gameObject.setId(UUID.randomUUID());
        for (var child : gameObject.getChildren()) {
            setUUID(child);
        }
    }
}
