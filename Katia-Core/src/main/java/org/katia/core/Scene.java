package org.katia.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.katia.Logger;
import org.katia.factory.GameObjectFactory;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * Game scene representation class.
 */
@Data
@JsonDeserialize
@NoArgsConstructor
public class Scene {

    private String name;
    private GameObject rootGameObject;

    @JsonIgnore
    private Globals globals;

    /**
     * Constructor.
     * @param name Scene name.
     */
    public Scene(String name) {
        this.name = name;
        this.rootGameObject = GameObjectFactory.createGameObject("Root");
        this.globals = JsePlatform.standardGlobals();
    }

    /**
     * Add game object to scene.
     * @param gameObject Game Object.
     */
    public void addGameObject(GameObject gameObject) {
        this.rootGameObject.addChild(gameObject);
    }

    /**
     * Add game object at index.
     * @param gameObject Game Object.
     * @param index Index.
     */
    public void addGameObject(GameObject gameObject, int index) {
        this.rootGameObject.addChild(gameObject, index);
    }

    /**
     * Remove game object from scene.
     * @param gameObject Game Object.
     */
    public void removeGameObject(GameObject gameObject) {
        this.rootGameObject.removeChild(gameObject);
    }

    /**
     * Find child game object.
     * @param name Name.
     * @return GameObject
     */
    public GameObject find(String name) {
        return rootGameObject.find(name);
    }

    /**
     * Find game object with provided select ID.
     * @param id Select ID.
     * @return GameObject
     */
    public GameObject findBySelectID(int id) {
        return rootGameObject.findBySelectID(id);
    }

    /**
     * Dispose of game scene.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing og scene:", name);
        rootGameObject.dispose();
    }
}
