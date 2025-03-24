package org.katia.scripting;

import org.katia.FileSystem;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.katia.game.Game;

/**
 * This is wrapper class for GameObject class representation in lua.
 */
public class LuaGameObject {

    Game game;

    public LuaGameObject(Game game) {
        this.game = game;
    }

    /**
     * Create game object from provided game object.
     * @param gameObject Game Object.
     * @return GameObject
     */
    public GameObject create(GameObject gameObject) {
        var g = GameObjectFactory.copy(gameObject);
        g.setFromPrefab(true);
        return g;
    }

    /**
     * Create game object from provided prefab key.
     * @param prefab Prefab key.
     * @return GameObject
     */
    public GameObject create(String prefab) {
        Scene scene = SceneFactory.generateSceneFromJson(
                FileSystem.readFromFile(game.getResourceManager().getPrefab(prefab))
        );
        assert scene != null;
        return GameObjectFactory.copy(scene.getRootGameObject().getChildren().getFirst());
    }
}
