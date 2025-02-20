package org.katia.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.core.components.ScriptComponent;
import org.katia.core.components.SpriteComponent;
import org.katia.core.components.TextComponent;
import org.katia.core.components.TransformComponent;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

public class GameObjectFactory {

    private static ObjectMapper objectMapper;

    public static void initialize() {
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

    public static GameObject copy(GameObject gameObject) {
        GameObject copy = generateGameObjectFromJson(generateJsonFromGameObject(gameObject));
        setUUID(copy);
        return copy;
    }

    private static void setUUID(GameObject gameObject) {
        gameObject.setId(UUID.randomUUID());
        for (var child : gameObject.getChildren()) {
            setUUID(child);
        }
    }

    /**
     * Create game object with provided component.
     * @param component Component type.
     * @return GameObject
     */
    public static GameObject createGameObjectWithComponent(String component) {
        GameObject gameObject = GameObjectFactory.createGameObject();
        gameObject.addComponent(Objects.requireNonNull(ComponentFactory.createComponent(component)));
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
        } catch (JsonProcessingException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return gameObject;
    }

    public static void reconstructGameObject(Scene scene, GameObject gameObject) {
        for (Class<?> component : gameObject.getComponents().keySet()) {
            if (component == ScriptComponent.class) {
//                ScriptComponent scriptComponent = gameObject.getComponent(ScriptComponent.class);
//                if (scriptComponent.getPath() != null) {
//                    scriptComponent.addScriptFile(scene, scriptComponent.getPath());
//                }
            }

            if (component == TextComponent.class) {
                TextComponent textComponent = gameObject.getComponent(TextComponent.class);
                if (textComponent.getFontPath() != null && !textComponent.getFontPath().isEmpty())
                textComponent.setFont(Objects.requireNonNull(FontFactory.createFont(textComponent.getFontPath(), 72, 512, 512)));
            }

            if (component == SpriteComponent.class) {
                SpriteComponent spriteComponent = gameObject.getComponent(SpriteComponent.class);
                if (spriteComponent.getPath() != null) {
                    spriteComponent.setTexture(spriteComponent.getPath());
                }
            }
        }

        for (GameObject child : gameObject.getChildren()) {
            child.setParent(gameObject);
            reconstructGameObject(scene, child);
        }
    }
}
