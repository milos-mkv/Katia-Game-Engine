package org.katia.scripting;

import org.katia.core.GameObject;
import org.katia.factory.GameObjectFactory;

public class LuaGameObject {

    public GameObject create(GameObject gameObject) {
        return GameObjectFactory.copy(gameObject);
    }
}
