package org.katia.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Vector2i;
import org.katia.factory.GameObjectFactory;
import org.katia.gfx.FrameBuffer;
import org.luaj.vm2.Globals;

@Data
@JsonDeserialize
@NoArgsConstructor
public class Scene {

    private String name;
    private GameObject rootGameObject;
    private Vector2i size;

    @JsonIgnore
    private Globals globals;

    /**
     * Constructor.
     * @param name Scene name.
     */
    public Scene(String name) {
        this.name = name;
        this.size = new Vector2i(0, 0);
        this.rootGameObject = GameObjectFactory.createGameObject("Root");
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


    public GameObject findBySelectID(int id) {
        return rootGameObject.findBySelectID(id);

    }
}
